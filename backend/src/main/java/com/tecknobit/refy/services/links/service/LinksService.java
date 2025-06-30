package com.tecknobit.refy.services.links.service;

import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.links.repository.LinksRepository;
import com.tecknobit.refy.services.shared.batch.items.CollectionLinkBatchItem;
import com.tecknobit.refy.services.shared.batch.items.TeamLinkBatchItem;
import com.tecknobit.refy.services.shared.batch.procedures.LinkCollectionBatchSyncProcedure;
import com.tecknobit.refy.services.shared.batch.procedures.TeamLinkBatchSyncProcedure;
import com.tecknobit.refy.services.shared.links.service.LinksBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tecknobit.equinoxbackend.configuration.IndexesCreator.formatFullTextKeywords;
import static com.tecknobit.refycore.ConstantsKt.LINK_KEY;

/**
 * The {@code LinksHelper} class is useful to manage all the {@link RefyLink} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see LinksBaseService
 */
@Service
public class LinksService extends LinksBaseService<RefyLink> {

    /**
     * {@code linksRepository} instance for the links repository
     */
    private final LinksRepository linksRepository;

    /**
     * Constructor used to ini the service
     *
     * @param linksRepository The instance for the links repository
     */
    @Autowired
    public LinksService(LinksRepository linksRepository) {
        this.linksRepository = linksRepository;
    }

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
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     *
     * @return the user links as {@link PaginatedResponse} of {@link RefyLink}
     */
    public PaginatedResponse<RefyLink> getUserOwnedLinks(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalLinks = linksRepository.countUserOwnedLinks(userId);
        List<RefyLink> links = linksRepository.getUserOwnedLinks(userId, pageable);
        return new PaginatedResponse<>(links, page, pageSize, totalLinks);
    }

    /**
     * Method to get all the user's links, included the links shared in the teams and in the
     * collections shared in the teams
     *
     * @param userId The identifier of the user
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     *
     * @return the user links as {@link PaginatedResponse} of {@link RefyLink}
     */
    public PaginatedResponse<RefyLink> getAllUserLinks(String userId, int page, int pageSize, Set<String> keywords) {
        Pageable pageable = PageRequest.of(page, pageSize);
        String fullTextMatcher = formatFullTextKeywords(keywords, "*", true);
        long totalLinks = linksRepository.countAllUserLinks(userId, fullTextMatcher);
        List<RefyLink> links = linksRepository.getAllUserLinks(userId, fullTextMatcher, pageable);
        return new PaginatedResponse<>(links, page, pageSize, totalLinks);
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
     * Method share the link with collections
     *
     * @param userId      The identifier of the user
     * @param linkId      The token of the user
     * @param collections The collections where share the link
     */
    public void shareLinkWithCollections(String userId, String linkId, List<String> collections) {
        RefyLink link = getItemIfAllowed(userId, linkId);
        LinkCollectionBatchSyncProcedure procedure = new LinkCollectionBatchSyncProcedure(linkId, collections,
                entityManager);
        procedure.useConverter(collectionsIds -> {
            List<CollectionLinkBatchItem> collectionLinkBatchItems = new ArrayList<>();
            for (String collectionId : collectionsIds)
                collectionLinkBatchItems.add(new CollectionLinkBatchItem(collectionId, linkId));
            return collectionLinkBatchItems;
        });
        procedure.setCurrentDataCallback(link::getCollectionsIds);
        procedure.executeBatchSynchronization();
    }

    /**
     * Method share the link with teams
     *
     * @param userId The identifier of the user
     * @param linkId The token of the user
     * @param teams The teams where share the link
     *
     */
    public void shareLinkWithTeams(String userId, String linkId, List<String> teams) {
        RefyLink link = getItemIfAllowed(userId, linkId);
        TeamLinkBatchSyncProcedure procedure = new TeamLinkBatchSyncProcedure(linkId, teams, entityManager);
        procedure.useConverter(teamsIds -> {
            List<TeamLinkBatchItem> teamLinkBatchItems = new ArrayList<>();
            for (String teamId : teamsIds)
                teamLinkBatchItems.add(new TeamLinkBatchItem(teamId, linkId));
            return teamLinkBatchItems;
        });
        procedure.setCurrentDataCallback(link::getTeamIds);
        procedure.executeBatchSynchronization();
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
