package com.tecknobit.refy.services.customlinks.service;

import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.refy.services.customlinks.batch.CustomLinkMapBatchItem;
import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink;
import com.tecknobit.refy.services.customlinks.repository.CustomLinksRepository;
import com.tecknobit.refy.services.links.service.LinksService;
import com.tecknobit.refy.services.shared.links.service.LinksBaseService;
import com.tecknobit.refycore.enums.ExpiredTime;
import jakarta.persistence.Query;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.tecknobit.equinoxbackend.apis.batch.EquinoxItemsHelper.InsertCommand.INSERT_INTO;
import static com.tecknobit.equinoxbackend.apis.batch.EquinoxItemsHelper.InsertCommand.REPLACE_INTO;
import static com.tecknobit.equinoxbackend.configuration.IndexesCreator.formatFullTextKeywords;
import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.CUSTOM_LINKS_ENDPOINT;

/**
 * The {@code CustomLinksService} class is useful to manage all the {@link CustomRefyLink} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see LinksBaseService
 * @see LinksService
 */
@Service
public class CustomLinksService extends LinksBaseService<CustomRefyLink> {

    /**
     * {@code customLinksRepository} instance for the custom links repository
     */
    private final CustomLinksRepository customLinksRepository;

    /**
     * Constructor used to init the service
     * 
     * @param customLinksRepository The instance for the custom links repository
     */
    @Autowired
    public CustomLinksService(CustomLinksRepository customLinksRepository) {
        this.customLinksRepository = customLinksRepository;
    }

    /**
     * Method to find a custom link by its identifier
     *
     * @param linkId The link identifier to fetch
     * @return the custom link related to the identifier as {@link CustomRefyLink}
     */
    public CustomRefyLink findById(String linkId) {
        return customLinksRepository.findById(linkId).orElse(null);
    }

    /**
     * Method to get all the user's custom links
     *
     * @param userId The identifier of the user
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords     The keywords used to filter the query to retrieve the items
     *
     * @return the user custom links as {@link PaginatedResponse} of {@link CustomRefyLink}
     */
    public PaginatedResponse<CustomRefyLink> getUserCustomLinks(String userId, int page, int pageSize,
                                                                Set<String> keywords) {
        Pageable pageable = PageRequest.of(page, pageSize);
        String fullTextMatcher = formatFullTextKeywords(keywords, "*", true);
        long totalCustomLinks = customLinksRepository.countUserCustomLinks(userId, fullTextMatcher);
        List<CustomRefyLink> customRefyLinks = customLinksRepository.getUserCustomLinks(userId, fullTextMatcher, pageable);
        return new PaginatedResponse<>(customRefyLinks, page, pageSize, totalCustomLinks);
    }

    /**
     * Method to execute the query to save a link
     *
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param description The description of the link
     * @param hasUniqueAccess: whether the link has the unique access
     * @param expiredTime The expiration time set for the link
     * @param resources The resources attached to the link
     * @param fields The field used to protect the resources
     */
    public void createCustomLink(String userId, String linkId, String title, String description, boolean hasUniqueAccess,
                                 ExpiredTime expiredTime, Map<String, Object> fields, Map<String, Object> resources) {
        customLinksRepository.saveLink(CUSTOM_LINK_KEY, linkId, title, description, CUSTOM_LINKS_ENDPOINT +
                "/" + linkId, System.currentTimeMillis(), expiredTime, hasUniqueAccess, generateIdentifier(), userId);
        attachMap(linkId, FIELDS_KEY, fields, IDENTIFIER_KEY, FIELD_VALUE_KEY, FIELD_KEY);
        attachMap(linkId, RESOURCES_KEY, resources, IDENTIFIER_KEY, RESOURCE_VALUE_KEY, RESOURCE_KEY);
    }

    /**
     * Method to attach a map value to the link
     *
     * @param linkId The identifier of the link where attach the map
     * @param table The table where attach the map
     * @param map The map to attach to the link
     * @param columns The columns used during the query
     *
     */
    private void attachMap(String linkId, String table, Map<String, Object> map, String... columns) {
        batchInsert(INSERT_INTO, table, new BatchQuery<Pair<String, Object>>() {
                    @Override
                    public Collection<Pair<String, Object>> getData() {
                        ArrayList<Pair<String, Object>> pairs = new ArrayList<>();
                        for (String key : map.keySet())
                            pairs.add(new Pair<>(key, map.get(key)));
                        return pairs;
                    }

                    @Override
                    public void prepareQuery(Query query, int index, Collection<Pair<String, Object>> pairs) {
                        for (Pair<String, Object> pair : pairs) {
                            query.setParameter(index++, linkId);
                            query.setParameter(index++, pair.getSecond());
                            query.setParameter(index++, pair.getFirst());
                        }
                    }

                    @Override
                    public String[] getColumns() {
                        return columns;
                    }
                }
        );
    }

    /**
     * Method to execute the query to save a link
     *
     * @param userId The owner of the link
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param description The description of the link
     * @param hasUniqueAccess: whether the link has the unique access
     * @param expiredTime The expiration time set for the link
     * @param fields The field used to protect the resources
     * @param resources The resources attached to the link
     */
    public void editCustomLink(String userId, String linkId, String title, String description, boolean hasUniqueAccess,
                               ExpiredTime expiredTime, Map<String, Object> fields, Map<String, Object> resources) {
        CustomRefyLink customRefyLink = getItemIfAllowed(userId, linkId);
        customLinksRepository.updateLink(linkId, title, description, expiredTime, hasUniqueAccess, userId);
        editMap(linkId, FIELDS_KEY, customRefyLink.getFields(), fields, IDENTIFIER_KEY, FIELD_VALUE_KEY, FIELD_KEY);
        editMap(linkId, RESOURCES_KEY, customRefyLink.getResources(), resources, IDENTIFIER_KEY, RESOURCE_VALUE_KEY,
                RESOURCE_KEY);
    }

    /**
     * Method to edit a map attached to the link
     *
     * @param linkId The identifier of the link where the map is attached
     * @param currentMap The current map attached to the link
     * @param map The new map to attach to the link
     */
    private void editMap(String linkId, String table, Map<String, ?> currentMap, Map<String, Object> map,
                         String... columns) {
        SyncBatchModel model = new SyncBatchModel() {
            @Override
            public Collection<CustomLinkMapBatchItem> getCurrentData() {
                ArrayList<CustomLinkMapBatchItem> pairs = new ArrayList<>();
                for (String key : currentMap.keySet())
                    pairs.add(new CustomLinkMapBatchItem(linkId, key, currentMap.get(key)));
                return pairs;
            }

            @Override
            public String[] getDeletingColumns() {
                return columns;
            }
        };
        BatchQuery<CustomLinkMapBatchItem> batchQuery = new BatchQuery<>() {
            @Override
            public Collection<CustomLinkMapBatchItem> getData() {
                ArrayList<CustomLinkMapBatchItem> pairs = new ArrayList<>();
                for (String key : map.keySet())
                    pairs.add(new CustomLinkMapBatchItem(linkId, key, map.get(key)));
                return pairs;
            }

            @Override
            public void prepareQuery(Query query, int index, Collection<CustomLinkMapBatchItem> items) {
                for (CustomLinkMapBatchItem item : items) {
                    query.setParameter(index++, item.getLinkId());
                    query.setParameter(index++, item.getValue());
                    query.setParameter(index++, item.getKey());
                }
            }

            @Override
            public String[] getColumns() {
                return columns;
            }
        };
        syncBatch(model, REPLACE_INTO, table, batchQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomRefyLink getItemIfAllowed(String userId, String linkId) {
        return customLinksRepository.getLinkIfAllowed(userId, linkId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteLink(String linkId) {
        customLinksRepository.deleteLink(linkId);
    }

}
