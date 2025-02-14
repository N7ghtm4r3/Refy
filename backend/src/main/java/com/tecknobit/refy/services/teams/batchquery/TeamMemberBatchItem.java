package com.tecknobit.refy.services.teams.batchquery;

import com.tecknobit.refy.helpers.JoinTableSyncBatchItem;
import com.tecknobit.refycore.enums.TeamRole;

// TODO: 13/02/2025 TO COMMENT
// TODO: 13/02/2025 ANNOTATE AS @BatchItem
public class TeamMemberBatchItem extends JoinTableSyncBatchItem<String> {

    private final TeamRole role;

    public TeamMemberBatchItem(String memberId, String teamId, TeamRole role) {
        super(memberId, teamId);
        this.role = role;
    }

    public TeamRole getRole() {
        return role;
    }

}
