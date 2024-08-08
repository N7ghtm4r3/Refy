package com.tecknobit.refy.controllers;

import com.tecknobit.refycore.records.Team;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.records.RefyUser.TEAMS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;
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
            @PathVariable(USER_IDENTIFIER_KEY) String userId
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) teamsHelper.getAllUserTeams(userId);
    }

    @Override
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, Object> payload
    ) {
        return "";
    }

    @Override
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId,
            @RequestBody Map<String, Object> payload
    ) {
        return "";
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

    @Override
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(TEAM_IDENTIFIER_KEY) String teamId
    ) {
        return "";
    }

    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String teamId) {
        userItem = teamsHelper.getItemIfAllowed(userId, teamId);
        return !isMe(userId, token) || userItem == null;
    }
    
}
