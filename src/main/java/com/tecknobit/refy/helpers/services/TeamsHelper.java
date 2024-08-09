package com.tecknobit.refy.helpers.services;

import com.tecknobit.refy.helpers.resources.RefyResourcesManager;
import com.tecknobit.refy.helpers.services.repositories.TeamsRepository;
import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
        ArrayList<Object> members = (ArrayList<Object>) payload.members.toList();
        members.add(userId);
        executeInsertBatch(ADD_MEMBERS_QUERY, VALUES_SLICE, members, query -> {
            int index = 1;
            TeamRole role = VIEWER;
            for (Object member : members) {
                if(member.equals(userId))
                    role = ADMIN;
                query.setParameter(index++, member);
                query.setParameter(index++, teamId);
                query.setParameter(index++, role.name());
            }
        });
        saveResource(logo, logoUrl);
    }

    public record TeamPayload(String title, MultipartFile logo_pic, String description, JSONArray members) {

        public boolean isValidTeamPayload() {
            return isTitleValid(title) && (logo_pic != null && !logo_pic.isEmpty()) && isDescriptionValid(description)
                    && !members.isEmpty();
        }

    }

}
