package com.tecknobit.refy.services.shared.services;

import com.tecknobit.refy.services.shared.entities.RefyItem;

/**
 * The {@code RefyItemRetriever} interface is useful to retrieve the items from the database
 *
 * @param <T> The type of the item to retrieve
 * @author N7ghtm4r3 - Tecknobit
 */
public interface RefyItemRetriever<T extends RefyItem> {

    /**
     * Method used to get an item if the user is authorized
     *
     * @param userId The user identifier
     * @param itemId The identifier of the target item
     * @return the target item as {@link T}
     */
    T getItemIfAllowed(String userId, String itemId);

}
