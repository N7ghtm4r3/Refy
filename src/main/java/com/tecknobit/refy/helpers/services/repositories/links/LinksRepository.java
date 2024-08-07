package com.tecknobit.refy.helpers.services.repositories.links;

import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

public interface LinksRepository extends JpaRepository<RefyLink, String> {

    @Query(
            value = "SELECT * FROM " + LINKS_KEY
                    + " WHERE " + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND dtype='" + LINK_KEY + "'"
                    + " UNION "
                    + " SELECT l.* FROM " + LINKS_KEY + " as l INNER JOIN " + MEMBERS_KEY + " ON l." + OWNER_KEY + "=:"
                    + USER_IDENTIFIER_KEY + " INNER JOIN " + TEAMS_LINKS_TABLE + " ON " + MEMBERS_KEY + "."
                    + TEAM_IDENTIFIER_KEY + "=" + TEAMS_LINKS_TABLE + "." + TEAM_IDENTIFIER_KEY
                    + " UNION "
                    + " SELECT l.* FROM " + LINKS_KEY + " as l INNER JOIN " + MEMBERS_KEY + " ON l." + OWNER_KEY + "=:"
                    + USER_IDENTIFIER_KEY + " INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON " + MEMBERS_KEY + "."
                    + TEAM_IDENTIFIER_KEY + "=" + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY
                    + " INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "."
                    + COLLECTION_IDENTIFIER_KEY + "=" + COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY,
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
            value = "SELECT * FROM " + LINKS_KEY
                    + " WHERE " + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY  + " AND "
                    + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY + " AND dtype='" + LINK_KEY + "'"
                    + " UNION "
                    + " SELECT l.* FROM " + LINKS_KEY + " as l INNER JOIN " + MEMBERS_KEY + " ON l." + OWNER_KEY + "=:"
                    + USER_IDENTIFIER_KEY + " INNER JOIN " + TEAMS_LINKS_TABLE + " ON " + MEMBERS_KEY + "."
                    + TEAM_IDENTIFIER_KEY + "=" + TEAMS_LINKS_TABLE + "." + TEAM_IDENTIFIER_KEY
                    + " UNION "
                    + " SELECT l.* FROM " + LINKS_KEY + " as l INNER JOIN " + MEMBERS_KEY + " ON l." + OWNER_KEY + "=:"
                    + USER_IDENTIFIER_KEY + " INNER JOIN " + COLLECTIONS_TEAMS_TABLE + " ON " + MEMBERS_KEY + "."
                    + TEAM_IDENTIFIER_KEY + "=" + COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY
                    + " INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "."
                    + COLLECTION_IDENTIFIER_KEY + "=" + COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY,
            nativeQuery = true
    )
    RefyLink getUserLinkIfOwner(
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
