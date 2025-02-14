package com.tecknobit.refy.services.collections.repository;

import com.tecknobit.refy.services.collections.entity.LinksCollection;
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

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.refy.configuration.indexes.IndexesCreator._IN_BOOLEAN_MODE;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code CollectionsRepository} interface is useful to manage the queries of the {@link LinksCollection}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see RefyItemsRepository
 *
 */
@Service
@Repository
public interface CollectionsRepository extends RefyItemsRepository<LinksCollection> {

    /**
     * Method to execute the query to get the user's owned collections identifiers
     *
     * @param owner The identifier of the user
     *
     * @return the identifiers of the owned user collections as {@link HashSet} of {@link String}
     */
    @Query(
            value = "SELECT " + IDENTIFIER_KEY + " FROM " + COLLECTIONS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    HashSet<String> getUserCollections(
            @Param(OWNER_KEY) String owner
    );

    /**
     * Method to count get the user owned collections
     *
     * @param userId The identifier of the user
     *
     * @return the user collections as {@code long}
     */
    @Query(
            value = "SELECT COUNT(*) FROM " + COLLECTIONS_KEY + _WHERE_ +
                    OWNER_KEY + "=:" + USER_IDENTIFIER_KEY,
            nativeQuery = true
    )
    long countUserOwnedCollections(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    /**
     * Method to execute the query to get the user owned collections
     *
     * @param userId The identifier of the user
     * @param pageable The parameters to paginate the query
     *
     * @return the user collections as {@link List} of {@link LinksCollection}
     */
    @Query(
            value = "SELECT * FROM " + COLLECTIONS_KEY + _WHERE_ +
                    OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<LinksCollection> getUserOwnedCollections(
            @Param(USER_IDENTIFIER_KEY) String userId,
            Pageable pageable
    );

    /**
     * Method to count all the user's collections, included the collections shared in the teams
     *
     * @param userId   The identifier of the user
     * @param keywords The keywords used to filter the query to retrieve the items
     * @return the user collections as {@code long}
     */
    @Query(
            value = "SELECT ( " +
                    "    ( " +
                    "        SELECT COUNT(*) " +
                    "        FROM " + COLLECTIONS_KEY + " AS c " + _WHERE_ + " c." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    "        AND ( " +
                    "            MATCH(c." + TITLE_KEY + ", c." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "            OR :" + KEYWORDS_KEY + " =''" +
                    "        ) " +
                    "    ) + " +
                    "    ( " +
                    "        SELECT COUNT(*) " +
                    "        FROM " + COLLECTIONS_KEY + " AS c " +
                    "        INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON c." + IDENTIFIER_KEY + " = " +
                    COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " " +
                    "        INNER JOIN " + MEMBERS_KEY + " ON " + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY + " " +
                    "        WHERE " + MEMBERS_KEY + "." + OWNER_KEY + " = :" + USER_IDENTIFIER_KEY +
                    "        AND ( " +
                    "            MATCH(c." + TITLE_KEY + ", c." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "            OR :" + KEYWORDS_KEY + "=''" +
                    "        ) " +
                    "    ) " +
                    ")",
            nativeQuery = true
    )
    long countAllUserCollections(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(KEYWORDS_KEY) String keywords
    );

    /**
     * Method to execute the query to get all the user's collections, included the collections shared in the teams
     *
     * @param userId The identifier of the user
     * @param keywords The keywords used to filter the query to retrieve the items
     * @param pageable The parameters to paginate the query
     *
     * @return the user collections as {@link List} of {@link LinksCollection}
     */
    @Query(
            value = "SELECT c.* FROM " + COLLECTIONS_KEY + " AS c " + _WHERE_ + " c."
                    + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND ( " +
                    "    MATCH(c." + TITLE_KEY + ", c." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    ") UNION " +
                    "SELECT c.* FROM " + COLLECTIONS_KEY + " AS c INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON c." +
                    IDENTIFIER_KEY + " = " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " INNER JOIN " +
                    MEMBERS_KEY + " ON " + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    " WHERE " + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND ( " +
                    "    MATCH(c." + TITLE_KEY + ", c." + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + " = '' " +
                    ")" +
                    " ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<LinksCollection> getAllUserCollections(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(KEYWORDS_KEY) String keywords,
            Pageable pageable
    );

    /**
     * Method to execute the query to save a collection
     *
     * @param collectionId The identifier of the collection
     * @param color The color of the collection
     * @param title The title of the collection
     * @param description The description of the collection
     * @param timestamp The date when the item has been inserted in the system
     * @param owner The owner of the collection
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + COLLECTIONS_KEY + "(" +
                    IDENTIFIER_KEY + "," +
                    COLLECTION_COLOR_KEY + "," +
                    TITLE_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    DATE_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + IDENTIFIER_KEY + "," +
                    ":" + COLLECTION_COLOR_KEY + "," +
                    ":" + TITLE_KEY + "," +
                    ":" + DESCRIPTION_KEY + "," +
                    ":" + DATE_KEY + "," +
                    ":" + OWNER_KEY +
                    ")",
            nativeQuery = true
    )
    void saveCollection(
            @Param(IDENTIFIER_KEY) String collectionId,
            @Param(COLLECTION_COLOR_KEY) String color,
            @Param(TITLE_KEY) String title,
            @Param(DESCRIPTION_KEY) String description,
            @Param(DATE_KEY) long timestamp,
            @Param(OWNER_KEY) String owner
    );

    /**
     * Method to execute the query to get a collection if the user is authorized
     *
     * @param userId The identifier of the user
     * @param collectionId The collection identifier
     *
     * @return the collection if the user is authorized as {@link LinksCollection}
     */
    @Query(
            value = "SELECT c.* FROM " + COLLECTIONS_KEY + " as c WHERE c." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND c." + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY +
                    " UNION " +
                    "SELECT c.* FROM " + COLLECTIONS_KEY + " as c INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON c." +
                    IDENTIFIER_KEY + " = " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " INNER JOIN " +
                    MEMBERS_KEY + " ON " + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY + " WHERE " + MEMBERS_KEY + "." +
                    OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND c." + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    LinksCollection getCollectionIfAllowed(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(IDENTIFIER_KEY) String collectionId
    );

    /**
     * Method to execute the query to edit a collection
     *
     * @param collectionId The identifier of the collection
     * @param color The color of the collection
     * @param title The title of the collection
     * @param description The description of the collection
     * @param owner The owner of the collection
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + COLLECTIONS_KEY + " SET " +
                    COLLECTION_COLOR_KEY + "=:" + COLLECTION_COLOR_KEY + "," +
                    TITLE_KEY + "=:" + TITLE_KEY + "," +
                    DESCRIPTION_KEY + "=:" + DESCRIPTION_KEY +
                    " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY + " AND " + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    void updateCollection(
            @Param(IDENTIFIER_KEY) String collectionId,
            @Param(COLLECTION_COLOR_KEY) String color,
            @Param(TITLE_KEY) String title,
            @Param(DESCRIPTION_KEY) String description,
            @Param(OWNER_KEY) String owner
    );

    // TODO: 14/02/2025 TO COMMENT
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_LINKS_TABLE + _WHERE_ +
                    COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY + " AND " +
                    LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeLinkFromCollection(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    // TODO: 14/02/2025 TO COMMENT
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_TEAMS_TABLE + _WHERE_ +
                    COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY + " AND " +
                    TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeTeamFromCollection(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

    /**
     * Method to execute the query to delete a collection
     *
     * @param collectionId The identifier of the collection to delete
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteCollection(
            @Param(IDENTIFIER_KEY) String collectionId
    );

}
