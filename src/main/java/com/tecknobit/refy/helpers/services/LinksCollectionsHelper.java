package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.refy.helpers.services.repositories.CollectionsRepository;
import com.tecknobit.refycore.records.LinksCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_LINKS_TABLE;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.COLLECTIONS_TEAMS_TABLE;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

/**
 * The {@code LinksCollectionsHelper} class is useful to manage all the {@link LinksCollection} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see RefyItemsHelper
 */
@Service
public class LinksCollectionsHelper extends RefyItemsHelper<LinksCollection> {

    /**
     * {@code ATTACH_COLLECTION_TO_LINKS_QUERY} the query used to attach links to a collection
     */
    protected static final String ATTACH_COLLECTION_TO_LINKS_QUERY =
            "REPLACE INTO " + COLLECTIONS_LINKS_TABLE +
                    "(" +
                    LINK_IDENTIFIER_KEY + "," +
                    COLLECTION_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code DETACH_COLLECTION_FROM_LINKS_QUERY} the query used to detach links from a collection
     */
    private static final String DETACH_COLLECTION_FROM_LINKS_QUERY =
            "DELETE FROM " + COLLECTIONS_LINKS_TABLE + " WHERE "
                    + COLLECTION_IDENTIFIER_KEY + "='%s' " + "AND " + LINK_IDENTIFIER_KEY + " IN (";

    /**
     * {@code ATTACH_COLLECTION_TO_TEAM_QUERY} the query used to share a collection with teams
     */
    private static final String ATTACH_COLLECTION_TO_TEAM_QUERY =
            "REPLACE INTO " + COLLECTIONS_TEAMS_TABLE +
                    "(" +
                    TEAM_IDENTIFIER_KEY + "," +
                    COLLECTION_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code DETACH_COLLECTION_FROM_TEAMS_QUERY} the query used to remove a collection from teams
     */
    private static final String DETACH_COLLECTION_FROM_TEAMS_QUERY =
            "DELETE FROM " + COLLECTIONS_TEAMS_TABLE + " WHERE "
                    + COLLECTION_IDENTIFIER_KEY + "='%s' " + "AND " + TEAM_IDENTIFIER_KEY + " IN (";

    /**
     * {@code collectionsRepository} instance for the collections repository
     */
    @Autowired
    private CollectionsRepository collectionsRepository;

    /**
     * Method to get the user's owned collections identifiers
     *
     * @param userId: the identifier of the user
     *
     * @return the identifiers of the owned user collections as {@link HashSet} of {@link String}
     */
    public HashSet<String> getUserCollections(String userId) {
        return collectionsRepository.getUserCollections(userId);
    }

    /**
     * Method to get the user's owned collections
     *
     * @param userId: the identifier of the user
     *
     * @return the user collections as {@link List} of {@link LinksCollection}
     */
    public List<LinksCollection> getUserOwnedCollections(String userId) {
        return collectionsRepository.getUserOwnedCollections(userId);
    }

    /**
     * Method to get all the user's collections, included the collections shared in the teams
     *
     * @param userId: the identifier of the user
     *
     * @return the user collections as {@link List} of {@link LinksCollection}
     */
    public List<LinksCollection> getAllUserCollections(String userId) {
        return collectionsRepository.getAllUserCollections(userId);
    }

    /**
     * Method to create a collection
     *
     * @param userId: the owner of the collection
     * @param collectionId: the identifier of the collection
     * @param color: the color of the collection
     * @param title: the title of the collection
     * @param description: the description of the collection
     * @param links: the links to attach to the collection
     */
    public void createCollection(String userId, String collectionId, String color, String title, String description,
                                 List<String> links) {
        collectionsRepository.saveCollection(collectionId, color, title, description, userId);
        executeInsertBatch(ATTACH_COLLECTION_TO_LINKS_QUERY, RELATIONSHIP_VALUES_SLICE, links, query -> {
            int index = 1;
            for (String link : links) {
                query.setParameter(index++, link);
                query.setParameter(index++, collectionId);
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
     * @param userId: the owner of the collection
     * @param collectionId: the identifier of the collection
     * @param color: the color of the collection
     * @param title: the title of the collection
     * @param description: the description of the collection
     * @param links: the links to attach to the collection
     */
    public void editCollection(String userId, String collectionId, String color, String title, String description,
                               List<String> links) {
        LinksCollection collection = collectionsRepository.findById(collectionId).orElseThrow();
        collectionsRepository.updateCollection(collectionId, color, title, description, userId);
        manageCollectionLinks(collection, links);
    }

    /**
     * Method to manage the links attached to the collection
     *
     * @param collectionId: the identifier of the collection
     * @param links: the links attached to the collections
     */
    @Wrapper
    public void manageCollectionLinks(String collectionId, List<String> links) {
        manageCollectionLinks(collectionsRepository.findById(collectionId).orElseThrow(), links);
    }

    /**
     * Method to manage the links attached to the collection
     *
     * @param collection: the collection where the link are attached
     * @param links: the links attached to the collections
     */
    private void manageCollectionLinks(LinksCollection collection, List<String> links) {
        String collectionId = collection.getId();
        manageAttachments(
                new AttachmentsManagementWorkflow() {

                    @Override
                    public List<String> getIds() {
                        return collection.getLinkIds();
                    }

                    @Override
                    public String insertQuery() {
                        return ATTACH_COLLECTION_TO_LINKS_QUERY;
                    }

                    @Override
                    public String deleteQuery() {
                        return DETACH_COLLECTION_FROM_LINKS_QUERY;
                    }
                },
                collectionId,
                links
        );
    }

    /**
     * Method to manage the teams where the collection is shared
     *
     * @param collectionId: the identifier of the collection
     * @param teams: the teams where the collection is shared
     */
    public void manageCollectionTeams(String collectionId, List<String> teams) {
        LinksCollection collection = collectionsRepository.findById(collectionId).orElseThrow();
        manageAttachments(
                new AttachmentsManagementWorkflow() {

                    @Override
                    public List<String> getIds() {
                        return collection.getTeamIds();
                    }

                    @Override
                    public String insertQuery() {
                        return ATTACH_COLLECTION_TO_TEAM_QUERY;
                    }

                    @Override
                    public String deleteQuery() {
                        return DETACH_COLLECTION_FROM_TEAMS_QUERY;
                    }
                },
                collectionId,
                teams
        );
    }

    /**
     * Method to delete a collection
     *
     * @param collectionId: the identifier of the collection to delete
     */
    public void deleteCollection(String collectionId) {
        collectionsRepository.detachCollectionFromLinks(collectionId);
        collectionsRepository.detachCollectionFromTeams(collectionId);
        collectionsRepository.deleteCollection(collectionId);
    }

}
