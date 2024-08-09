package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.refy.helpers.resources.RefyResourcesManager;
import com.tecknobit.refy.helpers.services.repositories.TeamsRepository;
import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.Team.RefyTeamMember;
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
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.Team.MEMBERS_KEY;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TEAM_ROLE_KEY;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.ADMIN;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.VIEWER;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;

@Service
public class TeamsHelper extends RefyItemsHelper<Team> implements RefyResourcesManager {

    private static final String ADD_MEMBERS_QUERY =
            "INSERT INTO " + MEMBERS_KEY +
                    "(" +
                        OWNER_KEY + "," +
                        TEAM_IDENTIFIER_KEY + "," +
                        TEAM_ROLE_KEY +
                    ")" +
            " VALUES ";

    private static final String VALUES_SLICE = "(?, ?, ?)";

    private static final String DETACH_MEMBERS_FROM_TEAM_QUERY =
            "DELETE FROM " + MEMBERS_KEY + " WHERE "
                    + TEAM_IDENTIFIER_KEY + "='%s' " + "AND " + OWNER_KEY + " IN (";

    private static final String REPLACE_MEMBERS_QUERY =
            "REPLACE INTO " + MEMBERS_KEY +
                    "(" +
                    OWNER_KEY + "," +
                    TEAM_IDENTIFIER_KEY + "," +
                    TEAM_ROLE_KEY +
                    ")" +
                    " VALUES ";

    @Autowired
    private TeamsRepository teamsRepository;

    public HashSet<String> getUserTeams(String userId) {
        return teamsRepository.getUserTeams(userId);
    }

    public List<Team> getAllUserTeams(String userId) {
        return teamsRepository.getAllUserTeams(userId);
    }

    @Override
    public Team getItemIfAllowed(String userId, String teamId) {
        return teamsRepository.getTeamIfAllowed(userId, teamId);
    }

    public void createTeam(String userId, String teamId, TeamPayload payload) throws IOException {
        MultipartFile logo = payload.logo_pic;
        String logoUrl = createLogoResource(logo, teamId + System.currentTimeMillis());
        teamsRepository.saveTeam(teamId, payload.title, logoUrl, payload.description, userId);
        List<String> members = JsonHelper.toList(payload.members.put(userId));
        executeInsertBatch(ADD_MEMBERS_QUERY, VALUES_SLICE, members, query -> {
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

    public void editTeam(String userId, Team team, TeamPayload payload) throws IOException {
        String teamId = team.getId();
        MultipartFile logo = payload.logo_pic;
        String logoUrl = createLogoResource(logo, teamId + System.currentTimeMillis());
        teamsRepository.editTeam(teamId, payload.title, logoUrl, payload.description, userId);
        List<String> members = JsonHelper.toList(payload.members.put(userId));
        manageAttachments(getEditWorkflow(team), VALUES_SLICE, teamId, members, getBatchQuery(team, members));
        deleteLogoResource(teamId);
        saveResource(logo, logoUrl);
    }

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
                return DETACH_MEMBERS_FROM_TEAM_QUERY;
            }

        };
    }

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

    public record TeamPayload(String title, MultipartFile logo_pic, String description, JSONArray members) {

        public boolean isValidTeamPayload() {
            return isTitleValid(title) && (logo_pic != null && !logo_pic.isEmpty()) && isDescriptionValid(description)
                    && !members.isEmpty();
        }

    }

}
