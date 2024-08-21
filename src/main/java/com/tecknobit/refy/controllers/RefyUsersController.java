package com.tecknobit.refy.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.equinox.environment.controllers.EquinoxUsersController;
import com.tecknobit.refy.helpers.services.RefyUsersHelper;
import com.tecknobit.refycore.records.RefyUser;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY;
import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.SIGN_IN_ENDPOINT;
import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.SIGN_UP_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.*;
import static com.tecknobit.equinox.inputs.InputValidator.*;
import static com.tecknobit.refycore.helpers.RefyInputValidator.WRONG_TAG_NAME_MESSAGE;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isTagNameValid;
import static com.tecknobit.refycore.records.RefyUser.TAG_NAME_KEY;

/**
 * The {@code RefyUsersController} class is useful to manage all the Refy users operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 * @see EquinoxUsersController
 *
 */
@RestController
public class RefyUsersController extends EquinoxUsersController<RefyUser> {

    /**
     * {@code refyUsersHelper} helper to manage the {@link RefyUser} database operations
     */
    private final RefyUsersHelper refyUsersHelper;

    /**
     * Constructor to init the {@link RefyUsersController} controller
     *
     * @param refyUsersHelper: helper to manage the {@link RefyUser} database operations
     */
    public RefyUsersController(RefyUsersHelper refyUsersHelper) {
        super(refyUsersHelper);
        this.refyUsersHelper = refyUsersHelper;
    }

    /**
     * Method to sign up in the <b>Refy's system</b>
     *
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "server_secret" : "the secret of the server" -> [String],
     *                                  "name" : "the name of the user" -> [String],
     *                                  "tag_name" : "the tag name of the user" -> [String],
     *                                  "surname": "the surname of the user" -> [String],
     *                                  "email": "the email of the user" -> [String],
     *                                  "password": "the password of the user" -> [String]
     *                              }
     *                      }
     *                 </pre>
     * @return the result of the request as {@link String}
     */
    @Override
    @PostMapping(path = SIGN_UP_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signUp", method = POST)
    public String signUp(Map<String, String> payload) {
        loadJsonHelper(payload);
        mantis.changeCurrentLocale(jsonHelper.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE));
        if (serverProtector.serverSecretMatches(jsonHelper.getString(SERVER_SECRET_KEY))) {
            String name = jsonHelper.getString(NAME_KEY);
            String surname = jsonHelper.getString(SURNAME_KEY);
            if (isNameValid(name)) {
                if (isSurnameValid(surname))
                    return executeAuth(payload, name, surname);
                else
                    return failedResponse(WRONG_SURNAME_MESSAGE);
            } else
                return failedResponse(WRONG_NAME_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to sign in the <b>Neutron's system</b>
     *
     * @param payload: payload of the request
     *                 <pre>
     *                      {@code
     *                              {
     *                                  "email": "the email of the user", -> [String]
     *                                  "password": "the password of the user" -> [String]
     *                              }
     *                      }
     *                 </pre>
     * @return the result of the request as {@link String}
     */
    @PostMapping(path = SIGN_IN_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signIn", method = POST)
    public String signIn(@RequestBody Map<String, String> payload) {
        return executeAuth(payload);
    }

    /**
     * Method to execute the auth operations
     *
     * @param payload:      the payload received with the auth request
     * @param personalData: the personal data of the user like name and surname
     * @return the result of the auth operation as {@link String}
     */
    private String executeAuth(Map<String, String> payload, String... personalData) {
        loadJsonHelper(payload);
        String email = jsonHelper.getString(EMAIL_KEY);
        String password = jsonHelper.getString(PASSWORD_KEY);
        String language = jsonHelper.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
        mantis.changeCurrentLocale(language);
        if (isEmailValid(email)) {
            if (isPasswordValid(password)) {
                if (isLanguageValid(language)) {
                    String id;
                    String token;
                    String profilePicUrl;
                    JSONObject response = new JSONObject();
                    if (personalData.length == 2) {
                        String tagName = jsonHelper.getString(TAG_NAME_KEY);
                        if(!isTagNameValid(tagName))
                            return failedResponse(WRONG_TAG_NAME_MESSAGE);
                        id = generateIdentifier();
                        token = generateIdentifier();
                        profilePicUrl = DEFAULT_PROFILE_PIC;
                        try {
                            refyUsersHelper.signUpUser(
                                    id,
                                    token,
                                    tagName,
                                    personalData[0],
                                    personalData[1],
                                    email.toLowerCase(),
                                    password,
                                    language
                            );
                        } catch (Exception e) {
                            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                        }
                    } else {
                        try {
                            RefyUser user = refyUsersHelper.signInUser(email.toLowerCase(), password);
                            if (user != null) {
                                id = user.getId();
                                token = user.getToken();
                                profilePicUrl = user.getProfilePic();
                                response.put(TAG_NAME_KEY, user.getTagName());
                                response.put(NAME_KEY, user.getName());
                                response.put(SURNAME_KEY, user.getSurname());
                                response.put(LANGUAGE_KEY, user.getLanguage());
                            } else
                                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                        } catch (Exception e) {
                            return failedResponse(WRONG_PROCEDURE_MESSAGE);
                        }
                    }
                    mantis.changeCurrentLocale(DEFAULT_LANGUAGE);
                    return successResponse(response
                            .put(IDENTIFIER_KEY, id)
                            .put(TOKEN_KEY, token)
                            .put(PROFILE_PIC_KEY, profilePicUrl)
                    );
                } else
                    return failedResponse(WRONG_LANGUAGE_MESSAGE);
            } else
                return failedResponse(WRONG_PASSWORD_MESSAGE);
        } else
            return failedResponse(WRONG_EMAIL_MESSAGE);
    }

}
