package com.tecknobit.refy.services.users.controller;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxbackend.environment.services.users.controller.EquinoxUsersController;
import com.tecknobit.equinoxcore.annotations.CustomParametersOrder;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refy.services.users.repository.RefyUsersRepository;
import com.tecknobit.refy.services.users.service.RefyUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static com.tecknobit.refycore.ConstantsKt.TAG_NAME_KEY;

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
     * {@code refyUsersService} helper to manage the {@link RefyUser} database operations
     */
    @Autowired
    private RefyUsersService refyUsersService;

    // TODO: 03/02/2025 CUSTOMIZE WITH THE TAG NAME THE SIGNUP AND SIGN-IN OPERATION

    /**
     * {@inheritDoc}
     */
    @Override
    @CustomParametersOrder(order = TAG_NAME_KEY)
    protected Object[] getSignUpCustomParams() {
        return new Object[]{jsonHelper.getString(TAG_NAME_KEY)};
    }

}
