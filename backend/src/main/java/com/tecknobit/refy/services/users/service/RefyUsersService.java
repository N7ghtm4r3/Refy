package com.tecknobit.refy.services.users.service;

import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersService;
import com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager;
import com.tecknobit.refy.services.collections.repository.CollectionsRepository;
import com.tecknobit.refy.services.teams.repository.TeamsRepository;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refy.services.users.repository.RefyUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.ConstantsKt.TAG_NAME_KEY;

/**
 * The {@code RefyUsersHelper} class is useful to manage all the Refy's user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxUsersService
 * @see ResourcesManager
 */
@Service
@Primary
public class RefyUsersService extends EquinoxUsersService<RefyUser, RefyUsersRepository> {

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
     * {@inheritDoc}
     */
    @Override
    protected List<String> getQueryValuesKeys() {
        ArrayList<String> keys = new ArrayList<>(super.getQueryValuesKeys());
        keys.add(TAG_NAME_KEY);
        return keys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getDynamicAccountDataKeys() {
        ArrayList<String> keys = new ArrayList<>(super.getDynamicAccountDataKeys());
        keys.add(TAG_NAME_KEY);
        return keys;
    }

    /**
     * Method to change the tag name of the {@link RefyUser}
     *
     * @param newTagName The new tag name of the user
     * @param userId:    the identifier of the user
     */
    public void changeTagName(String newTagName, String userId) {
        usersRepository.changeTagName(newTagName, userId);
    }

    /**
     * Method to get the potential members for a team
     *
     * @param userId The identifier of the user to not fetch
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
        super.deleteUser(id);
    }

}
