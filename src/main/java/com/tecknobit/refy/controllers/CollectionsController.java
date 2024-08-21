package com.tecknobit.refy.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.refycore.records.LinksCollection;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isCollectionPayloadValid;
import static com.tecknobit.refycore.records.LinksCollection.*;
import static com.tecknobit.refycore.records.RefyItem.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.RefyItem.TITLE_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;

/**
 * The {@code CollectionsController} class is useful to manage all the {@link LinksCollection} operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see DefaultRefyController
 *
 */
@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY)
public class CollectionsController extends DefaultRefyController<LinksCollection> {

    /**
     * Method to get a list of collections
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param ownedOnly: whether to get only the collections where the user is the owner
     *
     * @return the collections list, if authorized, else failed message as {@link T}
     *
     * @param <T> the {@link LinksCollection} type
     */
    @GetMapping(
            headers = TOKEN_KEY
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/collections", method = GET)
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestParam(name = OWNED_ONLY_KEY) boolean ownedOnly
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        List<LinksCollection> collections;
        if(ownedOnly)
            collections = collectionsHelper.getUserOwnedCollections(userId);
        else
            collections = collectionsHelper.getAllUserCollections(userId);
        return (T) successResponse(collections);
    }

    /**
     * Method to create a new collection
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "title" : "title of the collection" -> [String],
     *                                  "description" : "the description of the collection" -> [String],
     *                                  "color" : "the color of the collection" -> [String],
     *                                  "links" : ["the links to attach"] -> List[String]
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
    @RequestPath(path = "/api/v1/users/{user_id}/collections", method = POST)
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, Object> payload
    ) {
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String color = jsonHelper.getString(COLLECTION_COLOR_KEY);
        String title = jsonHelper.getString(TITLE_KEY);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        ArrayList<String> links = jsonHelper.fetchList(LINKS_KEY, new ArrayList<>());
        HashSet<String> userLinks = linksHelper.getUserLinks(userId);
        if(!userLinks.containsAll(links) || !isCollectionPayloadValid(color, title, description, links))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        collectionsHelper.createCollection(userId, generateIdentifier(), color, title, description, links);
        return successResponse();
    }

    /**
     * Method to edit a collection
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "title" : "title of the collection" -> [String],
     *                                  "description" : "the description of the collection" -> [String],
     *                                  "color" : "the color of the collection" -> [String],
     *                                  "links" : ["the links to attach"] -> List[String]
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PatchMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = PATCH)
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(userIsNotTheItemOwner(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String color = jsonHelper.getString(COLLECTION_COLOR_KEY);
        String title = jsonHelper.getString(TITLE_KEY);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        ArrayList<String> links = jsonHelper.fetchList(LINKS_KEY, new ArrayList<>());
        if(!isCollectionPayloadValid(color, title, description, links))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        return editAttachmentsList(payload, LINKS_KEY, new AttachmentsManagement() {

            @Override
            public HashSet<String> getUserAttachments() {
                return linksHelper.getUserLinks(userId);
            }

            @Override
            public List<String> getAttachmentsIds() {
                return userItem.getLinkIds();
            }

            @Override
            public void execute(List<String> links) {
                collectionsHelper.editCollection(userId, collectionId, color, title, description, links);
            }

        });
    }

    /**
     * Method to manage the links shared with the collection
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "links" : ["the links of the collection"] -> List[String],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + LINKS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/links", method = PUT)
    public String manageCollectionLinks(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return editAttachmentsList(payload, false, LINKS_KEY,
                new AttachmentsManagement() {

                    @Override
                    public HashSet<String> getUserAttachments() {
                        return linksHelper.getUserLinks(userId);
                    }

                    @Override
                    public List<String> getAttachmentsIds() {
                        return userItem.getLinkIds();
                    }

                    @Override
                    public void execute(List<String> links) {
                        collectionsHelper.manageCollectionLinks(collectionId, links);
                    }

                }
        );
    }

    /**
     * Method to manage the teams where the collection is shared
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "teams" : ["the teams of the collection"] -> List[String],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + TEAMS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/teams", method = PUT)
    public String manageCollectionTeams(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
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
                collectionsHelper.manageCollectionTeams(collectionId, teams);
            }

        });
    }

    /**
     * Method to get a collection
     * @param token: the token of the user
     * @param userId:    the identifier of the user
     * @param collectionId: the identifier of the collection to get
     *
     * @return the collection requested, if authorized, or the failed response message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = GET)
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId
    ) {
        return super.getItem(token, userId, collectionId);
    }

    /**
     * Method to delete a collection
     * @param token: the token of the user
     * @param userId:    the identifier of the user
     * @param collectionId: the identifier of the collection to delete
     *
     * @return the response message as {@link String}
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = DELETE)
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId
    ) {
        if(userIsNotTheItemOwner(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        collectionsHelper.deleteCollection(collectionId);
        return successResponse();
    }

    /**
     * Method to get whether the user is or not authorized to operate with the collection requested
     *
     * @param userId:    the identifier of the user
     * @param token: the token of the user
     * @param collectionId: the identifier of the collection requested
     * @return whether the user is or not authorized to operate with the collection requested
     */
    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String collectionId) {
        userItem = collectionsHelper.getItemIfAllowed(userId, collectionId);
        return !isMe(userId, token) || userItem == null;
    }

}
