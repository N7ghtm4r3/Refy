package com.tecknobit.refy.batchitems;

import com.tecknobit.refy.helpers.JoinTableSyncBatchItem;

import static com.tecknobit.refycore.ConstantsKt.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.LINK_IDENTIFIER_KEY;

// TODO: 13/02/2025 TO COMMENT
// TODO: 13/02/2025 ANNOTATE AS @BatchItem
public class CollectionLinkBatchItem extends JoinTableSyncBatchItem<String> {

    public static final String[] COLLECTION_LINK_JOIN_TABLE_COLUMNS = new String[]{COLLECTION_IDENTIFIER_KEY,
            LINK_IDENTIFIER_KEY};

    public CollectionLinkBatchItem(String collectionId, String linkId) {
        super(collectionId, linkId);
    }

}
