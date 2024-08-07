package com.tecknobit.refy.helpers.services;

import com.tecknobit.refy.helpers.services.repositories.TeamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class TeamsHelper {

    @Autowired
    private TeamsRepository teamsRepository;

    public HashSet<String> getUserTeams(String userId) {
        return teamsRepository.getUserTeams(userId);
    }

}
