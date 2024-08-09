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

@Service
public class LinksCollectionsHelper extends RefyItemsHelper<LinksCollection> {

    private static final String DETACH_COLLECTION_FROM_LINKS_QUERY =
            "DELETE FROM " + COLLECTIONS_LINKS_TABLE + " WHERE "
                    + COLLECTION_IDENTIFIER_KEY + "='%s' " + "AND " + TEAM_IDENTIFIER_KEY + " IN (";

    private static final String DETACH_COLLECTION_FROM_TEAMS_QUERY =
            "DELETE FROM " + COLLECTIONS_TEAMS_TABLE + " WHERE "
                    + COLLECTION_IDENTIFIER_KEY + "='%s' " + "AND " + TEAM_IDENTIFIER_KEY + " IN (";

    @Autowired
    private CollectionsRepository collectionsRepository;

    public HashSet<String> getUserCollections(String userId) {
        return collectionsRepository.getUserCollections(userId);
    }

    public List<LinksCollection> getAllUserCollections(String userId) {
        return collectionsRepository.getAllUserCollections(userId);
    }

    public void createCollection(String userId, String collectionId, String color, String title, String description,
                                 List<String> links) {
        collectionsRepository.saveCollection(collectionId, color, title, description, userId);
        executeInsertBatch(MANAGE_LINK_COLLECTION_RELATIONSHIP_QUERY, RELATIONSHIP_VALUES_SLICE, links, query -> {
            int index = 1;
            for (String link : links) {
                query.setParameter(index++, collectionId);
                query.setParameter(index++, link);
            }
        });
    }

    @Override
    public LinksCollection getItemIfAllowed(String userId, String collectionId) {
        return collectionsRepository.getCollectionIfAllowed(userId, collectionId);
    }

    public void editCollection(String userId, String collectionId, String color, String title, String description,
                               List<String> links) {
        LinksCollection collection = collectionsRepository.findById(collectionId).orElseThrow();
        collectionsRepository.updateCollection(collectionId, color, title, description, userId);
        manageCollectionLinks(collection, links);
    }

    @Wrapper
    public void manageCollectionLinks(String collectionId, List<String> links) {
        manageCollectionLinks(collectionsRepository.findById(collectionId).orElseThrow(), links);
    }

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
                        return MANAGE_LINK_COLLECTION_RELATIONSHIP_QUERY;
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
                        return MANAGE_COLLECTION_TEAM_RELATIONSHIP_QUERY;
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

    public void deleteCollection(String collectionId) {
        collectionsRepository.detachCollectionFromLinks(collectionId);
        collectionsRepository.detachCollectionFromTeams(collectionId);
        collectionsRepository.deleteCollection(collectionId);
    }

}
