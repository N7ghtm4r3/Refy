package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.equinox.environment.helpers.services.EquinoxUsersHelper;
import com.tecknobit.refy.helpers.services.repositories.RefyUsersRepository;
import com.tecknobit.refycore.records.RefyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

import static com.tecknobit.apimanager.apis.APIRequest.SHA256_ALGORITHM;

@Service
@Primary
public class RefyUsersHelper extends EquinoxUsersHelper<RefyUser> {

    @Autowired
    private RefyUsersRepository refyUsersRepository;

    public void signUpUser(String id, String token, String tagName, String name, String surname, String email,
                           String password, String language) throws NoSuchAlgorithmException {
        refyUsersRepository.saveUser(
                "RefyUser",
                id,
                token,
                tagName,
                name,
                surname,
                email,
                hash(password),
                language
        );
    }

    @Override
    public RefyUser signInUser(String email, String password) throws NoSuchAlgorithmException {
        return (RefyUser) super.signInUser(email, password);
    }

    /**
     * Method to hash a sensitive user data
     *
     * @param secret: the user value to hash
     * @throws NoSuchAlgorithmException when the hash of the user value fails
     */
    private String hash(String secret) throws NoSuchAlgorithmException {
        return APIRequest.base64Digest(secret, SHA256_ALGORITHM);
    }

}
