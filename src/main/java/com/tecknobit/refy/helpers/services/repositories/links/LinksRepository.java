package com.tecknobit.refy.helpers.services.repositories.links;

import com.tecknobit.refy.helpers.services.repositories.RefyItemsRepository;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * @param owner: the identifier of the user
     *
     * @return the identifiers of the owned user links as {@link HashSet} of {@link String}
     */
    @Query(
            value = "SELECT " + LINK_IDENTIFIER_KEY + " FROM " + LINKS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    HashSet<String> getUserLinks(
            @Param(OWNER_KEY) String owner
    );

    /**
     * Method to execute the query to get the user's owned links
     *
     * @param userId: the identifier of the user
     *
     * @return the user links as {@link List} of {@link RefyLink}
     */
    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND dtype='" + LINK_KEY + "'",
            nativeQuery = true
    )
    List<RefyLink> getUserOwnedLinks(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    /**
     * Method to execute the query to get all the user's links, included the links shared in the teams and in the
     * collections shared in the teams
     *
     * @param userId: the identifier of the user
     *
     * @return the user links as {@link List} of {@link RefyLink}
     */
    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND dtype='" + LINK_KEY + "'" +
                    " UNION " +
                    "SELECT l.* FROM " + LINKS_KEY + " AS l INNER JOIN " + COLLECTIONS_LINKS_TABLE + " ON " +
                    COLLECTIONS_LINKS_TABLE + "." + LINK_IDENTIFIER_KEY + " = l." + LINK_IDENTIFIER_KEY + " INNER JOIN " +
                    COLLECTIONS_TEAMS_TABLE + " ON " + COLLECTIONS_TEAMS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " = " +
                    COLLECTIONS_LINKS_TABLE + "." + COLLECTION_IDENTIFIER_KEY + " INNER JOIN " + MEMBERS_KEY + " ON " +
                    COLLECTIONS_TEAMS_TABLE + "." + TEAM_IDENTIFIER_KEY + " = " + MEMBERS_KEY + "." + TEAM_IDENTIFIER_KEY +
                    " WHERE " + MEMBERS_KEY + "." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND dtype='" + LINK_KEY + "'",
            nativeQuery = true
    )
    List<RefyLink> getAllUserLinks(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    /**
     * Method to execute the query to save a link
     *
     * @param discriminatorValue: the discriminator value
     * @param linkId: the identifier of the link
     * @param title: the title of the link
     * @param description: the description of the link
     * @param referenceLink: the reference link value
     * @param owner: the owner of the link
     */
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

    /**
     * Method to execute the query to get a link if the owner is authorized
     *
     * @param userId: the identifier of the user
     * @param linkId: the link identifier
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
     * @param linkId: the identifier of the link
     * @param title: the title of the link
     * @param description: the description of the link
     * @param referenceLink: the reference link value
     * @param owner: the owner of the link
     */
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
