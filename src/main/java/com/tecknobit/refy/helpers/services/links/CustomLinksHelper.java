package com.tecknobit.refy.helpers.services.links;

import com.tecknobit.refy.helpers.services.repositories.links.CustomLinksRepository;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.refycore.helpers.RefyEndpointsSet.CUSTOM_LINKS_ENDPOINT;
import static com.tecknobit.refycore.records.links.CustomRefyLink.*;

@Service
public class CustomLinksHelper extends LinksBaseHelper<CustomRefyLink> {

    private static final String ATTACH_RESOURCES_TO_CUSTOM_LINK_QUERY =
            "REPLACE INTO " + RESOURCES_KEY +
                    "(" +
                    IDENTIFIER_KEY + "," +
                    RESOURCE_VALUE_KEY + "," +
                    RESOURCE_KEY +
                    ")" +
                    " VALUES ";

    private static final String DETACH_RESOURCES_FROM_CUSTOM_LINK_QUERY =
            "DELETE FROM " + RESOURCES_KEY + " WHERE "
                    + IDENTIFIER_KEY + "='%s' " + "AND " + RESOURCE_KEY + " IN (";

    private static final String ATTACH_FIELDS_TO_CUSTOM_LINK_QUERY =
            "REPLACE INTO " + FIELDS_KEY +
                    "(" +
                    IDENTIFIER_KEY + "," +
                    FIELD_VALUE_KEY + "," +
                    FIELD_KEY +
                    ")" +
                    " VALUES ";

    private static final String DETACH_FIELDS_FROM_CUSTOM_LINK_QUERY =
            "DELETE FROM " + FIELDS_KEY + " WHERE "
                    + IDENTIFIER_KEY + "='%s' " + "AND " + FIELD_KEY + " IN (";

    @Autowired
    private CustomLinksRepository customLinksRepository;

    public CustomRefyLink findById(String linkId) {
        return customLinksRepository.findById(linkId).orElse(null);
    }

    public List<CustomRefyLink> getUserCustomLinks(String userId) {
        return customLinksRepository.getUserCustomLinks(userId);
    }

    public void createCustomLink(String userId, String linkId, String title, String description, boolean hasUniqueAccess,
                                 ExpiredTime expiredTime, Map<String, Object> resources, Map<String, Object> fields) {
        customLinksRepository.saveLink(CUSTOM_LINK_KEY, linkId, title, description, CUSTOM_LINKS_ENDPOINT
                        + "/" + userId, System.currentTimeMillis(), expiredTime, hasUniqueAccess, userId);
        attachMap(linkId, ATTACH_RESOURCES_TO_CUSTOM_LINK_QUERY, resources);
        attachMap(linkId, ATTACH_FIELDS_TO_CUSTOM_LINK_QUERY, fields);
    }

    private void attachMap(String linkId, String attachQuery, Map<String, Object> map) {
        executeInsertBatch(attachQuery, TUPLE_VALUES_SLICE, map.values(), query -> {
            int index = 1;
            for (String key : map.keySet()) {
                query.setParameter(index++, linkId);
                query.setParameter(index++, key);
                query.setParameter(index++, map.get(key));
            }
        });
    }

    public void editCustomLink(String userId, String linkId, String title, String description, boolean hasUniqueAccess,
                               ExpiredTime expiredTime, Map<String, Object> resources, Map<String, Object> fields) {
        CustomRefyLink customRefyLink = getItemIfAllowed(userId, linkId);
        customLinksRepository.updateLink(linkId, title, description, expiredTime, hasUniqueAccess, userId);
        editMap(linkId, ATTACH_RESOURCES_TO_CUSTOM_LINK_QUERY, DETACH_RESOURCES_FROM_CUSTOM_LINK_QUERY,
                customRefyLink.getResources(), resources);
        editMap(linkId, ATTACH_FIELDS_TO_CUSTOM_LINK_QUERY, DETACH_FIELDS_FROM_CUSTOM_LINK_QUERY,
                customRefyLink.getFields(), fields);
    }

    private void editMap(String linkId, String attachQuery, String detachQuery, Map<String, String> currentMap,
                         Map<String, Object> map) {
        manageAttachments(
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
        );
    }

    @Override
    public CustomRefyLink getItemIfAllowed(String userId, String linkId) {
        return customLinksRepository.getLinkIfAllowed(userId, linkId);
    }

    @Override
    public void deleteLink(String linkId) {
        customLinksRepository.deleteLink(linkId);
    }

}
