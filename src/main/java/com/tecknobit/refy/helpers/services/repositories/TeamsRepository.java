package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.records.RefyItem.*;
import static com.tecknobit.refycore.records.RefyUser.TEAMS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.*;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TEAM_ROLE_KEY;

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
    List<Team> getUserOwnedTeams(
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
                    + " AND t." + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY + " LIMIT 1",
            nativeQuery = true
    )
    Team getTeamIfAllowed(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + TEAMS_KEY + "(" +
                    TEAM_IDENTIFIER_KEY + "," +
                    TITLE_KEY + "," +
                    LOGO_PIC_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + TEAM_IDENTIFIER_KEY + "," +
                    ":" + TITLE_KEY + "," +
                    ":" + LOGO_PIC_KEY + "," +
                    ":" + DESCRIPTION_KEY + "," +
                    ":" + OWNER_KEY +
                    ")",
            nativeQuery = true
    )
    void saveTeam(
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(TITLE_KEY) String title,
            @Param(LOGO_PIC_KEY) String logoPic,
            @Param(DESCRIPTION_KEY) String description,
            @Param(OWNER_KEY) String owner
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + TEAMS_KEY + " SET " +
                    TITLE_KEY + "=:" + TITLE_KEY + "," +
                    LOGO_PIC_KEY + "=:" + LOGO_PIC_KEY + "," +
                    DESCRIPTION_KEY + "=:" + DESCRIPTION_KEY +
                    " WHERE " + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY + " AND " + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    void editTeam(
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(TITLE_KEY) String title,
            @Param(LOGO_PIC_KEY) String logoPic,
            @Param(DESCRIPTION_KEY) String description,
            @Param(OWNER_KEY) String owner
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + MEMBERS_KEY + " SET " +
                    TEAM_ROLE_KEY + "=" + ":#{#" + TEAM_ROLE_KEY + ".name()}" +
                    " WHERE " + OWNER_KEY + "=:" + OWNER_KEY
                    + " AND " + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void updateMemberRole(
            @Param(OWNER_KEY) String owner,
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(TEAM_ROLE_KEY) TeamRole role
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + MEMBERS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY + " AND "
                    + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeMember(
            @Param(OWNER_KEY) String owner,
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + TEAMS_KEY + " WHERE " + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteTeam(
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

}
