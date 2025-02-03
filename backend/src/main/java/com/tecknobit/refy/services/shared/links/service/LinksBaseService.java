package com.tecknobit.refy.services.shared.links.service;

import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.services.RefyItemsHelper;
import org.springframework.stereotype.Service;

/**
 * The {@code LinksBaseHelper} class is useful to manage all the links database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see RefyItemsHelper
 */
@Service
public abstract class LinksBaseService<T extends RefyLink> extends RefyItemsHelper<T> {

    /**
     * Method to delete a link
     *
     * @param linkId The identifier of the link to delete
     */
    public abstract void deleteLink(String linkId);

}
