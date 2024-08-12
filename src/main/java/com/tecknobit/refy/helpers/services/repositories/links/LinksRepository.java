package com.tecknobit.refy.helpers.services.repositories.links;

import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_LINKS_TABLE;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.RefyUser.DISCRIMINATOR_VALUE_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.Team.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.Team.TITLE_KEY;
import static com.tecknobit.refycore.records.Team.*;
import static com.tecknobit.refycore.records.links.RefyLink.*;

@Service
@Repository
public interface LinksRepository extends LinksBaseRepository<RefyLink> {

    @Query(
            value = "SELECT " + LINK_IDENTIFIER_KEY + " FROM " + LINKS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    HashSet<String> getUserLinks(
            @Param(OWNER_KEY) String owner
    );

    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND dtype='" + LINK_KEY + "'",
            nativeQuery = true
    )
    List<RefyLink> getUserOwnedLinks(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND dtype='" + LINK_KEY + "'" +
                    " UNION" +
                    " SELECT l.* FROM " + LINKS_KEY + " AS l INNER JOIN " + MEMBERS_KEY + " ON l." + OWNER_KEY + "=" +
                    MEMBERS_KEY + "." + OWNER_KEY + " INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON " + MEMBERS_KEY +
                    "." + TEAM_IDENTIFIER_KEY + "=" + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY +
                    " INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY +
                    "=" + COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " WHERE l." + OWNER_KEY + "=:" +
                    USER_IDENTIFIER_KEY + " AND " + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=" +
                    MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<RefyLink> getAllUserLinks(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + LINKS_KEY + "(" +
                    DISCRIMINATOR_VALUE_KEY + "," +
                    LINK_IDENTIFIER_KEY + "," +
                    TITLE_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    REFERENCE_LINK_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + DISCRIMINATOR_VALUE_KEY + "," +
                    ":" + LINK_IDENTIFIER_KEY + "," +
                    ":" + TITLE_KEY + "," +
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
            @Param(DESCRIPTION_KEY) String description,
            @Param(REFERENCE_LINK_KEY) String referenceLink,
            @Param(OWNER_KEY) String owner
    );

    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND l." +
                    LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY +
                    " UNION" +
                    " SELECT l.* FROM " + LINKS_KEY + " AS l INNER JOIN " + MEMBERS_KEY + " ON l." + OWNER_KEY + "=" +
                    MEMBERS_KEY + "." + OWNER_KEY + " INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON " + MEMBERS_KEY +
                    "." + TEAM_IDENTIFIER_KEY + "=" + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY +
                    " INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY +
                    "=" + COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " WHERE l." + OWNER_KEY + "=:" +
                    USER_IDENTIFIER_KEY + " AND " + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY + "=" +
                    MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY + " AND l." + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY
                    + " LIMIT 1",
            nativeQuery = true
    )
    RefyLink getLinkIfAllowed(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + LINKS_KEY + " SET " +
                    TITLE_KEY + "=:" + TITLE_KEY + "," +
                    DESCRIPTION_KEY + "=:" + DESCRIPTION_KEY + "," +
                    REFERENCE_LINK_KEY + "=:" + REFERENCE_LINK_KEY +
                    " WHERE " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY + " AND " + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    void updateLink(
            @Param(LINK_IDENTIFIER_KEY) String linkId,
            @Param(TITLE_KEY) String title,
            @Param(DESCRIPTION_KEY) String description,
            @Param(REFERENCE_LINK_KEY) String referenceLink,
            @Param(OWNER_KEY) String owner
    );

}
