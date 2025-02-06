package com.tecknobit.refy.services.users.controller;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxbackend.environment.services.users.controller.EquinoxUsersController;
import com.tecknobit.equinoxcore.annotations.CustomParametersOrder;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refy.services.users.repository.RefyUsersRepository;
import com.tecknobit.refy.services.users.service.RefyUsersService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;

import static com.tecknobit.refycore.ConstantsKt.AT_SYMBOL;
import static com.tecknobit.refycore.ConstantsKt.TAG_NAME_KEY;
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

}
