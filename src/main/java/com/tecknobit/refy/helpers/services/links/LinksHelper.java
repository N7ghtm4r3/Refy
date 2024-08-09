package com.tecknobit.refy.helpers.services.links;

import com.tecknobit.refy.helpers.services.RefyItemsHelper;
import com.tecknobit.refy.helpers.services.repositories.links.LinksRepository;
import com.tecknobit.refycore.records.links.RefyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_LINKS_TABLE;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.TEAMS_LINKS_TABLE;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_KEY;

@Service
public class LinksHelper extends RefyItemsHelper<RefyLink> {

    protected static final String ATTACH_LINK_TO_COLLECTIONS_QUERY =
            "REPLACE INTO " + COLLECTIONS_LINKS_TABLE +
                    "(" +
                    COLLECTION_IDENTIFIER_KEY + "," +
                    LINK_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    private static final String DETACH_LINK_FROM_COLLECTIONS_QUERY =
            "DELETE FROM " + COLLECTIONS_LINKS_TABLE + " WHERE "
                    + LINK_IDENTIFIER_KEY + "='%s' " + "AND " + COLLECTION_IDENTIFIER_KEY + " IN (";

    private static final String DETACH_LINK_FROM_TEAMS_QUERY =
            "DELETE FROM " + TEAMS_LINKS_TABLE + " WHERE "
                    + LINK_IDENTIFIER_KEY + "='%s' " + "AND " + TEAM_IDENTIFIER_KEY + " IN (";

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
    public RefyLink getItemIfAllowed(String userId, String linkId) {
        return linksRepository.getLinkIfAllowed(userId, linkId);
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
                    public String insertQuery() {
                        return ATTACH_LINK_TO_COLLECTIONS_QUERY;
                    }

                    @Override
                    public String deleteQuery() {
                        return DETACH_LINK_FROM_COLLECTIONS_QUERY;
                    }

                },
                linkId,
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
                    public String insertQuery() {
                        return ATTACH_LINK_TO_TEAM_QUERY;
                    }

                    @Override
                    public String deleteQuery() {
                        return DETACH_LINK_FROM_TEAMS_QUERY;
                    }

                },
                linkId,
                teams
        );
    }

    public void deleteLink(String linkId) {
        linksRepository.detachLinkFromCollections(linkId);
        linksRepository.detachLinkFromTeams(linkId);
        linksRepository.deleteLink(linkId);
    }

}
