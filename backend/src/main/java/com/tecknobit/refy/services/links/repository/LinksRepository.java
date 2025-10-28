package com.tecknobit.refy.services.links.repository;

import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.links.repository.LinksBaseRepository;
import com.tecknobit.refy.services.shared.repositories.RefyItemsRepository;
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
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.DISCRIMINATOR_VALUE_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code LinksRepository} interface is useful to manage the queries of the {@link RefyLink}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see RefyItemsRepository
 * @see LinksBaseRepository
 *
 */
@Service
@Repository
public interface LinksRepository extends LinksBaseRepository<RefyLink> {

    /**
     * Method to execute the query to get the user's owned links identifiers
     *
     * @param owner The identifier of the user
     *
     * @return the identifiers of the owned user links as {@link HashSet} of {@link String}
     */
    @Query(
            value = "SELECT " + LINK_IDENTIFIER_KEY + " FROM " + LINKS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    HashSet<String> getUserLinks(
            @Param(OWNER_KEY) String owner
    );

    /**
     * Method to count the user's owned links
     *
     * @param userId The identifier of the user
     * @return the count of the user links as {@code long}
     */
    @Query(
            value = "SELECT COUNT(*) FROM " + LINKS_KEY + _WHERE_
                    + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND dtype='" + LINK_KEY + "'",
            nativeQuery = true
    )
    long countUserOwnedLinks(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    /**
     * Method to execute the query to get the user's owned links
     *
     * @param userId The identifier of the user
     * @param pageable  The parameters to paginate the query
     *
     * @return the user links as {@link List} of {@link RefyLink}
     */
    @Query(
            value = "SELECT * FROM " + LINKS_KEY + _WHERE_
                    + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND dtype='" + LINK_KEY + "'" +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<RefyLink> getUserOwnedLinks(
            @Param(USER_IDENTIFIER_KEY) String userId,
            Pageable pageable
    );

    /**
     * Method to count all the user's links, included the links shared in the teams and in the
     * collections shared in the teams
     *
     * @param userId The identifier of the user
     * @return the count of the user links as {@code long}
     */
    @Query(
            value = "SELECT ( " +
                    "SELECT COUNT(*) FROM " + LINKS_KEY + " AS l " + _WHERE_ +
                    "  l." + OWNER_KEY + " = :" + USER_IDENTIFIER_KEY +
                    "  AND dtype = '" + LINK_KEY + "'" +
                    "  AND ( " +
                    "    MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    "  )" +
                    ") + ( " +
                    "SELECT COUNT(*) " +
                    "  FROM " + LINKS_KEY + " AS l " +
                    " LEFT JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY
                    + " = l." + LINK_IDENTIFIER_KEY +
                    " LEFT JOIN " + TEAMS_LINKS_TABLE + " ON " + TEAMS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY +
                    " = l." + LINK_IDENTIFIER_KEY +
                    " LEFT JOIN " + COLLECTIONS_TEAMS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY +
                    " = " + COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY +
                    " LEFT JOIN " + MEMBERS_KEY + " ON " + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=" +
                    MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    " OR " + TEAMS_LINKS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    "  WHERE " + MEMBERS_KEY + "." + OWNER_KEY + " = :" + USER_IDENTIFIER_KEY +
                    "  AND l.dtype = '" + LINK_KEY + "' " +
                    "  AND ( " +
                    "    MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    "  )" +
                    ")",
            nativeQuery = true
    )
    long countAllUserLinks(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(KEYWORDS_KEY) String keywords
    );

    /**
     * Method to execute the query to get all the user's links, included the links shared in the teams and in the
     * collections shared in the teams
     *
     * @param userId The identifier of the user
     * @param keywords The keywords used to filter the query to retrieve the items
     * @param pageable  The parameters to paginate the query
     *
     * @return the user links as {@link List} of {@link RefyLink}
     */
    @Query(
            value = "SELECT l.* " +
                    "FROM " + LINKS_KEY + " AS l " +
                    "WHERE l." + OWNER_KEY + " = :" + USER_IDENTIFIER_KEY +
                    " AND dtype = '" + LINK_KEY + "' " +
                    " AND ( " +
                    "     MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "     OR :" + KEYWORDS_KEY + " = '' " +
                    " ) " +
                    "UNION " +
                    "SELECT l.* " +
                    "FROM " + LINKS_KEY + " AS l " +
                    "LEFT  JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY +
                    " = l." + LINK_IDENTIFIER_KEY +
                    " LEFT  JOIN " + TEAMS_LINKS_TABLE + " ON " + TEAMS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY +
                    " = l." + LINK_IDENTIFIER_KEY +
                    " LEFT  JOIN " + COLLECTIONS_TEAMS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY +
                    " = " + COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY +
                    " LEFT  JOIN " + MEMBERS_KEY + " ON " + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=" +
                    MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    " OR " + TEAMS_LINKS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=" + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    " WHERE " + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND l.dtype = '" + LINK_KEY + "' " +
                    " AND ( " +
                    "     MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "     OR :" + KEYWORDS_KEY + " = '' " +
                    " ) " +
                    "ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<RefyLink> getAllUserLinks(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(KEYWORDS_KEY) String keywords,
            Pageable pageable
    );

    /**
     * Method to execute the query to save a link
     *
     * @param discriminatorValue The discriminator value
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param timestamp The date when the item has been inserted in the system
     * @param thumbnailPreview The url of the thumbnail preview of the link
     * @param description The description of the link
     * @param referenceLink The reference link value
     * @param owner The owner of the link
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + LINKS_KEY + "(" +
                    DISCRIMINATOR_VALUE_KEY + "," +
                    LINK_IDENTIFIER_KEY + "," +
                    TITLE_KEY + "," +
                    DATE_KEY + "," +
                    THUMBNAIL_PREVIEW_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    REFERENCE_LINK_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + DISCRIMINATOR_VALUE_KEY + "," +
                    ":" + LINK_IDENTIFIER_KEY + "," +
                    ":" + TITLE_KEY + "," +
                    ":" + DATE_KEY + "," +
                    ":" + THUMBNAIL_PREVIEW_KEY + "," +
                    ":" + DESCRIPTION_KEY + "," +
                    ":" + REFERENCE_LINK_KEY + "," +
                    ":" + OWNER_KEY +
                    ")",
            nativeQuery = true
    )
    void saveLink(
            @Param(DISCRIMINATOR_VALUE_KEY) String discriminatorValue,
            @Param(LINK_IDENTIFIER_KEY) String linkId,
            @Param(TITLE_KEY) String title,
            @Param(DATE_KEY) long timestamp,
            @Param(THUMBNAIL_PREVIEW_KEY) String thumbnailPreview,
            @Param(DESCRIPTION_KEY) String description,
            @Param(REFERENCE_LINK_KEY) String referenceLink,
            @Param(OWNER_KEY) String owner
    );

    /**
     * Method to execute the query to get a link if the owner is authorized
     *
     * @param userId The identifier of the user
     * @param linkId The link identifier
     *
     * @return the link if the user is authorized as {@link RefyLink}
     */
    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND l." +
                    LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY +
                    " UNION " +
                    "SELECT l.* FROM " + LINKS_KEY + " AS l INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " +
                    COLLECTIONS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY + " = l." + LINK_IDENTIFIER_KEY + " INNER JOIN " +
                    COLLECTIONS_TEAMS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " = " +
                    COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " INNER JOIN " + MEMBERS_KEY + " ON " +
                    COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY + " = " + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    " WHERE " + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND l." + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY + " AND dtype='" + LINK_KEY + "'",
            nativeQuery = true
    )
    RefyLink getLinkIfAllowed(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    /**
     * Method to execute the query to update a link
     *
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param thumbnailPreview The url of the thumbnail preview of the link
     * @param description The description of the link
     * @param referenceLink The reference link value
     * @param owner The owner of the link
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + LINKS_KEY + " SET " +
                    TITLE_KEY + "=:" + TITLE_KEY + "," +
                    THUMBNAIL_PREVIEW_KEY + "=:" + THUMBNAIL_PREVIEW_KEY + "," +
                    DESCRIPTION_KEY + "=:" + DESCRIPTION_KEY + "," +
                    REFERENCE_LINK_KEY + "=:" + REFERENCE_LINK_KEY +
                    " WHERE " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY + " AND " + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    void updateLink(
            @Param(LINK_IDENTIFIER_KEY) String linkId,
            @Param(TITLE_KEY) String title,
            @Param(THUMBNAIL_PREVIEW_KEY) String thumbnailPreview,
            @Param(DESCRIPTION_KEY) String description,
            @Param(REFERENCE_LINK_KEY) String referenceLink,
            @Param(OWNER_KEY) String owner
    );

    /**
     *  Method to count all the links shared in a collection
     *
     * @param collectionId The identifier of the collection from retrieve the links
     * @param keywords     The keywords used to filter the query to retrieve the items
     * @return the count of the collection links as {@code long}
     */
    @Query(
            value = "SELECT COUNT(*) " +
                    "FROM " + LINKS_KEY + " AS l " +
                    "INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY +
                    " = l." + LINK_IDENTIFIER_KEY + _WHERE_ +
                    COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY +
                    " AND dtype = '" + LINK_KEY + "' " +
                    "  AND ( " +
                    "    MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    "  )",
            nativeQuery = true
    )
    long countCollectionLinks(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @Param(KEYWORDS_KEY) String keywords
    );

    /**
     * Method to execute the query to get all the links shared in a collection
     *
     * @param collectionId The identifier of the collection from retrieve the links
     * @param keywords     The keywords used to filter the query to retrieve the items
     * @param pageable     The parameters to paginate the query
     * @return the collection links as {@link List} of {@link RefyLink}
     */
    @Query(
            value = "SELECT l.* " +
                    "FROM " + LINKS_KEY + " AS l " +
                    "INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY +
                    " = l." + LINK_IDENTIFIER_KEY + _WHERE_ +
                    COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY +
                    " AND dtype = '" + LINK_KEY + "' " +
                    "  AND ( " +
                    "    MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    "  )" +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<RefyLink> getCollectionLinks(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @Param(KEYWORDS_KEY) String keywords,
            Pageable pageable
    );

    /**
     * Method to count all the links shared in a team
     *
     * @param teamId   The identifier of the team from retrieve the links
     * @param keywords The keywords used to filter the query to retrieve the items
     * @return the count of the team links as {@code long}
     */
    @Query(
            value = "SELECT COUNT(*) " +
                    "FROM " + LINKS_KEY + " AS l " +
                    "INNER JOIN " + TEAMS_LINKS_TABLE + " ON " + TEAMS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY +
                    " = l." + LINK_IDENTIFIER_KEY + _WHERE_ +
                    TEAMS_LINKS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY +
                    " AND dtype = '" + LINK_KEY + "' " +
                    "  AND ( " +
                    "    MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    "  )",
            nativeQuery = true
    )
    long countTeamLinks(
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(KEYWORDS_KEY) String keywords
    );

    /**
     * Method to execute the query to get all the links shared in a team
     *
     * @param teamId   The identifier of the team from retrieve the links
     * @param keywords The keywords used to filter the query to retrieve the items
     * @param pageable The parameters to paginate the query
     * @return the team links as {@link List} of {@link RefyLink}
     */
    @Query(
            value = "SELECT l.* " +
                    "FROM " + LINKS_KEY + " AS l " +
                    "INNER JOIN " + TEAMS_LINKS_TABLE + " ON " + TEAMS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY +
                    " = l." + LINK_IDENTIFIER_KEY + _WHERE_ +
                    TEAMS_LINKS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY +
                    " AND dtype = '" + LINK_KEY + "' " +
                    "  AND ( " +
                    "    MATCH(l." + TITLE_KEY + ", l." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    "  )" +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<RefyLink> getTeamLinks(
            @Param(TEAM_IDENTIFIER_KEY) String teamId,
            @Param(KEYWORDS_KEY) String keywords,
            Pageable pageable
    );

}
