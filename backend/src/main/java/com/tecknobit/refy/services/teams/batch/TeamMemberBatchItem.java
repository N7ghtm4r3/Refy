package com.tecknobit.refy.services.teams.batch;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.equinoxbackend.apis.batch.JoinTableSyncBatchItem;
import com.tecknobit.refycore.enums.TeamRole;

/**
 * The {@code TeamMemberBatchItem} is used during the batch synchronization to handle the members of a team
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JoinTableSyncBatchItem
 */
@BatchQueryItem
public class TeamMemberBatchItem extends JoinTableSyncBatchItem<String, String> {

    /**
     * {@code role} The role of the member
     */
    private final TeamRole role;

    /**
     * Constructor to instantiate the batch item
     *
     * @param memberId The identifier of the member
     * @param teamId   The identifier of the team
     * @param role     The role of the member
     */
    public TeamMemberBatchItem(String memberId, String teamId, TeamRole role) {
        super(memberId, teamId);
        this.role = role;
    }

    /**
     * Method to get the {@link #role} instance
     *
     * @return the {@link #role} instance as {@link TeamRole}
     */
    public TeamRole getRole() {
        return role;
    }

}
