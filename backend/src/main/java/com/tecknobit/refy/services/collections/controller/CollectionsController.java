package com.tecknobit.refy.services.collections.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.shared.controllers.DefaultRefyController;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinoxcore.pagination.PaginatedResponse.*;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.INSTANCE;

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
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param ownedOnly Whether to get only the collections where the user is the owner
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
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
            @RequestParam(name = OWNED_ONLY_KEY) boolean ownedOnly,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize,
            @RequestParam(name = KEYWORDS_KEY, defaultValue = "", required = false) Set<String> keywords
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        PaginatedResponse<LinksCollection> collections;
        if(ownedOnly)
            collections = linksCollectionsService.getUserOwnedCollections(userId, page, pageSize);
        else
            collections = linksCollectionsService.getAllUserCollections(userId, page, pageSize, keywords);
        return (T) successResponse(collections);
    }

    /**
     * Method to edit a collection
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
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
        HashSet<String> userLinks = linksService.getUserLinks(userId);
        if (!userLinks.containsAll(links) || !INSTANCE.isCollectionPayloadValid(color, title, description))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        linksCollectionsService.editCollection(userId, collectionId, color, title, description, links);
        return successResponse();
    }

    /**
     * Method to manage the links shared with the collection
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
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
    public String attachLinksToCollection(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        ArrayList<String> links = jsonHelper.fetchList(LINKS_KEY, new ArrayList<>());
        HashSet<String> userLinks = linksService.getUserLinks(userId);
        if (!userLinks.containsAll(links))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        linksCollectionsService.attachLinksToCollection(collectionId, links);
        return successResponse();
    }

    /**
     * Method to manage the teams where the collection is shared
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
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
    public String shareCollectionWithTeams(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        HashSet<String> userTeams = teamsService.getUserTeams(userId);
        List<String> teams = jsonHelper.fetchList(TEAMS_KEY, new ArrayList<>());
        if (!userTeams.containsAll(teams))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        linksCollectionsService.shareCollectionWithTeams(collectionId, teams);
        return successResponse();
    }

    /**
     * Method to get a collection
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param collectionId The identifier of the collection to get
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
     * Method to get the list of the teams where the collection is shared
     *
     * @param token        The token of the user
     * @param userId       The identifier of the user
     * @param collectionId The identifier of the collection
     * @param page         The page requested
     * @param pageSize     The size of the items to insert in the page
     * @return the teams list, if authorized, else failed message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + TEAMS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/teams", method = GET)
    public <T> T getCollectionTeams(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize
    ) {
        if (isUserNotAuthorized(userId, token, collectionId))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(linksCollectionsService.getCollectionTeams(collectionId, page, pageSize));
    }

    /**
     * Method to get the list of the links shared with the collection
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param collectionId The identifier of the collection
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     *
     * @return the links list, if authorized, else failed message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + LINKS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/links", method = GET)
    public <T> T getCollectionLinks(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize,
            @RequestParam(name = KEYWORDS_KEY, defaultValue = "", required = false) Set<String> keywords
    ) {
        if (isUserNotAuthorized(userId, token, collectionId))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(linksCollectionsService.getCollectionLinks(collectionId, page, pageSize, keywords));
    }

    /**
     * Method to remove a link from a collection
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param collectionId The identifier of the collection
     * @param linkId The identifier of the link
     *
     * @return the response of the request as {@link String}
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + LINKS_KEY + "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/links/{link_id}", method = DELETE)
    public String removeLinkFromCollection(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        if (isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!userItem.getLinkIds().contains(linkId))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        linksCollectionsService.removeLinkFromCollection(collectionId, linkId);
        return successResponse();
    }

    /**
     * Method to remove a team from a collection
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param collectionId The identifier of the collection
     * @param teamId The identifier of the team
     *
     * @return the response of the request as {@link String}
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + TEAMS_KEY + "/{" + TEAM_IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/teams/{team_id}", method = DELETE)
    public String removeTeamFromCollection(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        if (isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if (!userItem.isUserAllowedToRemoveTeam(userId, teamId))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        linksCollectionsService.removeTeamFromCollection(collectionId, teamId);
        return successResponse();
    }

    /**
     * Method to delete a collection
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param collectionId The identifier of the collection to delete
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
        linksCollectionsService.deleteCollection(collectionId);
        return successResponse();
    }

    /**
     * Method to get whether the user is or not authorized to operate with the collection requested
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param collectionId The identifier of the collection requested
     * @return whether the user is or not authorized to operate with the collection requested
     */
    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String collectionId) {
        userItem = linksCollectionsService.getItemIfAllowed(userId, collectionId);
        return !isMe(userId, token) || userItem == null;
    }

}
