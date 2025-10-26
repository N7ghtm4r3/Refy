package com.tecknobit.refy.services.teams.repository;

import com.tecknobit.refy.services.shared.repositories.RefyItemsRepository;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refycore.enums.TeamRole;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.equinoxbackend.apis.database.SQLConstants._IN_BOOLEAN_MODE;
import static com.tecknobit.equinoxbackend.apis.database.SQLConstants._WHERE_;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code TeamsRepository} interface is useful to manage the queries of the {@link Team}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see RefyItemsRepository
 *
 */
@Service
@Repository
public interface TeamsRepository extends RefyItemsRepository<Team> {

    /**
     * Method to execute the query to get the user's owned teams identifiers
     *
     * @param owner The identifier of the user
     *
     * @return the identifiers of the owned user teams as {@link HashSet} of {@link String}
     */
    @Query(
            value = "SELECT " + TEAM_IDENTIFIER_KEY + " FROM " + TEAMS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    HashSet<String> getUserTeams(
            @Param(USER_IDENTIFIER_KEY) String owner
    );

    /**
     * Method to count the user's owned teams
     *
     * @param owner The identifier of the owner
     * @return the count of teams as {@code long}
     */
    @Query(
            value = "SELECT DISTINCT COUNT(*) " + "FROM " + TEAMS_KEY + " as t INNER JOIN " + MEMBERS_KEY +
                    " ON t." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    _WHERE_ + "t." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY,
            nativeQuery = true
    )
    long countUserOwnedTeams(
            @Param(USER_IDENTIFIER_KEY) String owner
    );

    /**
     * Method to execute the query to get the user's owned teams
     *
     * @param owner The identifier of the owner
     * @param pageable The parameters to paginate the query
     *
     * @return the user teams as {@link List} of {@link Team}
     */
    @Query(
            value = "SELECT DISTINCT t.* " + "FROM " + TEAMS_KEY + " as t INNER JOIN " + MEMBERS_KEY +
                    " ON t." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    _WHERE_ + "t." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<Team> getUserOwnedTeams(
            @Param(USER_IDENTIFIER_KEY) String owner,
            Pageable pageable
    );

    /**
     * Method to count all the user's teams
     *
     * @param owner    The identifier of the owner
     * @param keywords The keywords used to filter the query to retrieve the teams
     * @return the count of teams as {@code long}
     */
    @Query(
            value = "SELECT COUNT(*) " + "FROM " + TEAMS_KEY + " as t INNER JOIN " + MEMBERS_KEY +
                    " ON t." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    _WHERE_ + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND ( " +
                    "    MATCH(t." + TITLE_KEY + ", t." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    ")",
            nativeQuery = true
    )
    long countAllUserTeams(
            @Param(USER_IDENTIFIER_KEY) String owner,
            @Param(KEYWORDS_KEY) String keywords
    );

    /**
     * Method to execute the query to get all the user's teams
     *
     * @param owner The identifier of the owner
     * @param pageable The parameters to paginate the query
     * @param keywords The keywords used to filter the query to retrieve the teams
     *
     * @return the user teams as {@link List} of {@link Team}
     */
    @Query(
            value = "SELECT t.* " + "FROM " + TEAMS_KEY + " as t INNER JOIN " + MEMBERS_KEY +
                    " ON t." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    _WHERE_ + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND ( " +
                    "    MATCH(t." + TITLE_KEY + ", t." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    ")" +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<Team> getAllUserTeams(
            @Param(USER_IDENTIFIER_KEY) String owner,
            @Param(KEYWORDS_KEY) String keywords,
            Pageable pageable
    );

    /**
     * Method to execute the query to get a team if the user is authorized
     *
     * @param userId The identifier of the user
     * @param teamId The team identifier
     *
     * @return the team if the user is authorized as {@link Team}
     */
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

    /**
     * Method to count the teams where the collection is shared
     *
     * @param collectionId The identifier of the collection from retrieve the teams where is shared
     *
     * @return the count of teams as {@code long}
     */
    @Query(
            value = "SELECT COUNT(*) FROM " + TEAMS_KEY + " AS t INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON t." +
                    TEAM_IDENTIFIER_KEY + " = " + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY +
                    _WHERE_ + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY,
            nativeQuery = true
    )
    long countCollectionTeams(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId
    );

    /**
     * Method to execute the query to get the teams where the collection is shared
     *
     * @param collectionId The identifier of the collection from retrieve the teams where is shared
     * @param pageable     The parameters to paginate the query
     * @return the user teams as {@link List} of {@link Team}
     */
    @Query(
            value = "SELECT t.* FROM " + TEAMS_KEY + " AS t INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON t." +
                    TEAM_IDENTIFIER_KEY + "=" + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY +
                    _WHERE_ + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<Team> getCollectionTeams(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId,
            Pageable pageable
    );

    /**
     * Method to execute the query to save a team
     *
     * @param teamId The identifier of the team
     * @param title The title of the team
     * @param logoPic The logo picture of the team
     * @param description The description of the team
     * @param timestamp The date when the item has been inserted in the system
     * @param owner The owner of the team
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + TEAMS_KEY + "(" +
                    TEAM_IDENTIFIER_KEY + "," +
                    TITLE_KEY + "," +
                    LOGO_PIC_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    DATE_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + TEAM_IDENTIFIER_KEY + "," +
                    ":" + TITLE_KEY + "," +
                    ":" + LOGO_PIC_KEY + "," +
                    ":" + DESCRIPTION_KEY + "," +
                    ":" + DATE_KEY + "," +
                    ":" + OWNER_KEY +
                    ")",
            nativeQuery = true
    )
    void saveTeam(
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(TITLE_KEY) String title,
            @Param(LOGO_PIC_KEY) String logoPic,
            @Param(DESCRIPTION_KEY) String description,
            @Param(DATE_KEY) long timestamp,
            @Param(OWNER_KEY) String owner
    );

    /**
     * Method to execute the query to edit a team
     *
     * @param teamId The identifier of the team
     * @param title The title of the team
     * @param logoPic The logo picture of the team
     * @param description The description of the team
     * @param owner The owner of the team
     */
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

    /**
     * Method to execute the query to remove a collection from a team
     *
     * @param teamId       The identifier of the team
     * @param collectionId The identifier of the collection
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_TEAMS_TABLE + _WHERE_ +
                    TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY +
                    " AND " + COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeCollectionFromTeam(
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId
    );

    /**
     * Method to execute the query remove a link from a team
     *
     * @param teamId The identifier of the team
     * @param linkId The identifier of the link
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + TEAMS_LINKS_TABLE + _WHERE_ +
                    TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY +
                    " AND " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeLinkFromTeam(
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    /**
     * Method to execute the query to change a role of a member
     *
     * @param teamId The identifier of the team
     * @param member The member to change the role
     * @param role The role of the member to set
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + MEMBERS_KEY + " SET " +
                    TEAM_ROLE_KEY + "=" + ":#{#" + TEAM_ROLE_KEY + ".name()}" +
                    " WHERE " + OWNER_KEY + "=:" + OWNER_KEY
                    + " AND " + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeMemberRole(
            @Param(OWNER_KEY) String member,
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(TEAM_ROLE_KEY) TeamRole role
    );

    /**
     * Method to execute the query to remove a member from a team
     *
     * @param teamId The identifier of the team
     * @param member The member to remove
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + MEMBERS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY + " AND "
                    + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeMember(
            @Param(OWNER_KEY) String member,
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

    /**
     * Method to execute the query to delete a team
     *
     * @param teamId The identifier of the team
     */
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
