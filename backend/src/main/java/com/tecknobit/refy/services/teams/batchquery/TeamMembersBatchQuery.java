package com.tecknobit.refy.services.teams.batchquery;

import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import com.tecknobit.refy.services.teams.service.TeamsService.TeamPayload;
import com.tecknobit.refycore.dtos.AddedMember;
import com.tecknobit.refycore.enums.TeamRole;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.enums.TeamRole.ADMIN;

// TODO: 14/02/2025 TO COMMENT
// TODO: 14/02/2025 ANNOTATE AS @BatchQueryImpl
public class TeamMembersBatchQuery implements EquinoxItemsHelper.BatchQuery<AddedMember> {

    public static final String[] MEMBERS_TABLE_COLUMNS = new String[]{OWNER_KEY, TEAM_IDENTIFIER_KEY, TEAM_ROLE_KEY};

    private final String userId;

    private final String teamId;

    private final ArrayList<AddedMember> members;

    public TeamMembersBatchQuery(String userId, String teamId, TeamPayload payload) {
        this.userId = userId;
        this.teamId = teamId;
        members = extractRawMembersFromPayload(payload);
    }

    private ArrayList<AddedMember> extractRawMembersFromPayload(TeamPayload payload) {
        JSONArray payloadMembers = payload.members();
        ArrayList<AddedMember> members = new ArrayList<>();
        for (int j = 0; j < payloadMembers.length(); j++) {
            JSONObject rawMember = payloadMembers.getJSONObject(j);
            members.add(new AddedMember(
                    rawMember.getString(MEMBER_IDENTIFIER_KEY),
                    rawMember.getEnum(TeamRole.class, TEAM_ROLE_KEY)
            ));
        }
        members.add(new AddedMember(userId, ADMIN));
        return members;
    }

    @Override
    public Collection<AddedMember> getData() {
        return members;
    }

    @Override
    public void prepareQuery(Query query, int index, Collection<AddedMember> members) {
        for (AddedMember member : members) {
            query.setParameter(index++, member.getMemberId());
            query.setParameter(index++, teamId);
            query.setParameter(index++, member.getRole().name());
        }
    }

    @Override
    public String[] getColumns() {
        return MEMBERS_TABLE_COLUMNS;
    }

}
