package com.tecknobit.refy.helpers.services;

import com.tecknobit.refy.helpers.services.repositories.TeamsRepository;
import com.tecknobit.refycore.records.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class TeamsHelper extends RefyItemsHelper<Team> {

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

}
