package com.tecknobit.refy.services.shared.batch.items;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.equinoxbackend.apis.batch.JoinTableSyncBatchItem;

import static com.tecknobit.refycore.ConstantsKt.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.TEAM_IDENTIFIER_KEY;

/**
 * The {@code TeamLinkBatchItem} is used during the batch synchronization to share a link with a team
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JoinTableSyncBatchItem
 */
@BatchQueryItem
public class TeamLinkBatchItem extends JoinTableSyncBatchItem<String, String> {

    /**
     * {@code TEAMS_LINKS_JOIN_TABLE_COLUMNS} the columns of the join table used during the synchronization of the
     * {@code teams_links} table
     */
    public static final String[] TEAMS_LINKS_JOIN_TABLE_COLUMNS = new String[]{TEAM_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY};

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
