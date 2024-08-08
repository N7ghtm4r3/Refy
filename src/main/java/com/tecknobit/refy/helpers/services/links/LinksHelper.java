package com.tecknobit.refy.helpers.services.links;

import com.tecknobit.refy.helpers.services.RefyItemsHelper;
import com.tecknobit.refy.helpers.services.repositories.links.LinksRepository;
import com.tecknobit.refycore.records.links.RefyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.records.links.RefyLink.LINK_KEY;

@Service
public class LinksHelper extends RefyItemsHelper<RefyLink> {

    @Autowired
    private LinksRepository linksRepository;

    public HashSet<String> getUserLinks(String userId) {
        return linksRepository.getUserLinks(userId);
    }

    public List<RefyLink> getAllUserLinks(String userId) {
        return linksRepository.getAllUserLinks(userId);
    }

    public void createLink(String userId, String linkId, String title, String description, String referenceLink) {
        linksRepository.saveLink(LINK_KEY, linkId, title, description, referenceLink, userId);
    }

    @Override
    public RefyLink getUserItemIfOwner(String userId, String linkId) {
        return linksRepository.getUserLinkIfOwner(userId, linkId);
    }

    public void editLink(String userId, String linkId, String title, String description, String referenceLink) {
        linksRepository.updateLink(linkId, title, description, referenceLink, userId);
    }

    public void manageLinkCollections(String linkId, List<String> collections) {
        RefyLink link = linksRepository.findById(linkId).orElseThrow();
        manageAttachments(
                new AttachmentsManagementWorkflow() {

                    @Override
                    public List<String> getIds() {
                        return link.getCollectionsIds();
                    }

                    @Override
                    public void add(String collectionId) {
                        linksRepository.addLinkToCollection(collectionId, linkId);
                    }

                    @Override
                    public void remove(String collectionId) {
                        linksRepository.removeLinkFromCollection(collectionId, linkId);
                    }

                },
                collections
        );
    }

    public void manageLinkTeams(String linkId, List<String> teams) {
        RefyLink link = linksRepository.findById(linkId).orElseThrow();
        manageAttachments(
                new AttachmentsManagementWorkflow() {

                    @Override
                    public List<String> getIds() {
                        return link.getTeamIds();
                    }

                    @Override
                    public void add(String teamId) {
                        linksRepository.addLinkToTeam(teamId, linkId);
                    }

                    @Override
                    public void remove(String teamId) {
                        linksRepository.removeLinkFromTeam(teamId, linkId);
                    }

                },
                teams
        );
    }

    public void deleteLink(String linkId) {
        linksRepository.detachLinkFromCollections(linkId);
        linksRepository.detachLinkFromTeams(linkId);
        linksRepository.deleteLink(linkId);
    }

}
