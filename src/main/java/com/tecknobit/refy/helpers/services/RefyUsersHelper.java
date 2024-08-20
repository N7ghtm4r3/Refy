package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.equinox.environment.helpers.services.EquinoxUsersHelper;
import com.tecknobit.equinox.resourcesutils.ResourcesManager;
import com.tecknobit.refy.helpers.services.repositories.CollectionsRepository;
import com.tecknobit.refy.helpers.services.repositories.RefyUsersRepository;
import com.tecknobit.refy.helpers.services.repositories.TeamsRepository;
import com.tecknobit.refycore.records.RefyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;

import static com.tecknobit.apimanager.apis.APIRequest.SHA256_ALGORITHM;

/**
 * The {@code RefyUsersHelper} class is useful to manage all the Refy's user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxUsersHelper
 * @see ResourcesManager
 */
@Service
@Primary
public class RefyUsersHelper extends EquinoxUsersHelper<RefyUser> {

    /**
     * {@code refyUsersRepository} instance for the Refy users repository
     */
    @Autowired
    private RefyUsersRepository refyUsersRepository;

    /**
     * {@code collectionsRepository} instance for the collections repository
     */
    @Autowired
    private CollectionsRepository collectionsRepository;

    /**
     * {@code teamsRepository} instance for the teams repository
     */
    @Autowired
    private TeamsRepository teamsRepository;

    /**
     * Method to sign up a new user in the system
     *
     * @param id:       the identifier of the user
     * @param token:    the token of the user
     * @param tagName:  the tag name of the user
     * @param name:     the name of the user
     * @param surname:  the surname of the user
     * @param email:    the email of the user
     * @param password: the password of the user
     * @param language: the language of the user
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public RefyUser signInUser(String email, String password) throws NoSuchAlgorithmException {
        return (RefyUser) super.signInUser(email, password);
    }

    /**
     * Method to get the potential members for a team
     *
     * @param userId: the identifier of the user to not fetch
     *
     * @return list of potential members as {@link List} of {@link List} of {@link String}
     */
    public List<List<String>> getPotentialMembers(String userId) {
        return refyUsersRepository.getPotentialMembers(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(String id) {
        HashSet<String> collections = collectionsRepository.getUserCollections(id);
        for (String collectionId : collections) {
            collectionsRepository.detachCollectionFromLinks(collectionId);
            collectionsRepository.detachCollectionFromTeams(collectionId);
        }
        HashSet<String> teams = teamsRepository.getUserTeams(id);
        for (String teamId : teams)
            teamsRepository.detachTeamFromLinks(teamId);
        refyUsersRepository.deleteUser(id);
        deleteProfileResource(id);
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
