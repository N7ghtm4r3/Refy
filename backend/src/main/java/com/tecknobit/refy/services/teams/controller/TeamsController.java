package com.tecknobit.refy.services.teams.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.shared.controllers.DefaultRefyController;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.service.TeamsService.TeamPayload;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refy.services.users.service.RefyUsersService;
import com.tecknobit.refycore.enums.TeamRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinoxcore.pagination.PaginatedResponse.*;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.enums.TeamRole.ADMIN;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.CHANGE_MEMBER_ROLE_ENDPOINT;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.LEAVE_ENDPOINT;

/**
 * The {@code TeamsController} class is useful to manage all the {@link Team} operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see DefaultRefyController
 *
 */
@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + TEAMS_KEY)
public class TeamsController extends DefaultRefyController<Team> {

    /**
     * {@code refyUsersService} helper to manage the {@link RefyUser} database operations
     */
    @Autowired
    private RefyUsersService refyUsersService;

    /**
     * Method to get a list of teams
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param ownedOnly Whether to get only the teams where the user is the owner
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     *
     * @return the teams list, if authorized, else failed message as {@link T}
     *
     * @param <T> the {@link Team} type
     */
    @GetMapping(
            headers = TOKEN_KEY
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/teams", method = GET)
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
        PaginatedResponse<Team> teams;
        if(ownedOnly)
            teams = teamsService.getUserOwnedTeams(userId, page, pageSize);
        else
            teams = teamsService.getAllUserTeams(userId, page, pageSize, keywords);
        return (T) successResponse(teams);
    }

    /**
     * Method to get the potential members to add in a team
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/" + MEMBERS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/members", method = GET)
    public <T> T listPotentialMembers(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(refyUsersService.getPotentialMembers(userId, page, pageSize));
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote see the {@link #create(String, String, TeamPayload)} method instead
     */
    @Override
    public String create(String token, String userId, Map<String, Object> payload) {
        return null;
    }

    /**
     * Method to create a new team
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request formatted by SpringBoot as {@link TeamPayload}
     *
     * @return the response of the request as {@link String}
     *
     */
    @PostMapping(
            headers = TOKEN_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams", method = POST)
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @ModelAttribute TeamPayload payload
    ) {
        if(!isMe(userId, token) || !payload.isValidTeamPayload(true))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        try {
            teamsService.createTeam(userId, generateIdentifier(), payload);
            return successResponse();
        } catch (Exception e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote see the {@link #edit(String, String, String, TeamPayload)} method instead
     */
    @Override
    public String edit(String token, String userId, String itemId, Map<String, Object> payload) {
        return null;
    }

    /**
     * Method to edit a team
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param teamId The identifier of the team to edit
     * @param payload The payload of the request formatted by SpringBoot as {@link TeamPayload}
     *
     * @return the response of the request as {@link String}
     *
     */
    @PostMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = POST)
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @ModelAttribute TeamPayload payload
    ) {
        if (isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId) ||
                !payload.isValidTeamPayload(false)) {
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        }
        try {
            teamsService.editTeam(userId, userItem, payload);
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
        return successResponse();
    }

    /**
     * Method to share the links with the team
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "links" : ["the links to share with the team"] -> List[String],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + LINKS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/links", method = PUT)
    public String shareLinksWithTeam(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        HashSet<String> userLinks = linksService.getUserLinks(userId);
        List<String> links = jsonHelper.fetchList(LINKS_KEY, new ArrayList<>());
        ArrayList<String> linksSharedByTheUser = new ArrayList<>(links);
        linksSharedByTheUser.removeAll(userItem.getLinkIds());
        if (!userLinks.containsAll(linksSharedByTheUser))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        teamsService.shareLinksWithTeam(userId, teamId, links);
        return successResponse();
    }

    /**
     * Method to share the collections with the team
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "collections" : ["the collections to share with the team"] -> List[String],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/collections", method = PUT)
    public String shareCollectionsWithTeam(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        HashSet<String> userCollections = linksCollectionsService.getUserCollections(userId);
        List<String> collections = jsonHelper.fetchList(COLLECTIONS_KEY, new ArrayList<>());
        ArrayList<String> collectionsSharedByTheUser = new ArrayList<>(collections);
        collectionsSharedByTheUser.removeAll(userItem.getCollectionsIds());
        if (!userCollections.containsAll(collectionsSharedByTheUser))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        teamsService.shareCollectionsWithTeam(userId, teamId, collections);
        return successResponse();
    }

    /**
     * Method to get a team
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param teamId The identifier of the team to get
     *
     * @return the team requested, if authorized, or the failed response message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = GET)
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        return super.getItem(token, userId, teamId);
    }

    /**
     * Method to get the list of the collections of a team
     *
     * @param token    The token of the user
     * @param userId   The identifier of the user
     * @param teamId   The identifier of the team
     * @param page     The page requested
     * @param pageSize The size of the items to insert in the page
     * @return the collections list, if authorized, else failed message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/collections", method = GET)
    public <T> T getTeamCollections(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize
    ) {
        if (isUserNotAuthorized(userId, token, teamId))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(teamsService.getTeamCollections(teamId, page, pageSize));
    }

    /**
     * Method to get the list of the links of a team
     *
     * @param token    The token of the user
     * @param userId   The identifier of the user
     * @param teamId   The identifier of the team
     * @param page     The page requested
     * @param pageSize The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     * @return the links list, if authorized, else failed message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + LINKS_KEY
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/links", method = GET)
    public <T> T getTeamLinks(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize,
            @RequestParam(name = KEYWORDS_KEY, defaultValue = "", required = false) Set<String> keywords
    ) {
        if (isUserNotAuthorized(userId, token, teamId))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(teamsService.getTeamLinks(teamId, page, pageSize, keywords));
    }

    /**
     * Method to remove a collection from a team
     *
     * @param token        The token of the user
     * @param userId       The identifier of the user
     * @param teamId       The identifier of the team
     * @param collectionId The identifier of the collection
     * @return the response of the request as {@link String}
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY + "/{" + COLLECTION_IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/collections/{collection_id}", method = DELETE)
    public String removeCollectionFromTeam(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId
    ) {
        if (isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId) ||
                !userItem.isTheCollectionOwner(userId, collectionId)) {
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        }
        teamsService.removeCollectionFromTeam(teamId, collectionId);
        return successResponse();
    }

    /**
     * Method to remove a link from a team
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param teamId The identifier of the team
     * @param linkId The identifier of the link
     *
     * @return the response of the request as {@link String}
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + LINKS_KEY + "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/links/{link_id}", method = DELETE)
    public String removeLinkFromTeam(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        if (isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId) ||
                !userItem.isTheLinkOwner(userId, linkId)) {
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        }
        teamsService.removeLinkFromTeam(teamId, linkId);
        return successResponse();
    }

    /**
     * Method to change the role of a member
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param teamId The identifier of the team where change the member role
     * @param memberId The identifier of the member to change its role
     * @param payload The payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "role" : ["the role to set"] -> [{@link TeamRole }],
     *                              }
     *                      }
     *                 </pre>
     *
     * @return the response of the request as {@link String}
     *
     */
    @PatchMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + MEMBERS_KEY + "/{" + MEMBER_IDENTIFIER_KEY + "}" + CHANGE_MEMBER_ROLE_ENDPOINT
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/members/{member_id}/updateRole", method = PATCH)
    public String changeMemberRole(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @PathVariable(MEMBER_IDENTIFIER_KEY) String memberId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId) || !userItem.hasMember(memberId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if(hierarchyIsNotRespected(userId, memberId))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        loadJsonHelper(payload);
        try {
            TeamRole role = TeamRole.valueOf(jsonHelper.getString(TEAM_ROLE_KEY));
            teamsService.changeMemberRole(teamId, memberId, role);
            return successResponse();
        } catch (IllegalArgumentException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    /**
     * Method to remove a member from a team
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param teamId The identifier of the team where remove the member
     * @param memberId The identifier of the member to remove
     *
     * @return the response of the request as {@link String}
     *
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + MEMBERS_KEY + "/{" + MEMBER_IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/members/{member_id}", method = DELETE)
    public String removeMember(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @PathVariable(MEMBER_IDENTIFIER_KEY) String memberId
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId) || !userItem.hasMember(memberId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if(hierarchyIsNotRespected(userId, memberId))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        teamsService.removeMember(teamId, memberId);
        return successResponse();
    }

    /**
     * Method to check whether the hierarchy of the members is respected in the request
     *
     * @param userId The identifier of the user
     * @param memberId The identifier of the member
     *
     * @return whether the hierarchy is respected as boolean
     */
    private boolean hierarchyIsNotRespected(String userId, String memberId) {
        return userId.equals(memberId) || userItem.isTheAuthor(memberId);
    }

    /**
     * Method to leave from a team
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param teamId The identifier of the team from leave
     *
     * @return the response of the request as {@link String}
     *
     * @apiNote the author/owner of the team cannot leave the group
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}" + LEAVE_ENDPOINT
    )
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/leave}", method = DELETE)
    public String leave(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || userItem.isTheAuthor(userId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if(userItem.isAdmin(userId)) {
            if(userItem.hasMembers()) {
                if(userItem.hasAdmins(userId))
                    teamsService.removeMember(teamId, userId);
                else {
                    String viewerId = userItem.getViewer().getId();
                    teamsService.changeMemberRole(teamId, viewerId, ADMIN);
                    teamsService.removeMember(teamId, userId);
                }
            } else
                teamsService.deleteTeam(teamId);
        } else
            teamsService.removeMember(teamId, userId);
        return successResponse();
    }

    /**
     * Method to delete a team
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param teamId The identifier of the team to delete
     *
     * @return the response of the request as {@link String}
     *
     * @apiNote only author/owner of the team can delete the group
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = DELETE)
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isTheAuthor(userId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        teamsService.deleteTeam(teamId);
        return successResponse();
    }

    /**
     * Method to get whether the user is or not authorized to operate with the team requested
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param teamId The identifier of the team requested
     * @return whether the user is or not authorized to operate with the team requested
     */
    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String teamId) {
        userItem = teamsService.getItemIfAllowed(userId, teamId);
        return !isMe(userId, token) || userItem == null;
    }
    
}
