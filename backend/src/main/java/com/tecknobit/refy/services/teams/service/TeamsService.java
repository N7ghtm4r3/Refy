package com.tecknobit.refy.services.teams.service;

import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.shared.batch.items.TeamCollectionBatchItem;
import com.tecknobit.refy.services.shared.batch.items.TeamLinkBatchItem;
import com.tecknobit.refy.configuration.RefyResourcesManager;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.collections.repository.CollectionsRepository;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.links.repository.LinksRepository;
import com.tecknobit.refy.services.shared.services.RefyItemRetriever;
import com.tecknobit.refy.services.teams.batch.TeamMemberBatchItem;
import com.tecknobit.refy.services.teams.batch.TeamMembersBatchQuery;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.repository.TeamsRepository;
import com.tecknobit.refycore.enums.TeamRole;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.tecknobit.equinoxbackend.configuration.IndexesCreator.formatFullTextKeywords;
import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.InsertCommand.INSERT_INTO;
import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.InsertCommand.REPLACE_INTO;
import static com.tecknobit.refy.services.shared.batch.items.TeamCollectionBatchItem.TEAM_COLLECTION_JOIN_TABLE_COLUMNS;
import static com.tecknobit.refy.services.teams.batch.TeamMembersBatchQuery.MEMBERS_TABLE_COLUMNS;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.INSTANCE;

/**
 * The {@code TeamsHelper} class is useful to manage all the {@link Team} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItemsHelper
 * @see RefyResourcesManager
 * @see RefyItemRetriever
 */
@Service
public class TeamsService extends EquinoxItemsHelper implements RefyResourcesManager, RefyItemRetriever<Team> {

    /**
     * {@code teamsRepository} instance for the teams repository
     */
    @Autowired
    private TeamsRepository teamsRepository;

    /**
     * {@code collectionsRepository} instance for the collections repository
     */
    @Autowired
    private CollectionsRepository collectionsRepository;

    /**
     * {@code linksRepository} instance for the links repository
     */
    @Autowired
    private LinksRepository linksRepository;

    /**
     * Method to get the user's owned teams identifiers
     *
     * @param userId The identifier of the user
     *
     * @return the identifiers of the owned user teams as {@link HashSet} of {@link String}
     */
    public HashSet<String> getUserTeams(String userId) {
        return teamsRepository.getUserTeams(userId);
    }

    /**
     * Method to get the user's owned teams
     *
     * @param userId The identifier of the owner
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     *
     * @return the user teams as {@link PaginatedResponse} of {@link Team}
     */
    public PaginatedResponse<Team> getUserOwnedTeams(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalTeams = teamsRepository.countUserOwnedTeams(userId);
        List<Team> teams = teamsRepository.getUserOwnedTeams(userId, pageable);
        return new PaginatedResponse<>(teams, page, pageSize, totalTeams);
    }

    /**
     * Method to get all the user's teams
     *
     * @param userId The identifier of the owner
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the teams
     *
     * @return the user teams as {@link List} of {@link Team}
     */
    public PaginatedResponse<Team> getAllUserTeams(String userId, int page, int pageSize, Set<String> keywords) {
        Pageable pageable = PageRequest.of(page, pageSize);
        String fullTextFormatter = formatFullTextKeywords(keywords, "*", true);
        long totalTeams = teamsRepository.countAllUserTeams(userId, fullTextFormatter);
        List<Team> teams = teamsRepository.getAllUserTeams(userId, fullTextFormatter, pageable);
        return new PaginatedResponse<>(teams, page, pageSize, totalTeams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Team getItemIfAllowed(String userId, String teamId) {
        return teamsRepository.getTeamIfAllowed(userId, teamId);
    }

    /**
     * Method to create a new team
     *
     * @param userId The user identifier of the team owner
     * @param teamId The identifier of the team
     * @param payload The payload with the details of the new team
     */
    public void createTeam(String userId, String teamId, TeamPayload payload) throws IOException {
        MultipartFile logo = payload.logo_pic;
        String logoUrl = createLogoResource(logo, teamId + System.currentTimeMillis());
        teamsRepository.saveTeam(teamId, payload.title, logoUrl, payload.description, System.currentTimeMillis(), userId);
        batchInsert(INSERT_INTO, MEMBERS_KEY, new TeamMembersBatchQuery(userId, teamId, payload));
        saveResource(logo, logoUrl);
    }

    /**
     * Method to edit a team
     *
     * @param userId The user identifier of the team owner
     * @param team The team to edit
     * @param payload The payload with the details of the team
     */
    public void editTeam(String userId, Team team, TeamPayload payload) throws IOException {
        String teamId = team.getId();
        MultipartFile logo = payload.logo_pic;
        boolean logoChanged = logo != null && !logo.isEmpty();
        String logoUrl;
        if(logoChanged)
            logoUrl = createLogoResource(logo, teamId + System.currentTimeMillis());
        else
            logoUrl = team.getLogoPic();
        teamsRepository.editTeam(teamId, payload.title, logoUrl, payload.description, userId);
        if (logoChanged) {
            deleteLogoResource(teamId);
            saveResource(logo, logoUrl);
        }
        synchronizeMembers(userId, teamId, payload);
    }

    /**
     * Method to synchronize the members of a team
     *
     * @param userId The identifier of the user
     * @param teamId The identifier of the team
     * @param payload The payload with the details of the team
     */
    private void synchronizeMembers(String userId, String teamId, TeamPayload payload) {
        SyncBatchModel model = new SyncBatchModel() {
            @Override
            public Collection<TeamMemberBatchItem> getCurrentData() {
                ArrayList<TeamMemberBatchItem> teamMemberBatchItems = new ArrayList<>();
                Team team = getItemIfAllowed(userId, teamId);
                for (Team.RefyTeamMember member : team.getMembers())
                    teamMemberBatchItems.add(new TeamMemberBatchItem(member.getId(), teamId, member.getRole()));
                return teamMemberBatchItems;
            }

            @Override
            public String[] getDeletingColumns() {
                return new String[]{OWNER_KEY, TEAM_IDENTIFIER_KEY};
            }
        };
        BatchQuery<TeamMemberBatchItem> batchQuery = new BatchQuery<>() {
            @Override
            public Collection<TeamMemberBatchItem> getData() {
                ArrayList<TeamMemberBatchItem> teamMemberBatchItems = new ArrayList<>();
                JSONArray rawMembers = payload.members;
                for (int j = 0; j < rawMembers.length(); j++) {
                    JSONObject rawMember = rawMembers.getJSONObject(j);
                    teamMemberBatchItems.add(new TeamMemberBatchItem(
                            rawMember.getString(MEMBER_IDENTIFIER_KEY),
                            teamId,
                            rawMember.getEnum(TeamRole.class, TEAM_ROLE_KEY)
                    ));
                }
                return teamMemberBatchItems;
            }

            @Override
            public void prepareQuery(Query query, int index, Collection<TeamMemberBatchItem> items) {
                for (TeamMemberBatchItem teamMemberBatchItem : items) {
                    query.setParameter(index++, teamMemberBatchItem.getOwner());
                    query.setParameter(index++, teamId);
                    query.setParameter(index++, teamMemberBatchItem.getRole().name());
                }
            }

            @Override
            public String[] getColumns() {
                return MEMBERS_TABLE_COLUMNS;
            }
        };
        syncBatch(model, REPLACE_INTO, MEMBERS_KEY, batchQuery);
    }

    /**
     * Method to share the links with the team
     *
     * @param userId The identifier of the user
     * @param teamId The identifier of the team
     * @param links  The links to share in the team
     */
    // FIXME: 14/02/2025 USE THE BatchSynchronizationProcedure WHEN IMPLEMENTED
    public void shareLinksWithTeam(String userId, String teamId, List<String> links) {
        SyncBatchModel model = new SyncBatchModel() {
            @Override
            public Collection<TeamLinkBatchItem> getCurrentData() {
                Team team = getItemIfAllowed(userId, teamId);
                List<String> linkIds = team.getLinkIds();
                ArrayList<TeamLinkBatchItem> teamLinkBatchItems = new ArrayList<>();
                for (String linkId : linkIds)
                    teamLinkBatchItems.add(new TeamLinkBatchItem(teamId, linkId));
                return teamLinkBatchItems;
            }

            @Override
            public String[] getDeletingColumns() {
                return new String[]{TEAM_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY};
            }
        };
        BatchQuery<TeamLinkBatchItem> batchQuery = new BatchQuery<>() {
            @Override
            public Collection<TeamLinkBatchItem> getData() {
                ArrayList<TeamLinkBatchItem> teamLinkBatchItems = new ArrayList<>();
                for (String linkId : links)
                    teamLinkBatchItems.add(new TeamLinkBatchItem(teamId, linkId));
                return teamLinkBatchItems;
            }

            @Override
            public void prepareQuery(Query query, int index, Collection<TeamLinkBatchItem> items) {
                for (TeamLinkBatchItem element : items) {
                    query.setParameter(index++, element.getOwner());
                    query.setParameter(index++, element.getOwned());
                }
            }

            @Override
            public String[] getColumns() {
                return new String[]{TEAM_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY};
            }
        };
        syncBatch(model, TEAMS_LINKS_TABLE, batchQuery);
    }

    /**
     * Method to share the collections with the team
     *
     * @param userId The identifier of the user
     * @param teamId The identifier of the team
     * @param collections The collections to share in the team
     *
     */
    // FIXME: 14/02/2025 USE THE BatchSynchronizationProcedure WHEN IMPLEMENTED
    public void shareCollectionsWithTeam(String userId, String teamId, List<String> collections) {
        SyncBatchModel model = new SyncBatchModel() {
            @Override
            public Collection<TeamCollectionBatchItem> getCurrentData() {
                Team team = getItemIfAllowed(userId, teamId);
                ArrayList<TeamCollectionBatchItem> teamCollectionBatchItems = new ArrayList<>();
                List<String> collectionIds = team.getCollectionsIds();
                for (String collectionId : collectionIds)
                    teamCollectionBatchItems.add(new TeamCollectionBatchItem(teamId, collectionId));
                return teamCollectionBatchItems;
            }

            @Override
            public String[] getDeletingColumns() {
                return TEAM_COLLECTION_JOIN_TABLE_COLUMNS;
            }
        };
        BatchQuery<TeamCollectionBatchItem> batchQuery = new BatchQuery<>() {
            @Override
            public Collection<TeamCollectionBatchItem> getData() {
                ArrayList<TeamCollectionBatchItem> teamCollectionBatchItems = new ArrayList<>();
                for (String collectionId : collections)
                    teamCollectionBatchItems.add(new TeamCollectionBatchItem(teamId, collectionId));
                return teamCollectionBatchItems;
            }

            @Override
            public void prepareQuery(Query query, int index, Collection<TeamCollectionBatchItem> items) {
                for (TeamCollectionBatchItem teamCollectionBatchItem : items) {
                    query.setParameter(index++, teamCollectionBatchItem.getOwner());
                    query.setParameter(index++, teamCollectionBatchItem.getOwned());
                }
            }

            @Override
            public String[] getColumns() {
                return TEAM_COLLECTION_JOIN_TABLE_COLUMNS;
            }
        };
        syncBatch(model, COLLECTIONS_TEAMS_TABLE, batchQuery);
    }

    /**
     * Method to get the collections shared with the team
     *
     * @param teamId   The identifier of the team
     * @param page     The page requested
     * @param pageSize The size of the items to insert in the page
     * @return the collection teams as {@link PaginatedResponse} of {@link LinksCollection}
     */
    public PaginatedResponse<LinksCollection> getTeamCollections(String teamId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalCollections = collectionsRepository.countTeamCollections(teamId);
        List<LinksCollection> collections = collectionsRepository.getTeamCollections(teamId, pageable);
        return new PaginatedResponse<>(collections, page, pageSize, totalCollections);
    }

    /**
     * Method to get the links shared in a team
     *
     * @param teamId   The identifier of the team
     * @param page     The page requested
     * @param pageSize The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     * @return the collection links as {@link PaginatedResponse} of {@link RefyLink}
     */
    public PaginatedResponse<RefyLink> getTeamLinks(String teamId, int page, int pageSize, Set<String> keywords) {
        Pageable pageable = PageRequest.of(page, pageSize);
        String fullTextMatcher = formatFullTextKeywords(keywords, "*", true);
        long totalLinks = linksRepository.countTeamLinks(teamId, fullTextMatcher);
        List<RefyLink> links = linksRepository.getTeamLinks(teamId, fullTextMatcher, pageable);
        return new PaginatedResponse<>(links, page, pageSize, totalLinks);
    }

    /**
     * Method to remove a collection from a team
     *
     * @param teamId The identifier of the team
     * @param collectionId The identifier of the collection
     */
    public void removeCollectionFromTeam(String teamId, String collectionId) {
        teamsRepository.removeCollectionFromTeam(teamId, collectionId);
    }

    /**
     * Method to remove a link from a team
     *
     * @param teamId The identifier of the team
     * @param linkId The identifier of the link
     */
    public void removeLinkFromTeam(String teamId, String linkId) {
        teamsRepository.removeLinkFromTeam(teamId, linkId);
    }

    /**
     * Method change the role of a team member
     * @param teamId The identifier of the team
     * @param memberId The identifier of the member
     * @param role The role to set to the member
     */
    public void changeMemberRole(String teamId, String memberId, TeamRole role) {
        teamsRepository.changeMemberRole(memberId, teamId, role);
    }

    /**
     * Method to remove a member from a team
     *
     * @param teamId The identifier of the team
     * @param memberId The identifier of the member
     */
    public void removeMember(String teamId, String memberId) {
        teamsRepository.removeMember(memberId, teamId);
    }

    /**
     * Method to delete a team
     *
     * @param teamId The identifier of the team to delete
     */
    public void deleteTeam(String teamId) {
        teamsRepository.detachTeamFromLinks(teamId);
        teamsRepository.detachTeamFromCollections(teamId);
        teamsRepository.deleteTeam(teamId);
        deleteLogoResource(teamId);
    }

    /**
     * Record class representing a team payload useful to create or edit a {@link Team}
     *
     * @param title The title of the team
     * @param logo_pic The logo picture of the team
     * @param description The description of the team
     * @param members The members of the team
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public record TeamPayload(String title, MultipartFile logo_pic, String description, JSONArray members) {

        /**
         * Method to check if the team payload is valid to create or edit a team
         * @param validateLogoPic: whether the logo pic must be checked or passed
         * @return whether the team payload is valid as boolean
         */
        public boolean isValidTeamPayload(boolean validateLogoPic) {
            boolean validPayload = INSTANCE.isTitleValid(title) && INSTANCE.isDescriptionValid(description);
            if(validateLogoPic)
                return validPayload && (logo_pic != null && !logo_pic.isEmpty());
            return validPayload;
        }

    }

}
