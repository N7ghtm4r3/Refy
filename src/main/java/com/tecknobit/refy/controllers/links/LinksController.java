package com.tecknobit.refy.controllers.links;

import com.tecknobit.refy.controllers.DefaultRefyController;
import com.tecknobit.refy.helpers.services.links.LinksHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.records.RefyUser.LINKS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + LINKS_KEY)
public class LinksController extends DefaultRefyController {

    @Autowired
    private final LinksHelper linksHelper;

    public LinksController(LinksHelper linksHelper) {
        this.linksHelper = linksHelper;
    }

    @Override
    @GetMapping(
            headers = TOKEN_KEY
    )
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) linksHelper.getUserLinks(userId);
    }

    @Override
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, String> payload
    ) {
        return "";
    }

    @Override
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId,
            @RequestBody Map<String, String> payload
    ) {

        return "";
    }

    @Override
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {
        return null;
    }

    @Override
    public <T> T delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {
        return (T) "";
    }

}
