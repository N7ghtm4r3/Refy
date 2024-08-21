package com.tecknobit.refy.controllers.links;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.refy.controllers.DefaultRefyController;
import com.tecknobit.refycore.records.links.RefyLink;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isLinkPayloadValid;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyItem.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.RefyItem.OWNED_ONLY_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.REFERENCE_LINK_KEY;

/**
 * The {@code LinksController} class is useful to manage all the {@link RefyLink} operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see DefaultRefyController
 *
 */
@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + LINKS_KEY)
public class LinksController extends DefaultRefyController<RefyLink> {

    /**
     * Method to get a list of links
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param ownedOnly: whether to get only the links where the user is the owner
     *
     * @return the links list, if authorized, else failed message as {@link T}
     *
     * @param <T> the {@link RefyLink} type
     */
    @GetMapping(
            headers = TOKEN_KEY
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/links", method = GET)
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestParam(name = OWNED_ONLY_KEY) boolean ownedOnly
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        List<RefyLink> links;
        if(ownedOnly)
            links = linksHelper.getUserOwnedLinks(userId);
        else
            links = linksHelper.getAllUserLinks(userId);
        return (T) successResponse(links);
    }

    /**
     * Method to create a new link
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "reference_link" : "the url of the link" -> [String],
     *                                  "description" : "the description of the link" -> [String]
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PostMapping(
            headers = TOKEN_KEY
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/links", method = POST)
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, Object> payload
    ) {
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        String referenceLink = jsonHelper.getString(REFERENCE_LINK_KEY);
        if(!isLinkPayloadValid(description, referenceLink))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            linksHelper.createLink(userId, generateIdentifier(), getLinkTitle(referenceLink), description, referenceLink);
            return successResponse();
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * Method to get from the reference link the real title from the related web page
     *
     * @param referenceLink: the reference link from fetch its title
     * @return the title of the reference link as {@link String}
     * @throws IOException when an error occurred during the scraping of the title
     */
    private String getLinkTitle(String referenceLink) throws IOException {
        return Jsoup.connect(referenceLink).get().title();
    }

    /**
     * Method to edit an existing link
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "reference_link" : "the url of the link" -> [String],
     *                                  "description" : "the description of the link" -> [String]
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PatchMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}", method = PATCH)
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        String referenceLink = jsonHelper.getString(REFERENCE_LINK_KEY);
        if(!isLinkPayloadValid(description, referenceLink))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            String title = userItem.getTitle();
            if(!userItem.getReferenceLink().equals(referenceLink))
                title = getLinkTitle(referenceLink);
            linksHelper.editLink(userId, linkId, title, description, referenceLink);
            return successResponse();
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * Method to manage the collections where the link is shared
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "collections" : ["the current link collections"] -> List[String],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}/collections", method = PUT)
    public String manageLinkCollections(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return editAttachmentsList(payload, COLLECTIONS_KEY, new AttachmentsManagement() {

            @Override
            public HashSet<String> getUserAttachments() {
                return collectionsHelper.getUserCollections(userId);
            }

            @Override
            public List<String> getAttachmentsIds() {
                return userItem.getCollectionsIds();
            }

            @Override
            public void execute(List<String> collections) {
                linksHelper.manageLinkCollections(linkId, collections);
            }

        });
    }

    /**
     * Method to manage the teams where the link is shared
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "teams" : ["the current link teams"] -> List[String],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}/" + TEAMS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}/teams", method = PUT)
    public String manageLinkTeams(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return editAttachmentsList(payload, TEAMS_KEY, new AttachmentsManagement() {

            @Override
            public HashSet<String> getUserAttachments() {
                return teamsHelper.getUserTeams(userId);
            }

            @Override
            public List<String> getAttachmentsIds() {
                return userItem.getTeamIds();
            }

            @Override
            public void execute(List<String> teams) {
                linksHelper.manageLinkTeams(linkId, teams);
            }

        });
    }

    /**
     * Method to delete a link
     * @param token: the token of the user
     * @param userId:    the identifier of the user
     * @param linkId: the identifier of the link to delete
     * @return the response message as {@link String}
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}", method = DELETE)
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        if(userIsNotTheItemOwner(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        linksHelper.deleteLink(linkId);
        return successResponse();
    }

    /**
     * Method to get whether the user is or not authorized to operate with the link requested
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param linkId: the identifier of the link requested
     * @return whether the user is or not authorized to operate with the link requested
     */
    protected boolean isUserNotAuthorized(String userId, String token, String linkId) {
        userItem = linksHelper.getItemIfAllowed(userId, linkId);
        return !isMe(userId, token) || userItem == null;
    }

}
