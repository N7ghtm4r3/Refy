package com.tecknobit.refy.services.shared.batch.items;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.equinoxbackend.batch.JoinTableSyncBatchItem;

import static com.tecknobit.refycore.ConstantsKt.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.LINK_IDENTIFIER_KEY;

/**
 * The {@code CollectionLinkBatchItem} is used during the batch synchronization to share a link with a collection
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JoinTableSyncBatchItem
 */
@BatchQueryItem
public class CollectionLinkBatchItem extends JoinTableSyncBatchItem<String, String> {

    /**
     * {@code COLLECTION_LINK_JOIN_TABLE_COLUMNS} the columns of the join table used during the synchronization, the
     * {@code collection_links} table
     */
    public static final String[] COLLECTION_LINK_JOIN_TABLE_COLUMNS = new String[]{COLLECTION_IDENTIFIER_KEY,
            LINK_IDENTIFIER_KEY};

    /**
     * Constructor to instantiate the batch item
     *
     * @param collectionId The identifier of the collection
     * @param linkId       The identifier of the link
     */
    public CollectionLinkBatchItem(String collectionId, String linkId) {
        super(collectionId, linkId);
    }

}
