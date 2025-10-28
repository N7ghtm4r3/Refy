package com.tecknobit.refy.services.users.services;

import com.tecknobit.equinoxbackend.apis.resources.ResourcesManager;
import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersService;
import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.collections.repository.CollectionsRepository;
import com.tecknobit.refy.services.teams.entities.Team.RefyTeamMember;
import com.tecknobit.refy.services.teams.repository.TeamsRepository;
import com.tecknobit.refy.services.users.entities.RefyUser;
import com.tecknobit.refy.services.users.entities.UserSettings;
import com.tecknobit.refy.services.users.repositories.RefyUsersRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.ConstantsKt.CLOSE_APPLICATION_ON_LINK_OPEN_KEY;
import static com.tecknobit.refycore.ConstantsKt.TAG_NAME_KEY;

/**
 * The {@code RefyUsersService} class is useful to manage all the Refy's user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxUsersService
 * @see ResourcesManager
 */
@Service
@Primary
public class RefyUsersService extends EquinoxUsersService<RefyUser, RefyUsersRepository> {

    /**
     * {@code collectionsRepository} instance for the collections repository
     */
    private final CollectionsRepository collectionsRepository;

    /**
     * {@code teamsRepository} instance for the teams repository
     */
    private final TeamsRepository teamsRepository;

    /**
     * Constructor used to init the {@link EquinoxUsersService} service
     *
     * @param usersRepository The instance for the users repository
     * @param collectionsRepository The instance for the collections repository
     * @param teamsRepository The instance for the teams repository
     */ 
    @Autowired
    public RefyUsersService(RefyUsersRepository usersRepository, CollectionsRepository collectionsRepository, 
                            TeamsRepository teamsRepository) {
        super(usersRepository);
        this.collectionsRepository = collectionsRepository;
        this.teamsRepository = teamsRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSignUpKeys() {
        ArrayList<String> keys = new ArrayList<>(super.getSignUpKeys());
        keys.add(TAG_NAME_KEY);
        return keys;
    }

    /**
     * Method used to get the dynamic data of the user to correctly update in all the devices where the user is connected
     *
     * @param userId The identifier of the user
     * @return the dynamic data as {@link JSONObject}
     */
    @Override
    public JSONObject getDynamicAccountData(String userId) {
        RefyUser user = usersRepository.findById(userId).orElseThrow();
        JSONObject dynamicAccountData = super.getDynamicAccountData(userId);
        UserSettings settings = user.getSettings();
        dynamicAccountData.put(CLOSE_APPLICATION_ON_LINK_OPEN_KEY, settings.closeApplicationOnLinkOpen());
        return dynamicAccountData;
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
     * @param userId The identifier of the user
     */
    public void changeTagName(String newTagName, String userId) {
        usersRepository.changeTagName(newTagName, userId);
    }

    /**
     * Method to get the potential members for a team
     *
     * @param userId The identifier of the user to not fetch
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     *
     * @return list of potential members as {@link List} of {@link List} of {@link String}
     */
    public PaginatedResponse<RefyTeamMember> getPotentialMembers(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalPotentialMembers = usersRepository.count() - 1;
        List<List<String>> rawPotentialsMembers = usersRepository.getPotentialMembers(userId, pageable);
        ArrayList<RefyTeamMember> potentialsMember = new ArrayList<>();
        for (List<String> rawPotentialMember : rawPotentialsMembers)
            potentialsMember.add(new RefyTeamMember(rawPotentialMember));
        return new PaginatedResponse<>(potentialsMember, page, pageSize, totalPotentialMembers);
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
