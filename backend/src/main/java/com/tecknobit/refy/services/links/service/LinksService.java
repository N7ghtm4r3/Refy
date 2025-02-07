package com.tecknobit.refy.services.links.service;

import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.links.repository.LinksRepository;
import com.tecknobit.refy.services.shared.links.service.LinksBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code LinksHelper} class is useful to manage all the {@link RefyLink} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see LinksBaseService
 */
@Service
public class LinksService extends LinksBaseService<RefyLink> {

    /**
     * {@code ATTACH_LINK_TO_COLLECTIONS_QUERY} the query used to attach link to collections
     */
    protected static final String ATTACH_LINK_TO_COLLECTIONS_QUERY =
            "REPLACE INTO " + COLLECTIONS_LINKS_TABLE +
                    "(" +
                    COLLECTION_IDENTIFIER_KEY + "," +
                    LINK_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";
    /**
     * {@code DETACH_LINK_FROM_COLLECTIONS_QUERY} the query used to detach link from collections
     */
    private static final String DETACH_LINK_FROM_COLLECTIONS_QUERY =
            "DELETE FROM " + COLLECTIONS_LINKS_TABLE + " WHERE "
                    + LINK_IDENTIFIER_KEY + "='%s' " + "AND " + COLLECTION_IDENTIFIER_KEY + " IN (";

    /**
     * {@code ATTACH_LINK_TO_TEAMS_QUERY} the query used to attach link to teams
     */
    private static final String ATTACH_LINK_TO_TEAMS_QUERY =
            "REPLACE INTO " + TEAMS_LINKS_TABLE +
                    "(" +
                    TEAM_IDENTIFIER_KEY + "," +
                    LINK_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code DETACH_LINK_FROM_TEAMS_QUERY} the query used to detach link from teams
     */
    private static final String DETACH_LINK_FROM_TEAMS_QUERY =
            "DELETE FROM " + TEAMS_LINKS_TABLE + " WHERE "
                    + LINK_IDENTIFIER_KEY + "='%s' " + "AND " + TEAM_IDENTIFIER_KEY + " IN (";

    /**
     * {@code linksRepository} instance for the links repository
     */
    @Autowired
    private LinksRepository linksRepository;

    /**
     * Method to get the user's owned links identifiers
     *
     * @param userId The identifier of the user
     *
     * @return the identifiers of the owned user links as {@link HashSet} of {@link String}
     */
    public HashSet<String> getUserLinks(String userId) {
        return linksRepository.getUserLinks(userId);
    }

    /**
     * Method to get the user's owned links
     *
     * @param userId The identifier of the user
     *
     * @return the user links as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getUserOwnedLinks(String userId) {
        return linksRepository.getUserOwnedLinks(userId);
    }

    /**
     * Method to get all the user's links, included the links shared in the teams and in the
     * collections shared in the teams
     *
     * @param userId The identifier of the user
     *
     * @return the user links as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getAllUserLinks(String userId) {
        return linksRepository.getAllUserLinks(userId);
    }

    /**
     * Method to save a new link
     *
     * @param userId The owner of the link
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param thumbnailPreview The url of the thumbnail preview of the link
     * @param description The description of the link
     * @param referenceLink The reference link value
     */
    public void createLink(String userId, String linkId, String title, String thumbnailPreview, String description,
                           String referenceLink) {
        linksRepository.saveLink(LINK_KEY, linkId, title, System.currentTimeMillis(), thumbnailPreview, description,
                referenceLink, userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefyLink getItemIfAllowed(String userId, String linkId) {
        return linksRepository.getLinkIfAllowed(userId, linkId);
    }

    /**
     * Method to edit a new link
     *
     * @param userId The owner of the link
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param thumbnailPreview The url of the thumbnail preview of the link
     * @param description The description of the link
     * @param referenceLink The reference link value
     */
    public void editLink(String userId, String linkId, String title, String thumbnailPreview, String description,
                         String referenceLink) {
        linksRepository.updateLink(linkId, title, thumbnailPreview, description, referenceLink, userId);
    }

    /**
     * Method to manage the collections where the link is shared
     *
     * @param linkId The identifier of the link
     * @param collections The collections where the link is shared
     */
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

    /**
     * Method to manage the teams where the link is shared
     *
     * @param linkId The identifier of the link
     * @param teams The teams where the link is shared
     */
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
                        return ATTACH_LINK_TO_TEAMS_QUERY;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteLink(String linkId) {
        linksRepository.detachLinkFromCollections(linkId);
        linksRepository.detachLinkFromTeams(linkId);
        linksRepository.deleteLink(linkId);
    }

}
