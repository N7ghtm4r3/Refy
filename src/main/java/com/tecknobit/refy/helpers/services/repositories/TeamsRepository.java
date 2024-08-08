package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.refycore.records.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;

import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.RefyUser.TEAMS_KEY;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;

@Service
@Repository
public interface TeamsRepository extends RefyItemsRepository<Team> {

    @Query(
            value = "SELECT " + TEAM_IDENTIFIER_KEY + " FROM " + TEAMS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    HashSet<String> getUserTeams(
            @Param(OWNER_KEY) String owner
    );

}
