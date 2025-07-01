package com.tecknobit.refy.services.customlinks.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.collections.service.LinksCollectionsService;
import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink;
import com.tecknobit.refy.services.customlinks.service.CustomLinksService;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.links.service.LinksService;
import com.tecknobit.refy.services.shared.controllers.DefaultRefyController;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.service.TeamsService;
import com.tecknobit.refycore.enums.ExpiredTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager.RESOURCES_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinoxcore.pagination.PaginatedResponse.*;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.CUSTOM_LINKS_ENDPOINT;
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
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}" + CUSTOM_LINKS_ENDPOINT)
public class CustomLinksController extends DefaultRefyController<CustomRefyLink> {

    /**
     * {@code customLinksService} helper to manage the {@link CustomRefyLink} database operations
     */
    private final CustomLinksService customLinksService;

    /**
     * Constructor used to init the controller
     *
     * @param linksService The helper to manage the {@link RefyLink} database operations
     * @param linksCollectionsService The helper to manage the {@link LinksCollection} database operations
     * @param teamsService The helper to manage the {@link Team} database operations
     * @param customLinksService The helper to manage the {@link CustomRefyLink} database operations
     */
    @Autowired
    public CustomLinksController(LinksService linksService, LinksCollectionsService linksCollectionsService,
                                 TeamsService teamsService, CustomLinksService customLinksService) {
        super(linksService, linksCollectionsService, teamsService);
        this.customLinksService = customLinksService;
    }

    /**
     * Method to get a list of custom links
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param ownedOnly Whether to get only the collections where the user is the owner
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     *
     * @return the custom links list, if authorized, else failed message as {@link T}
     *
     * @param <T> the {@link CustomRefyLink} type
     */
    @GetMapping(
            headers = TOKEN_KEY
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/customLinks", method = GET)
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            boolean ownedOnly,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize,
            @RequestParam(name = KEYWORDS_KEY, defaultValue = "", required = false) Set<String> keywords
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(customLinksService.getUserCustomLinks(userId, page, pageSize, keywords));
    }

    /**
     * Method to create a new custom link
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "title" : "title of the link" -> [String],
     *                                  "description" : "the description of the custom link" -> [String],
     *                                  "resources" : "the resources to share with the link" -> Map[String, String],
     *                                  "fields" : "the fields used to protect the resources with a validation form" -> Map[String, String],
     *                                  "hasUniqueAccess" : "whether the link, when requested for the first time, must be deleted and no more accessible" -> [boolean],
     *                                  "expiredTime" : "when the link expires and automatically deleted" -> [{@link ExpiredTime }],
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
    @RequestPath(path = "/api/v1/users/{user_id}/customLinks", method = POST)
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
        if(!INSTANCE.isCustomLinkPayloadValid(title, description, resources, fields))
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
        customLinksService.createCustomLink(userId, generateIdentifier(), title, description, hasUniqueAccess, expiredTime,
                fields, resources);
        return successResponse();
    }

    /**
     * Method to edit an existing custom link
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "title" : "title of the link" -> [String],
     *                                  "description" : "the description of the custom link" -> [String],
     *                                  "resources" : "the resources to share with the link" -> Map[String, String],
     *                                  "fields" : "the fields used to protect the resources with a validation form" -> Map[String, String],
     *                                  "hasUniqueAccess" : "whether the link, when requested for the first time, must be deleted and no more accessible" -> [boolean],
     *                                  "expiredTime" : "when the link expires and automatically deleted" -> [{@link ExpiredTime}],
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
    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = PATCH)
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String title = jsonHelper.getString(TITLE_KEY);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        Map<String, Object> resources = jsonHelper.getJSONObject(RESOURCES_KEY, new JSONObject()).toMap();
        Map<String, Object> fields = jsonHelper.getJSONObject(FIELDS_KEY, new JSONObject()).toMap();
        if(!INSTANCE.isCustomLinkPayloadValid(title, description, resources, fields))
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
        customLinksService.editCustomLink(userId, linkId, title, description, hasUniqueAccess, expiredTime, fields,
                resources);
        return successResponse();
    }

    /**
     * Method to get an existing custom link
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param linkId The identifier of the custom link to get
     *
     * @return the custom link requested, if authorized, or the failed response message as {@link T}
     */
    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = GET)
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        return super.getItem(token, userId, linkId);
    }

    /**
     * Method to delete a custom link
     *
     * @param token The token of the user
     * @param userId The identifier of the user
     * @param linkId The identifier of the custom link to delete
     *
     * @return the response message as {@link String}
     */
    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = DELETE)
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        customLinksService.deleteLink(linkId);
        return successResponse();
    }

    /**
     * Method to get whether the user is or not authorized to operate with the custom link requested
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param linkId The identifier of the link requested
     * @return whether the user is or not authorized to operate with the custom link requested
     */
    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String linkId) {
        userItem = customLinksService.getItemIfAllowed(userId, linkId);
        return !isMe(userId, token) || userItem == null;
    }

}
