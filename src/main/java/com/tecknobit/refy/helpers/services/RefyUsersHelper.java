package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.equinox.environment.helpers.services.EquinoxUsersHelper;
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

@Service
@Primary
public class RefyUsersHelper extends EquinoxUsersHelper<RefyUser> {

    @Autowired
    private RefyUsersRepository refyUsersRepository;

    @Autowired
    private CollectionsRepository collectionsRepository;

    @Autowired
    private TeamsRepository teamsRepository;

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
        super.deleteUser(id);
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
