package com.tecknobit.refy.services.customlinks.service;

import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink;
import com.tecknobit.refy.services.customlinks.repository.CustomLinksRepository;
import com.tecknobit.refy.services.links.service.LinksService;
import com.tecknobit.refy.services.shared.links.service.LinksBaseService;
import com.tecknobit.refycore.enums.ExpiredTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;
import static com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager.RESOURCES_KEY;
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
public class CustomLinksService extends LinksBaseService {

    /**
     * {@code ATTACH_RESOURCES_TO_CUSTOM_LINK_QUERY} the query used to attach the resources to the link
     */
    private static final String ATTACH_RESOURCES_TO_CUSTOM_LINK_QUERY =
            "REPLACE INTO " + RESOURCES_KEY +
                    "(" +
                    IDENTIFIER_KEY + "," +
                    RESOURCE_VALUE_KEY + "," +
                    RESOURCE_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code DETACH_RESOURCES_FROM_CUSTOM_LINK_QUERY} the query used to detach the resources from the link
     */
    private static final String DETACH_RESOURCES_FROM_CUSTOM_LINK_QUERY =
            "DELETE FROM " + RESOURCES_KEY + " WHERE "
                    + IDENTIFIER_KEY + "='%s' " + "AND " + RESOURCE_KEY + " IN (";

    /**
     * {@code ATTACH_FIELDS_TO_CUSTOM_LINK_QUERY} the query used to attach the fields to the link
     */
    private static final String ATTACH_FIELDS_TO_CUSTOM_LINK_QUERY =
            "REPLACE INTO " + FIELDS_KEY +
                    "(" +
                    IDENTIFIER_KEY + "," +
                    FIELD_VALUE_KEY + "," +
                    FIELD_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code DETACH_FIELDS_FROM_CUSTOM_LINK_QUERY} the query used to detach the fields from the link
     */
    private static final String DETACH_FIELDS_FROM_CUSTOM_LINK_QUERY =
            "DELETE FROM " + FIELDS_KEY + " WHERE "
                    + IDENTIFIER_KEY + "='%s' " + "AND " + FIELD_KEY + " IN (";

    /**
     * {@code customLinksRepository} instance for the custom links repository
     */
    @Autowired
    private CustomLinksRepository customLinksRepository;

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
     *
     * @return the user custom links as {@link List} of {@link CustomRefyLink}
     */
    public List<CustomRefyLink> getUserCustomLinks(String userId) {
        return customLinksRepository.getUserCustomLinks(userId);
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
                                 ExpiredTime expiredTime, Map<String, Object> resources, Map<String, Object> fields) {
        customLinksRepository.saveLink(CUSTOM_LINK_KEY, linkId, title, description, CUSTOM_LINKS_ENDPOINT + "/" + userId,
                System.currentTimeMillis(), expiredTime, hasUniqueAccess, generateIdentifier(), userId);
        attachMap(linkId, ATTACH_RESOURCES_TO_CUSTOM_LINK_QUERY, resources);
        attachMap(linkId, ATTACH_FIELDS_TO_CUSTOM_LINK_QUERY, fields);
    }

    /**
     * Method to attach a map value to the link
     *
     * @param linkId The identifier of the link where attach the map
     * @param attachQuery The query used to attach the map
     * @param map The map to attach to the link
     */
    private void attachMap(String linkId, String attachQuery, Map<String, Object> map) {
        /*executeInsertBatch(attachQuery, TUPLE_VALUES_SLICE, map.values(), query -> {
            int index = 1;
            for (String key : map.keySet()) {
                query.setParameter(index++, linkId);
                query.setParameter(index++, map.get(key));
                query.setParameter(index++, key);
            }
        });*/
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
     * @param resources The resources attached to the link
     * @param fields The field used to protect the resources
     */
    public void editCustomLink(String userId, String linkId, String title, String description, boolean hasUniqueAccess,
                               ExpiredTime expiredTime, Map<String, Object> resources, Map<String, Object> fields) {
        CustomRefyLink customRefyLink = getItemIfAllowed(userId, linkId);
        customLinksRepository.updateLink(linkId, title, description, expiredTime, hasUniqueAccess, userId);
        editMap(linkId, ATTACH_RESOURCES_TO_CUSTOM_LINK_QUERY, DETACH_RESOURCES_FROM_CUSTOM_LINK_QUERY,
                customRefyLink.getResources(), resources);
        editMap(linkId, ATTACH_FIELDS_TO_CUSTOM_LINK_QUERY, DETACH_FIELDS_FROM_CUSTOM_LINK_QUERY,
                customRefyLink.getFields(), fields);
    }

    /**
     * Method to edit a map attached to the link
     *
     * @param linkId The identifier of the link where the map is attached
     * @param attachQuery The query used to attach the map
     * @param detachQuery The query used to detach the map
     * @param currentMap The current map attached to the link
     * @param map The new map to attach to the link
     */
    private void editMap(String linkId, String attachQuery, String detachQuery, Map<String, String> currentMap,
                         Map<String, Object> map) {
        /*manageAttachments(
                new AttachmentsManagementWorkflow() {
                    @Override
                    public List<String> getIds() {
                        return new ArrayList<>(currentMap.keySet());
                    }

                    @Override
                    public String insertQuery() {
                        return attachQuery;
                    }

                    @Override
                    public String deleteQuery() {
                        return detachQuery;
                    }
                },
                TUPLE_VALUES_SLICE,
                linkId,
                map.keySet().stream().toList(),
                query -> {
                    int index = 1;
                    for (String key : map.keySet()) {
                        query.setParameter(index++, linkId);
                        query.setParameter(index++, map.get(key));
                        query.setParameter(index++, key);
                    }
                }
        );*/
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
