package com.tecknobit.refy.controllers;

import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refy.helpers.services.TeamsHelper.TeamPayload;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.LEAVE_ENDPOINT;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.UPDATE_MEMBER_ROLE_ENDPOINT;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyItem.OWNED_ONLY_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.Team.MEMBERS_KEY;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.MEMBER_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TEAM_ROLE_KEY;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.ADMIN;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + TEAMS_KEY)
public class TeamsController extends DefaultRefyController<Team> {

    @GetMapping(
            headers = TOKEN_KEY
    )
    @Override
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestParam(name = OWNED_ONLY_KEY) boolean ownedOnly
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        List<Team> teams;
        if(ownedOnly)
            teams = teamsHelper.getUserOwnedTeams(userId);
        else
            teams = teamsHelper.getAllUserTeams(userId);
        return (T) successResponse(teams);
    }

    @Override
    public String create(String token, String userId, Map<String, Object> payload) {
        return null;
    }

    @PostMapping(
            headers = TOKEN_KEY
    )
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @ModelAttribute TeamPayload payload
    ) {
        if(!isMe(userId, token) || !payload.isValidTeamPayload())
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        try {
            teamsHelper.createTeam(userId, generateIdentifier(), payload);
            return successResponse();
        } catch (Exception e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    @Override
    public String edit(String token, String userId, String itemId, Map<String, Object> payload) {
        return null;
    }

    @PostMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}"
    )
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @ModelAttribute TeamPayload payload
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId) || !payload.isValidTeamPayload())
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        try {
            teamsHelper.editTeam(userId, userItem, payload);
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
        return successResponse();
    }

    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + LINKS_KEY
    )
    public String manageLinkTeams(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
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
                teamsHelper.manageTeamLinks(teamId, links);
            }

        });
    }

    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY
    )
    public String manageTeamCollections(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isAdmin(userId))
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
                teamsHelper.manageTeamCollections(teamId, collections);
            }

        });
    }

    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}"
    )
    @Override
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        return super.getItem(token, userId, teamId);
    }

    @PatchMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + MEMBERS_KEY + "/{" + MEMBER_IDENTIFIER_KEY + "}" + UPDATE_MEMBER_ROLE_ENDPOINT
    )
    public String updateMemberRole(
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
            teamsHelper.updateMemberRole(teamId, memberId, role);
            return successResponse();
        } catch (IllegalArgumentException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}/" + MEMBERS_KEY + "/{" + MEMBER_IDENTIFIER_KEY + "}"
    )
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
        teamsHelper.removeMember(teamId, memberId);
        return successResponse();
    }

    private boolean hierarchyIsNotRespected(String userId, String memberId) {
        return userId.equals(memberId) || userItem.isTheAuthor(memberId);
    }

    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}" + LEAVE_ENDPOINT
    )
    public String leave(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        if(isUserNotAuthorized(userId, token, teamId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if(userItem.isAdmin(userId)) {
            if(userItem.hasMembers()) {
                if(userItem.hasAdmins(userId))
                    teamsHelper.removeMember(teamId, userId);
                else {
                    String viewerId = userItem.getViewer().getId();
                    teamsHelper.updateMemberRole(teamId, viewerId, ADMIN);
                    teamsHelper.removeMember(teamId, userId);
                }
            } else
                teamsHelper.deleteTeam(teamId);
        } else
            teamsHelper.removeMember(teamId, userId);
        return successResponse();
    }

    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + TEAM_IDENTIFIER_KEY + "}"
    )
    @Override
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        if(isUserNotAuthorized(userId, token, teamId) || !userItem.isTheAuthor(userId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        teamsHelper.deleteTeam(teamId);
        return successResponse();
    }

    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String teamId) {
        userItem = teamsHelper.getItemIfAllowed(userId, teamId);
        return !isMe(userId, token) || userItem == null;
    }
    
}
