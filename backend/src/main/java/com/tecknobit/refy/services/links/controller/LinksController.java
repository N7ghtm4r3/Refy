package com.tecknobit.refy.services.links.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.controllers.DefaultRefyController;
import kotlin.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinoxcore.pagination.PaginatedResponse.*;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.INSTANCE;

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
     * {@code OG_IMAGE_METADATA} constant value to extract from an HTML page the {@code og:image} metadata property
     */
    private static final String OG_IMAGE_METADATA = "meta[property=og:image]";

    /**
     * {@code CONTENT_KEY} constant value for the {@code content} value
     */
    private static final String CONTENT_KEY = "content";

    /**
     * Method to get a list of links
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param ownedOnly Whether to get only the links where the user is the owner
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
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
            @RequestParam(name = OWNED_ONLY_KEY, defaultValue = "false", required = false) boolean ownedOnly,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize,
            @RequestParam(name = KEYWORDS_KEY, defaultValue = "", required = false) Set<String> keywords
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        PaginatedResponse<RefyLink> links;
        if(ownedOnly)
            links = linksService.getUserOwnedLinks(userId, page, pageSize);
        else
            links = linksService.getAllUserLinks(userId, page, pageSize, keywords);
        return (T) successResponse(links);
    }

    /**
     * Method to create a new link
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
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
        if(!INSTANCE.isLinkPayloadValid(description, referenceLink))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            Pair<String, String> metadata = getMetadata(referenceLink);
            linksService.createLink(userId, generateIdentifier(), metadata.getFirst(), metadata.getSecond(), description,
                    referenceLink);
            return successResponse();
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * Method to edit an existing link
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
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
        if(!INSTANCE.isLinkPayloadValid(description, referenceLink))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            String title = userItem.getTitle();
            String thumbnailPreview = userItem.getLinkThumbnailPreview();
            if(!userItem.getReferenceLink().equals(referenceLink)) {
                Pair<String, String> metadata = getMetadata(referenceLink);
                title = metadata.getFirst();
                thumbnailPreview = metadata.getSecond();
            }
            linksService.editLink(userId, linkId, title, thumbnailPreview, description, referenceLink);
            return successResponse();
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * Method to extract from the reference link the metadata information such website title and {@link #OG_IMAGE_METADATA}
     * property
     *
     * @param referenceLink The link used to extract the metadata
     *
     * @return the metadata information as {@link Pair} of {@link String}
     */
    private Pair<String, String> getMetadata(String referenceLink) throws IOException {
        Document document = Jsoup.connect(referenceLink)
                .get();
        String title = document.title();
        String thumbnailPreview = document.select(OG_IMAGE_METADATA).attr(CONTENT_KEY);
        return new Pair<>(title, thumbnailPreview);
    }

    /**
     * Method to get a team
     *
     * @param token  The token of the user
     * @param userId The identifier of the user
     * @param linkId The identifier of the team to get
     * @return the team requested, if authorized, or the failed response message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}", method = GET)
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        if (!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        RefyLink link = linksService.getItemIfAllowed(userId, linkId);
        if (link == null)
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        return (T) successResponse(link);
    }

    /**
     * Method to manage the collections where the link is shared
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "collections" : ["the collections where share the link"] -> List[String],
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
    public String shareLinkWithCollections(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        linksService.shareLinkWithCollections(userId, linkId, jsonHelper.fetchList(COLLECTIONS_KEY));
        return successResponse();
    }

    /**
     * Method to manage the teams where the link is shared
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
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
    public String shareLinkWithTeams(
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
                return teamsService.getUserTeams(userId);
            }

            @Override
            public List<String> getAttachmentsIds() {
                return userItem.getTeamIds();
            }

            @Override
            public void execute(List<String> teams) {
                //linksService.shareLinkWithTeams(linkId, teams);
            }

        });
    }

    /**
     * Method to delete a link
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param linkId The identifier of the link to delete
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
        linksService.deleteLink(linkId);
        return successResponse();
    }

    /**
     * Method to get whether the user is or not authorized to operate with the link requested
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param linkId The identifier of the link requested
     * @return whether the user is or not authorized to operate with the link requested
     */
    protected boolean isUserNotAuthorized(String userId, String token, String linkId) {
        userItem = linksService.getItemIfAllowed(userId, linkId);
        return !isMe(userId, token) || userItem == null;
    }

}
