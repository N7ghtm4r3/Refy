package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.refy.helpers.resources.RefyResourcesManager;
import com.tecknobit.refy.helpers.services.repositories.TeamsRepository;
import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.helpers.RefyInputValidator.isDescriptionValid;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isTitleValid;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.Team.*;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TEAM_ROLE_KEY;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.ADMIN;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.VIEWER;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

/**
 * The {@code TeamsHelper} class is useful to manage all the {@link Team} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see RefyItemsHelper
 * @see RefyResourcesManager
 */
@Service
public class TeamsHelper extends RefyItemsHelper<Team> implements RefyResourcesManager {

    /**
     * {@code ADD_MEMBERS_QUERY} the query used to add new members to a team
     */
    private static final String ADD_MEMBERS_QUERY =
            "INSERT INTO " + MEMBERS_KEY +
                    "(" +
                        OWNER_KEY + "," +
                        TEAM_IDENTIFIER_KEY + "," +
                        TEAM_ROLE_KEY +
                    ")" +
            " VALUES ";

    /**
     * {@code REMOVE_MEMBERS_FROM_TEAM_QUERY} the query used to remove members from a team
     */
    private static final String REMOVE_MEMBERS_FROM_TEAM_QUERY =
            "DELETE FROM " + MEMBERS_KEY + " WHERE "
                    + TEAM_IDENTIFIER_KEY + "='%s' " + "AND " + OWNER_KEY + " IN (";

    /**
     * {@code REPLACE_MEMBERS_QUERY} the query used to replace member in a team
     */
    private static final String REPLACE_MEMBERS_QUERY =
            "REPLACE INTO " + MEMBERS_KEY +
                    "(" +
                    OWNER_KEY + "," +
                    TEAM_IDENTIFIER_KEY + "," +
                    TEAM_ROLE_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code ATTACH_TEAM_TO_LINKS_QUERY} the query used to attach links to the team
     */
    protected static final String ATTACH_TEAM_TO_LINKS_QUERY =
            "REPLACE INTO " + TEAMS_LINKS_TABLE +
                    "(" +
                    LINK_IDENTIFIER_KEY + "," +
                    TEAM_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";
    /**
     * {@code DETACH_TEAM_FROM_LINKS_QUERY} the query used to detach links from a team
     */
    private static final String DETACH_TEAM_FROM_LINKS_QUERY =
            "DELETE FROM " + TEAMS_LINKS_TABLE + " WHERE "
                    + TEAM_IDENTIFIER_KEY + "='%s' " + "AND " + LINK_IDENTIFIER_KEY + " IN (";

    /**
     * {@code ATTACH_TEAM_TO_COLLECTIONS_QUERY} the query used to attach collections to the team
     */
    protected static final String ATTACH_TEAM_TO_COLLECTIONS_QUERY =
            "REPLACE INTO " + COLLECTIONS_TEAMS_TABLE +
                    "(" +
                    COLLECTION_IDENTIFIER_KEY + "," +
                    TEAM_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code DETACH_TEAM_FROM_COLLECTIONS_QUERY} the query used to detach collections from a team
     */
    private static final String DETACH_TEAM_FROM_COLLECTIONS_QUERY =
            "DELETE FROM " + COLLECTIONS_TEAMS_TABLE + " WHERE "
                    + TEAM_IDENTIFIER_KEY + "='%s' " + "AND " + COLLECTION_IDENTIFIER_KEY + " IN (";

    /**
     * {@code teamsRepository} instance for the teams repository
     */
    @Autowired
    private TeamsRepository teamsRepository;

    /**
     * Method to get the user's owned teams identifiers
     *
     * @param userId: the identifier of the user
     *
     * @return the identifiers of the owned user teams as {@link HashSet} of {@link String}
     */
    public HashSet<String> getUserTeams(String userId) {
        return teamsRepository.getUserTeams(userId);
    }

    /**
     * Method to get the user's owned teams
     *
     * @param userId: the identifier of the owner
     *
     * @return the user teams as {@link List} of {@link Team}
     */
    public List<Team> getUserOwnedTeams(String userId) {
        return teamsRepository.getUserOwnedTeams(userId);
    }

    /**
     * Method to get all the user's teams
     *
     * @param userId: the identifier of the owner
     *
     * @return the user teams as {@link List} of {@link Team}
     */
    public List<Team> getAllUserTeams(String userId) {
        return teamsRepository.getAllUserTeams(userId);
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
     * @param userId: the user identifier of the team owner
     * @param teamId: the identifier of the team
     * @param payload: the payload with the details of the new team
     */
    public void createTeam(String userId, String teamId, TeamPayload payload) throws IOException {
        MultipartFile logo = payload.logo_pic;
        String logoUrl = createLogoResource(logo, teamId + System.currentTimeMillis());
        teamsRepository.saveTeam(teamId, payload.title, logoUrl, payload.description, userId);
        List<String> members = JsonHelper.toList(payload.members.put(userId));
        executeInsertBatch(ADD_MEMBERS_QUERY, TUPLE_VALUES_SLICE, members, query -> {
            int index = 1;
            TeamRole role = VIEWER;
            for (String member : members) {
                if(member.equals(userId))
                    role = ADMIN;
                query.setParameter(index++, member);
                query.setParameter(index++, teamId);
                query.setParameter(index++, role.name());
            }
        });
        saveResource(logo, logoUrl);
    }

    /**
     * Method to edit a team
     *
     * @param userId: the user identifier of the team owner
     * @param team: the team to edit
     * @param payload: the payload with the details of the team
     */
    public void editTeam(String userId, Team team, TeamPayload payload) throws IOException {
        String teamId = team.getId();
        MultipartFile logo = payload.logo_pic;
        boolean logoChanged = logo != null;
        String logoUrl;
        if(logoChanged)
            logoUrl = createLogoResource(logo, teamId + System.currentTimeMillis());
        else
            logoUrl = team.getLogoPic();
        teamsRepository.editTeam(teamId, payload.title, logoUrl, payload.description, userId);
        List<String> members = JsonHelper.toList(payload.members.put(userId));
        manageAttachments(getEditWorkflow(team), TUPLE_VALUES_SLICE, teamId, members, getBatchQuery(team, members));
        if(logoChanged) {
            deleteLogoResource(teamId);
            saveResource(logo, logoUrl);
        }
    }

    /**
     * Method to get the workflow to manage the team's attachments
     * @param team: the team where manage the attachments
     * @return the attachments workflow as {@link AttachmentsManagementWorkflow}
     */
    private AttachmentsManagementWorkflow getEditWorkflow(Team team) {
        return new AttachmentsManagementWorkflow() {

            @Override
            public List<String> getIds() {
                return team.getMembersIds();
            }

            @Override
            public String insertQuery() {
                return REPLACE_MEMBERS_QUERY;
            }

            @Override
            public String deleteQuery() {
                return REMOVE_MEMBERS_FROM_TEAM_QUERY;
            }

        };
    }

    /**
     * Method to get the batch query to manage the team members
     * @param team: the team where the batch query is to execute
     * @param members: the members list
     * @return the batch query as {@link BatchQuery}
     */
    private BatchQuery getBatchQuery(Team team, List<String> members) {
        String teamId = team.getId();
        HashSet<String> payloadMembers = new HashSet<>(members);
        return query -> {
            int index = 1;
            for (RefyTeamMember member : team.getMembers()) {
                String memberId = member.getId();
                if(payloadMembers.contains(memberId)) {
                    query.setParameter(index++, memberId);
                    query.setParameter(index++, teamId);
                    query.setParameter(index++, member.getRole().name());
                }
            }
            for (String member : members) {
                if(!team.hasMember(member)) {
                    query.setParameter(index++, member);
                    query.setParameter(index++, teamId);
                    query.setParameter(index++, VIEWER.name());
                }
            }
        };
    }

    /**
     * Method to manage the links attached to a team
     *
     * @param teamId: the identifier of the team
     * @param links: the links to attach or detach from a team
     */
    public void manageTeamLinks(String teamId, List<String> links) {
        Team team = teamsRepository.findById(teamId).orElseThrow();
        manageAttachments(
                new AttachmentsManagementWorkflow() {

                    @Override
                    public List<String> getIds() {
                        return team.getLinkIds();
                    }

                    @Override
                    public String insertQuery() {
                        return ATTACH_TEAM_TO_LINKS_QUERY;
                    }

                    @Override
                    public String deleteQuery() {
                        return DETACH_TEAM_FROM_LINKS_QUERY;
                    }

                },
                teamId,
                links
        );
    }

    /**
     * Method to manage the collections shared with a team
     *
     * @param teamId: the identifier of the team
     * @param collections: the collections to attach or detach from a team
     */
    public void manageTeamCollections(String teamId, List<String> collections) {
        Team team = teamsRepository.findById(teamId).orElseThrow();
        manageAttachments(
                new AttachmentsManagementWorkflow() {

                    @Override
                    public List<String> getIds() {
                        return team.getCollectionsIds();
                    }

                    @Override
                    public String insertQuery() {
                        return ATTACH_TEAM_TO_COLLECTIONS_QUERY;
                    }

                    @Override
                    public String deleteQuery() {
                        return DETACH_TEAM_FROM_COLLECTIONS_QUERY;
                    }

                },
                teamId,
                collections
        );
    }

    /**
     * Method change the role of a team member
     * @param teamId: the identifier of the team
     * @param memberId: the identifier of the member
     * @param role: the role to set to the member
     */
    public void changeMemberRole(String teamId, String memberId, TeamRole role) {
        teamsRepository.changeMemberRole(memberId, teamId, role);
    }

    /**
     * Method to remove a member from a team
     *
     * @param teamId: the identifier of the team
     * @param memberId: the identifier of the member
     */
    public void removeMember(String teamId, String memberId) {
        teamsRepository.removeMember(memberId, teamId);
    }

    /**
     * Method to delete a team
     *
     * @param teamId: the identifier of the team to delete
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
     * @param title: the title of the team
     * @param logo_pic: the logo picture of the team
     * @param description: the description of the team
     * @param members: the members of the team
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
            boolean validPayload = isTitleValid(title) && isDescriptionValid(description) && !members.isEmpty();
            if(validateLogoPic)
                return validPayload && (logo_pic != null && !logo_pic.isEmpty());
            return validPayload;
        }

    }

}
