package com.tecknobit.refy.batchitems;

import com.tecknobit.refy.helpers.JoinTableSyncBatchItem;

import static com.tecknobit.refycore.ConstantsKt.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.TEAM_IDENTIFIER_KEY;

// TODO: 13/02/2025 TO COMMENT
// TODO: 13/02/2025 ANNOTATE AS @BatchItem
public class TeamCollectionBatchItem extends JoinTableSyncBatchItem<String> {

    public static final String[] TEAM_COLLECTION_JOIN_TABLE_COLUMNS = new String[]{TEAM_IDENTIFIER_KEY,
            COLLECTION_IDENTIFIER_KEY};

    public TeamCollectionBatchItem(String teamId, String collectionId) {
        super(teamId, collectionId);
    }

}
