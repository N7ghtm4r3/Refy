package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.refycore.records.RefyItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_LINKS_TABLE;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

/**
 * The {@code RefyItemsRepository} interface is useful to manage the queries of the {@link RefyItem}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 *
 */
@Service
@Repository
public interface RefyItemsRepository<T extends RefyItem> extends JpaRepository<T, String> {

    /**
     * Method to execute the query to detach a link from collections
     *
     * @param linkId: the identifier of the link to detach from the collections
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_LINKS_TABLE + " WHERE " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void detachLinkFromCollections(
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    /**
     * Method to execute the query to detach a link from teams
     *
     * @param linkId: the identifier of the link to detach from the teams
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + TEAMS_LINKS_TABLE + " WHERE " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void detachLinkFromTeams(
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    /**
     * Method to execute the query to detach a team from links
     *
     * @param teamId: the identifier of the team to detach from the links
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + TEAMS_LINKS_TABLE + " WHERE " + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void detachTeamFromLinks(
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

    /**
     * Method to execute the query to detach a team from collections
     *
     * @param teamId: the identifier of the team to detach from the collections
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_TEAMS_TABLE + " WHERE " + TEAM_IDENTIFIER_KEY + "=:" + TEAM_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void detachTeamFromCollections(
            @Param(TEAM_IDENTIFIER_KEY) String teamId
    );

    /**
     * Method to execute the query to detach a collection from links
     *
     * @param collectionId: the identifier of the collection to detach from the links
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_LINKS_TABLE
                    + " WHERE " + COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void detachCollectionFromLinks(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId
    );

    /**
     * Method to execute the query to detach a collection from teams
     *
     * @param collectionId: the identifier of the collection to detach from the teams
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + COLLECTIONS_TEAMS_TABLE
                    + " WHERE " + COLLECTION_IDENTIFIER_KEY + "=:" + COLLECTION_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void detachCollectionFromTeams(
            @Param(COLLECTION_IDENTIFIER_KEY) String collectionId
    );

}
