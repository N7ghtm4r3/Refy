package com.tecknobit.refy.helpers.resources;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.equinox.resourcesutils.ResourcesManager;
import org.springframework.web.multipart.MultipartFile;

/**
 * The {@code RefyResourcesManager} interface is useful to create and manage the resources files of the Refy's system
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see com.tecknobit.equinox.resourcesutils.ResourcesManager
 */
public interface RefyResourcesManager extends ResourcesManager {

    /**
     * {@code LOGOS_DIRECTORY} the key of the <b>logos</b> folder where the logos of the team will be saved
     */
    String LOGOS_DIRECTORY = "logos";

    /**
     * Method to create the pathname for a logo picture of a team
     *
     * @param resource:   the resource from create its pathname
     * @param resourceId: the resource identifier
     * @return the pathname created for a logo picture of a team
     */
    @Wrapper
    default String createLogoResource(MultipartFile resource, String resourceId) {
        return createResource(resource, LOGOS_DIRECTORY, resourceId);
    }

    /**
     * Method to delete a logo picture of a team
     *
     * @param teamId: the team identifier of the logo picture
     * @return whether the profile pic has been deleted as boolean
     */
    @Wrapper
    default boolean deleteLogoResource(String teamId) {
        return deleteResource(LOGOS_DIRECTORY, teamId);
    }

}
