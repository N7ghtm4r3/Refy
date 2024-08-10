package com.tecknobit.refy.controllers.links;

import com.tecknobit.refy.controllers.DefaultRefyController;
import com.tecknobit.refy.helpers.services.links.CustomLinksHelper;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.CUSTOM_LINKS_ENDPOINT;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isCustomLinkPayloadValid;
import static com.tecknobit.refycore.records.RefyItem.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.RefyItem.TITLE_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.CustomRefyLink.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}" + CUSTOM_LINKS_ENDPOINT)
public class CustomLinksController extends DefaultRefyController<CustomRefyLink> {

    @Autowired
    private CustomLinksHelper customLinksHelper;

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
        return (T) customLinksHelper.getUserCustomLinks(userId);
    }

    @PostMapping(
            headers = TOKEN_KEY
    )
    @Override
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, Object> payload
    ) {
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String title = jsonHelper.getString(TITLE_KEY);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        Map<String, Object> resources = jsonHelper.getJSONObject(RESOURCES_KEY, new JSONObject()).toMap();
        Map<String, Object> fields = jsonHelper.getJSONObject(FIELDS_KEY, new JSONObject()).toMap();
        if(!isCustomLinkPayloadValid(title, description, resources, fields))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        boolean hasUniqueAccess = jsonHelper.getBoolean(UNIQUE_ACCESS_KEY);
        String sExpiredTime = jsonHelper.getString(EXPIRED_TIME_KEY);
        ExpiredTime expiredTime;
        if(sExpiredTime != null) {
            try {
                expiredTime = ExpiredTime.valueOf(sExpiredTime);
            } catch (IllegalArgumentException e) {
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
            }
        } else
            expiredTime = ExpiredTime.NO_EXPIRATION;
        customLinksHelper.createCustomLink(userId, generateIdentifier(), title, description, hasUniqueAccess, expiredTime,
                resources, fields);
        return successResponse();
    }

    @PatchMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, Object> payload
    ) {
        return "";
    }

    @Override
    public <T> T getItem(String token, String userId, String itemId) {
        return super.getItem(token, userId, itemId);
    }

    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        customLinksHelper.deleteLink(linkId);
        return successResponse();
    }

    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String linkId) {
        userItem = customLinksHelper.getItemIfAllowed(userId, linkId);
        return !isMe(userId, token) || userItem == null;
    }

}
