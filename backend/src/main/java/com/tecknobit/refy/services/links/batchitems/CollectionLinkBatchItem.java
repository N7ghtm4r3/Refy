package com.tecknobit.refy.services.links.batchitems;

import com.tecknobit.refy.helpers.JoinTableSyncBatchItem;

// TODO: 13/02/2025 TO COMMENT
// TODO: 13/02/2025 ANNOTATE AS @BatchItem
public class CollectionLinkBatchItem extends JoinTableSyncBatchItem<String> {

    public CollectionLinkBatchItem(String collectionId, String linkId) {
        super(collectionId, linkId);
    }

}
