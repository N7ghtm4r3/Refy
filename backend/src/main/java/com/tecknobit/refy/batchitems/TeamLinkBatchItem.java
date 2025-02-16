package com.tecknobit.refy.batchitems;

import com.tecknobit.refy.helpers.JoinTableSyncBatchItem;

/**
 * The {@code TeamLinkBatchItem} is used during the batch synchronization to share a link with a team
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JoinTableSyncBatchItem
 */
// TODO: 13/02/2025 ANNOTATE AS @BatchItem
public class TeamLinkBatchItem extends JoinTableSyncBatchItem<String> {

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
