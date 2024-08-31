package com.tecknobit.refy.helpers.services.links;

import com.tecknobit.refy.helpers.services.RefyItemsHelper;
import com.tecknobit.refycore.records.links.RefyLink;
import org.springframework.stereotype.Service;

/**
 * The {@code LinksBaseHelper} class is useful to manage all the links database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see RefyItemsHelper
 */
@Service
public abstract class LinksBaseHelper<T extends RefyLink> extends RefyItemsHelper<T> {

    /**
     * Method to delete a link
     *
     * @param linkId: the identifier of the link to delete
     */
    public abstract void deleteLink(String linkId);

}
