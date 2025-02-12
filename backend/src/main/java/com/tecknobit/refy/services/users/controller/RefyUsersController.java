package com.tecknobit.refy.services.users.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxbackend.environment.services.users.controller.EquinoxUsersController;
import com.tecknobit.equinoxcore.annotations.CustomParametersOrder;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refy.services.users.repository.RefyUsersRepository;
import com.tecknobit.refy.services.users.service.RefyUsersService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.PATCH;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.refycore.ConstantsKt.AT_SYMBOL;
import static com.tecknobit.refycore.ConstantsKt.TAG_NAME_KEY;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.CHANGE_TAG_NAME_ENDPOINT;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.INSTANCE;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.WRONG_TAG_NAME_MESSAGE;

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
        if(!INSTANCE.isTagNameValid(custom[0].toString()))
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
        if (!INSTANCE.isTagNameValid(tagName))
            return failedResponse(WRONG_TAG_NAME_MESSAGE);
        try {
            usersService.changeTagName(tagName, id);
            return successResponse();
        } catch (Exception e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

}
