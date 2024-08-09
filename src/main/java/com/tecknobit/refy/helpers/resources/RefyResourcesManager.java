package com.tecknobit.refy.helpers.resources;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.equinox.resourcesutils.ResourcesManager;
import org.springframework.web.multipart.MultipartFile;

public interface RefyResourcesManager extends ResourcesManager {

    String LOGOS_DIRECTORY = "logos";

    @Wrapper
    default String createLogoResource(MultipartFile resource, String resourceId) {
        return createResource(resource, LOGOS_DIRECTORY, resourceId);
    }

    @Wrapper
    default boolean deleteLogoResource(String teamId) {
        return deleteResource(LOGOS_DIRECTORY, teamId);
    }

}
