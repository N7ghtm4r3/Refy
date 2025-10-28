package com.tecknobit.refy.services.shared.batch.items;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.equinoxbackend.apis.batch.JoinTableSyncBatchItem;

import static com.tecknobit.refycore.ConstantsKt.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.TEAM_IDENTIFIER_KEY;

/**
 * The {@code TeamCollectionBatchItem} is used during the batch synchronization to share a collection with a team
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JoinTableSyncBatchItem
 */
@BatchQueryItem
public class TeamCollectionBatchItem extends JoinTableSyncBatchItem<String, String> {

    /**
     * {@code TEAM_COLLECTION_JOIN_TABLE_COLUMNS} the columns of the join table used during the synchronization, the
     * {@code collections_teams} table
     */
    public static final String[] TEAM_COLLECTION_JOIN_TABLE_COLUMNS = new String[]{TEAM_IDENTIFIER_KEY,
            COLLECTION_IDENTIFIER_KEY};

    /**
     * Constructor to instantiate the batch item
     *
     * @param teamId       The identifier of the team
     * @param collectionId The identifier of the collection
     */
    public TeamCollectionBatchItem(String teamId, String collectionId) {
        super(teamId, collectionId);
    }

}
