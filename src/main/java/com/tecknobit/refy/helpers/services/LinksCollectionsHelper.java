package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.refy.helpers.services.repositories.CollectionsRepository;
import com.tecknobit.refycore.records.LinksCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class LinksCollectionsHelper extends RefyItemsHelper<LinksCollection> {

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
        for (String link : links)
            collectionsRepository.addLinkToCollection(collectionId, link);
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
                    public void add(String linkId) {
                        collectionsRepository.addLinkToCollection(collectionId, linkId);
                    }

                    @Override
                    public void remove(String linkId) {
                        collectionsRepository.removeLinkFromCollection(collectionId, linkId);
                    }

                },
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
                    public void add(String teamId) {
                        collectionsRepository.addCollectionToTeam(teamId, collectionId);
                    }

                    @Override
                    public void remove(String teamId) {
                        collectionsRepository.removeCollectionFromTeam(teamId, collectionId);
                    }

                },
                teams
        );
    }

    public void deleteCollection(String collectionId) {
        collectionsRepository.detachCollectionFromLinks(collectionId);
        collectionsRepository.detachCollectionFromTeams(collectionId);
        collectionsRepository.deleteCollection(collectionId);
    }

}
