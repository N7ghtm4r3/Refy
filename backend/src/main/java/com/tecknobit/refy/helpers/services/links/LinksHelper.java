package com.tecknobit.refy.helpers.services.links;

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

/**
 * The {@code LinksHelper} class is useful to manage all the {@link RefyLink} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see LinksBaseHelper
 */
@Service
public class LinksHelper extends LinksBaseHelper<RefyLink> {

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
     * @param userId: the identifier of the user
     *
     * @return the identifiers of the owned user links as {@link HashSet} of {@link String}
     */
    public HashSet<String> getUserLinks(String userId) {
        return linksRepository.getUserLinks(userId);
    }

    /**
     * Method to get the user's owned links
     *
     * @param userId: the identifier of the user
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
     * @param userId: the identifier of the user
     *
     * @return the user links as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getAllUserLinks(String userId) {
        return linksRepository.getAllUserLinks(userId);
    }

    /**
     * Method to save a new link
     *
     * @param userId: the owner of the link
     * @param linkId: the identifier of the link
     * @param title: the title of the link
     * @param description: the description of the link
     * @param referenceLink: the reference link value
     */
    public void createLink(String userId, String linkId, String title, String description, String referenceLink) {
        linksRepository.saveLink(LINK_KEY, linkId, title, description, referenceLink, userId);
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
     * @param userId: the owner of the link
     * @param linkId: the identifier of the link
     * @param title: the title of the link
     * @param description: the description of the link
     * @param referenceLink: the reference link value
     */
    public void editLink(String userId, String linkId, String title, String description, String referenceLink) {
        linksRepository.updateLink(linkId, title, description, referenceLink, userId);
    }

    /**
     * Method to manage the collections where the link is shared
     *
     * @param linkId: the identifier of the link
     * @param collections: the collections where the link is shared
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
     * @param linkId: the identifier of the link
     * @param teams: the teams where the link is shared
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
