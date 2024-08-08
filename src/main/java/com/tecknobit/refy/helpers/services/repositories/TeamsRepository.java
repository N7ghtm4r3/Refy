package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.refycore.records.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.RefyUser.TEAMS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.MEMBERS_KEY;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;

@Service
@Repository
public interface TeamsRepository extends RefyItemsRepository<Team> {

    @Query(
            value = "SELECT " + TEAM_IDENTIFIER_KEY + " FROM " + TEAMS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY,
            nativeQuery = true
    )
    HashSet<String> getUserTeams(
            @Param(USER_IDENTIFIER_KEY) String owner
    );

    @Query(
            value = "SELECT t.* " + "FROM " + TEAMS_KEY + " as t INNER JOIN " + MEMBERS_KEY
                    + " ON t." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY
                    + " WHERE " + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<Team> getAllUserTeams(
            @Param(USER_IDENTIFIER_KEY) String owner
    );

    @Query(
            value = "SELECT t.* " + "FROM " + TEAMS_KEY + " as t INNER JOIN " + MEMBERS_KEY
                    + " ON t." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY
                    + " WHERE " + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY
                    + " AND t." + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    Team getTeamIfAllowed(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

}
