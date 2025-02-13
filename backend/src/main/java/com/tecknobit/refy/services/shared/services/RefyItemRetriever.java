package com.tecknobit.refy.services.shared.services;

import com.tecknobit.refy.services.shared.entities.RefyItem;

// TODO: 13/02/2025 TO COMMENT
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
