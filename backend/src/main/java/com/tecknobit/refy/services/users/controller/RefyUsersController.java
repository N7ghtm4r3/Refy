package com.tecknobit.refy.services.users.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxbackend.environment.services.users.controller.EquinoxUsersController;
import com.tecknobit.equinoxcore.annotations.CustomParametersOrder;
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation;
import com.tecknobit.refy.services.users.entities.RefyUser;
import com.tecknobit.refy.services.users.entities.UserSettings;
import com.tecknobit.refy.services.users.repositories.RefyUsersRepository;
import com.tecknobit.refy.services.users.services.RefyUserSettingsService;
import com.tecknobit.refy.services.users.services.RefyUsersService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.PATCH;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.CHANGE_TAG_NAME_ENDPOINT;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.isTagNameValid;

/**
 * The {@code RefyUsersController} class is useful to manage all the Refy users operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see EquinoxUsersController
 *
 */
@RestController
public class RefyUsersController extends EquinoxUsersController<RefyUser, RefyUsersRepository, RefyUsersService> {

    /**
     * {@code WRONG_TAG_NAME_MESSAGE} message to use when the tag name of the user is not valid
     */
    private static final String WRONG_TAG_NAME_MESSAGE = "wrong_tag_name";

    @RequiresDocumentation(additionalNotes = "TO INSERT SINCE")
    private final RefyUserSettingsService settingsService;

    /**
     * Constructor to init the controller
     *
     * @param usersService The helper to manage the users database operations
     */
    @Autowired
    @RequiresDocumentation
    public RefyUsersController(RefyUsersService usersService, RefyUserSettingsService settingsService) {
        super(usersService);
        this.settingsService = settingsService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CustomParametersOrder(order = TAG_NAME_KEY)
    protected Object[] getSignUpCustomParams() {
        String tagName = jsonHelper.getString(TAG_NAME_KEY);
        if(!tagName.startsWith(AT_SYMBOL))
            tagName += AT_SYMBOL + tagName;
        return new Object[]{tagName};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CustomParametersOrder(order = TAG_NAME_KEY)
    protected String validateSignUp(String name, String surname, String email, String password, String language,
                                    Object... custom) {
        String validation = super.validateSignUp(name, surname, email, password, language, custom);
        if(validation != null)
            return validation;
        if(!isTagNameValid(custom[0].toString()))
            return WRONG_TAG_NAME_MESSAGE;
        else return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JSONObject assembleSignInSuccessResponse(RefyUser user) {
        JSONObject response = super.assembleSignInSuccessResponse(user);
        response.put(TAG_NAME_KEY, user.getTagName());
        UserSettings settings = user.getSettings();
        JSONObject jSettings = new JSONObject();
        jSettings.put(CLOSE_APPLICATION_ON_LINK_OPEN_KEY, settings.closeApplicationOnLinkOpen());
        response.put(SETTINGS_KEY, jSettings);
        return response;
    }

    /**
     * Method to change the tag name of the user
     *
     * @param id      The identifier of the user
     * @param token   The token of the user
     * @param payload Payload of the request
     *                <pre>
     *                                     {@code
     *                                             {
     *                                                 "tag_na,e": "the new tag name of the user" -> [String]
     *                                             }
     *                                     }
     *                                </pre>
     * @return the result of the request as {@link String}
     */
    @PatchMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + CHANGE_TAG_NAME_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeTagName", method = PATCH)
    public String changeTagName(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if (!isMe(id, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String tagName = jsonHelper.getString(TAG_NAME_KEY);
        if (!isTagNameValid(tagName))
            return failedResponse(WRONG_TAG_NAME_MESSAGE);
        try {
            usersService.changeTagName(tagName, id);
            return successResponse();
        } catch (Exception e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    @RequiresDocumentation(additionalNotes = "TO INSERT SINCE")
    @PatchMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}/" + SETTINGS_KEY,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/settings", method = PATCH)
    public String changeUserSettings(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if (!isMe(id, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        settingsService.changeUserSettings(me, jsonHelper);
        return successResponse();
    }

}
