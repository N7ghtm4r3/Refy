package com.tecknobit.refy.batchitems;

import com.tecknobit.refy.helpers.JoinTableSyncBatchItem;

// TODO: 13/02/2025 TO COMMENT
// TODO: 13/02/2025 ANNOTATE AS @BatchItem
public class TeamLinkBatchItem extends JoinTableSyncBatchItem<String> {

    public TeamLinkBatchItem(String teamId, String linkId) {
        super(teamId, linkId);
    }

}
