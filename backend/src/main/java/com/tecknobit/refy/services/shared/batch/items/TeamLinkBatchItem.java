package com.tecknobit.refy.services.shared.batch.items;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.equinoxbackend.batch.JoinTableSyncBatchItem;

/**
 * The {@code TeamLinkBatchItem} is used during the batch synchronization to share a link with a team
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JoinTableSyncBatchItem
 */
@BatchQueryItem
public class TeamLinkBatchItem extends JoinTableSyncBatchItem<String, String> {

    /**
     * Constructor to instantiate the batch item
     *
     * @param teamId The identifier of the team
     * @param linkId The identifier of the link
     */
    public TeamLinkBatchItem(String teamId, String linkId) {
        super(teamId, linkId);
    }

}
