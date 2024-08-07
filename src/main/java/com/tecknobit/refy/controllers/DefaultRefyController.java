package com.tecknobit.refy.controllers;

import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.refy.helpers.services.links.LinksHelper;
import com.tecknobit.refycore.records.RefyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}")
public abstract class DefaultRefyController extends EquinoxController<RefyUser> {

    @Autowired
    protected LinksHelper linksHelper;

    public abstract <T> T list(
            String token,
            String userId
    );

    public abstract String create(
            String token,
            String userId,
            Map<String, String> payload
    );

    public abstract String edit(
            String token,
            String userId,
            String itemId,
            Map<String, String> payload
    );

    public abstract <T> T getItem(
            String token,
            String userId,
            String itemId
    );

    public abstract String delete(
            String token,
            String userId,
            String itemId
    );

    /*
    @Override
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId
    ) {

    }

    @Override
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, String> payload
    ) {

    }

    @Override
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId,
            @RequestBody Map<String, String> payload
    ) {

    }

    @Override
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {

    }

    @Override
    public <T> T delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {

    }
     */

}
