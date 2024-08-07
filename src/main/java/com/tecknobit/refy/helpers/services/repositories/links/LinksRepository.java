package com.tecknobit.refy.helpers.services.repositories.links;

import com.tecknobit.refycore.records.links.RefyLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_LINKS_TABLE;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.RefyUser.LINKS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_KEY;

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
    List<RefyLink> getUserLinks(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

}
