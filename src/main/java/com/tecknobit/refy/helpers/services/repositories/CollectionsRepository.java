package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.refycore.records.LinksCollection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.LinksCollection.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.LinksCollection.TITLE_KEY;
import static com.tecknobit.refycore.records.LinksCollection.*;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.*;

@Service
@Repository
public interface CollectionsRepository extends RefyItemsRepository<LinksCollection> {

    @Query(
            value = "SELECT " + IDENTIFIER_KEY + " FROM " + COLLECTIONS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    HashSet<String> getUserCollections(
            @Param(OWNER_KEY) String owner
    );

    @Query(
            value = "SELECT c.* FROM " + COLLECTIONS_KEY + " as c WHERE c." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<LinksCollection> getUserOwnedCollections(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    @Query(
            value = "SELECT c.* FROM " + COLLECTIONS_KEY + " as c WHERE c." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " UNION " +
                    "SELECT c.* FROM " + COLLECTIONS_KEY + " as c INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON c." +
                    IDENTIFIER_KEY + " = " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " INNER JOIN " +
                    MEMBERS_KEY + " ON " + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY + " WHERE " + MEMBERS_KEY + "." +
                    OWNER_KEY + "=:" + USER_IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<LinksCollection> getAllUserCollections(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + COLLECTIONS_KEY + "(" +
                    IDENTIFIER_KEY + "," +
                    COLLECTION_COLOR_KEY + "," +
                    TITLE_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + IDENTIFIER_KEY + "," +
                    ":" + COLLECTION_COLOR_KEY + "," +
                    ":" + TITLE_KEY + "," +
                    ":" + DESCRIPTION_KEY + "," +
                    ":" + OWNER_KEY +
                    ")",
            nativeQuery = true
    )
    void saveCollection(
            @Param(IDENTIFIER_KEY) String collectionId,
            @Param(COLLECTION_COLOR_KEY) String color,
            @Param(TITLE_KEY) String title,
            @Param(DESCRIPTION_KEY) String description,
            @Param(OWNER_KEY) String owner
    );

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
