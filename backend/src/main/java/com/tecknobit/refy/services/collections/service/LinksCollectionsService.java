package com.tecknobit.refy.services.collections.service;

import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.collections.repository.CollectionsRepository;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.links.repository.LinksRepository;
import com.tecknobit.refy.services.shared.batch.items.CollectionLinkBatchItem;
import com.tecknobit.refy.services.shared.batch.items.TeamCollectionBatchItem;
import com.tecknobit.refy.services.shared.batch.procedures.LinkCollectionBatchSyncProcedure;
import com.tecknobit.refy.services.shared.batch.procedures.TeamCollectionBatchSyncProcedure;
import com.tecknobit.refy.services.shared.services.RefyItemRetriever;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.repository.TeamsRepository;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.tecknobit.equinoxbackend.configuration.IndexesCreator.formatFullTextKeywords;
import static com.tecknobit.refy.services.shared.batch.items.CollectionLinkBatchItem.COLLECTION_LINK_JOIN_TABLE_COLUMNS;
import static com.tecknobit.refycore.ConstantsKt.COLLECTIONS_LINKS_TABLE;

/**
 * The {@code LinksCollectionsHelper} class is useful to manage all the {@link LinksCollection} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EquinoxItemsHelper
 * @see LinksCollection
 */
@Service
public class LinksCollectionsService extends EquinoxItemsHelper implements RefyItemRetriever<LinksCollection> {

    /**
     * {@code collectionsRepository} instance for the collections repository
     */
    @Autowired
    private CollectionsRepository collectionsRepository;

    /**
     * {@code linksRepository} instance for the links repository
     */
    @Autowired
    private LinksRepository linksRepository;

    /**
     * {@code teamsRepository} instance for the teams repository
     */
    @Autowired
    private TeamsRepository teamsRepository;

    /**
     * Method to get the user's owned collections identifiers
     *
     * @param userId The identifier of the user
     *
     * @return the identifiers of the owned user collections as {@link HashSet} of {@link String}
     */
    public HashSet<String> getUserCollections(String userId) {
        return collectionsRepository.getUserCollections(userId);
    }

    /**
     * Method to get the user's owned collections
     *
     * @param userId The identifier of the user
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     *
     * @return the user collections as {@link PaginatedResponse} of {@link LinksCollection}
     */
    public PaginatedResponse<LinksCollection> getUserOwnedCollections(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalCollections = collectionsRepository.countUserOwnedCollections(userId);
        List<LinksCollection> collections = collectionsRepository.getUserOwnedCollections(userId, pageable);
        return new PaginatedResponse<>(collections, page, pageSize, totalCollections);
    }

    /**
     * Method to get all the user's collections, included the collections shared in the teams
     *
     * @param userId The identifier of the user
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     *
     * @return the user collections as {@link List} of {@link LinksCollection}
     */
    public PaginatedResponse<LinksCollection> getAllUserCollections(String userId, int page, int pageSize,
                                                                    Set<String> keywords) {
        Pageable pageable = PageRequest.of(page, pageSize);
        String fullTextMatcher = formatFullTextKeywords(keywords, "*", true);
        long totalCollections = collectionsRepository.countAllUserCollections(userId, fullTextMatcher);
        List<LinksCollection> collections = collectionsRepository.getAllUserCollections(userId, fullTextMatcher, pageable);
        return new PaginatedResponse<>(collections, page, pageSize, totalCollections);
    }

    /**
     * Method to create a collection
     *
     * @param userId The owner of the collection
     * @param collectionId The identifier of the collection
     * @param color The color of the collection
     * @param title The title of the collection
     * @param description The description of the collection
     * @param links The links to attach to the collection
     */
    public void createCollection(String userId, String collectionId, String color, String title, String description,
                                 List<String> links) {
        collectionsRepository.saveCollection(collectionId, color, title, description, System.currentTimeMillis(), userId);
        batchInsert(InsertCommand.INSERT_INTO, COLLECTIONS_LINKS_TABLE, new BatchQuery<String>() {
            @Override
            public Collection<String> getData() {
                return links;
            }

            @Override
            public void prepareQuery(Query query, int index, Collection<String> links) {
                for (String link : links) {
                    query.setParameter(index++, collectionId);
                    query.setParameter(index++, link);
                }
            }

            @Override
            public String[] getColumns() {
                return COLLECTION_LINK_JOIN_TABLE_COLUMNS;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksCollection getItemIfAllowed(String userId, String collectionId) {
        return collectionsRepository.getCollectionIfAllowed(userId, collectionId);
    }

    /**
     * Method to edit a collection
     *
     * @param userId The owner of the collection
     * @param collectionId The identifier of the collection
     * @param color The color of the collection
     * @param title The title of the collection
     * @param description The description of the collection
     * @param links The links to attach to the collection
     */
    public void editCollection(String userId, String collectionId, String color, String title, String description,
                               List<String> links) {
        collectionsRepository.updateCollection(collectionId, color, title, description, userId);
        attachLinksToCollection(collectionId, links);
    }

    /**
     * Method to attach the links with a collection
     *
     * @param collectionId The identifier of the collection
     * @param links        The links to attach to the collection
     */
    public void attachLinksToCollection(String collectionId, List<String> links) {
        LinksCollection collection = collectionsRepository.findById(collectionId).orElseThrow();
        LinkCollectionBatchSyncProcedure procedure = new LinkCollectionBatchSyncProcedure(collectionId, links,
                entityManager);
        procedure.useConverter(linksIds -> {
            List<CollectionLinkBatchItem> collectionLinkBatchItems = new ArrayList<>();
            for (String linkId : linksIds)
                collectionLinkBatchItems.add(new CollectionLinkBatchItem(collectionId, linkId));
            return collectionLinkBatchItems;
        });
        procedure.setCurrentDataCallback(collection::getLinkIds);
        procedure.executeBatchSynchronization();
    }

    /**
     * Method to manage the teams where the collection is shared
     *
     * @param collectionId The identifier of the collection
     * @param teams The teams where the collection is shared
     */
    public void shareCollectionWithTeams(String collectionId, List<String> teams) {
        LinksCollection collection = collectionsRepository.findById(collectionId).orElseThrow();
        TeamCollectionBatchSyncProcedure procedure = new TeamCollectionBatchSyncProcedure(collectionId, teams,
                entityManager);
        procedure.useConverter(teamsIds -> {
            List<TeamCollectionBatchItem> teamCollectionBatchItems = new ArrayList<>();
            for (String teamId : teamsIds)
                teamCollectionBatchItems.add(new TeamCollectionBatchItem(teamId, collectionId));
            return teamCollectionBatchItems;
        });
        procedure.setCurrentDataCallback(collection::getTeamIds);
        procedure.executeBatchSynchronization();
    }

    /**
     * Method to get the teams where the collection is shared
     *
     * @param collectionId The identifier of the collection
     * @param page         The page requested
     * @param pageSize     The size of the items to insert in the page
     * @return the collection teams as {@link PaginatedResponse} of {@link RefyLink}
     */
    public PaginatedResponse<Team> getCollectionTeams(String collectionId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalTeams = teamsRepository.countCollectionTeams(collectionId);
        List<Team> teams = teamsRepository.getCollectionTeams(collectionId, pageable);
        return new PaginatedResponse<>(teams, page, pageSize, totalTeams);
    }

    /**
     * Method to get the links shared in a collection
     *
     * @param collectionId The identifier of the collection
     * @param page     The page requested
     * @param pageSize The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     * @return the collection links as {@link PaginatedResponse} of {@link RefyLink}
     */
    public PaginatedResponse<RefyLink> getCollectionLinks(String collectionId, int page, int pageSize, Set<String> keywords) {
        Pageable pageable = PageRequest.of(page, pageSize);
        String fullTextMatcher = formatFullTextKeywords(keywords, "*", true);
        long totalLinks = linksRepository.countCollectionLinks(collectionId, fullTextMatcher);
        List<RefyLink> links = linksRepository.getCollectionLinks(collectionId, fullTextMatcher, pageable);
        return new PaginatedResponse<>(links, page, pageSize, totalLinks);
    }

    /**
     * Method to remove a link from a collection
     *
     * @param collectionId The identifier of the collection
     * @param linkId       The identifier of the link
     */
    public void removeLinkFromCollection(String collectionId, String linkId) {
        collectionsRepository.removeLinkFromCollection(collectionId, linkId);
    }

    /**
     * Method to remove a team from a collection
     *
     * @param collectionId The identifier of the collection
     * @param teamId The identifier of the team
     */
    public void removeTeamFromCollection(String collectionId, String teamId) {
        collectionsRepository.removeTeamFromCollection(collectionId, teamId);
    }

    /**
     * Method to delete a collection
     *
     * @param collectionId The identifier of the collection to delete
     */
    public void deleteCollection(String collectionId) {
        collectionsRepository.detachCollectionFromLinks(collectionId);
        collectionsRepository.detachCollectionFromTeams(collectionId);
        collectionsRepository.deleteCollection(collectionId);
    }

}
