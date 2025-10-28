package com.tecknobit.refy.services.teams.batch;

import com.tecknobit.equinoxbackend.annotations.BatchQueryImpl;
import com.tecknobit.equinoxbackend.apis.batch.EquinoxItemsHelper.BatchQuery;
import com.tecknobit.refy.services.teams.service.TeamsService.TeamPayload;
import com.tecknobit.refycore.dtos.AddedMember;
import com.tecknobit.refycore.enums.TeamRole;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.OWNER_KEY;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.enums.TeamRole.ADMIN;

/**
 * The {@code TeamMembersBatchQuery} is the batch query used to handle the members of a team
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see BatchQuery
 */
@BatchQueryImpl
public class TeamMembersBatchQuery implements BatchQuery<AddedMember> {

    /**
     * {@code MEMBERS_TABLE_COLUMNS} the columns of the table used to store the members of a team
     */
    public static final String[] MEMBERS_TABLE_COLUMNS = new String[]{OWNER_KEY, TEAM_IDENTIFIER_KEY, TEAM_ROLE_KEY};

    /**
     * {@code userId} the identifier of the user (member of the team)
     */
    private final String userId;

    /**
     * {@code teamId} the identifier of the team
     */
    private final String teamId;

    /**
     * {@code members} the list of the members added to the team
     */
    private final ArrayList<AddedMember> members;

    /**
     * Constructor to instantiate the batch item
     *
     * @param userId  the identifier of the user (member of the team)
     * @param teamId  The identifier of the team
     * @param payload The payload used during for the request
     */
    public TeamMembersBatchQuery(String userId, String teamId, TeamPayload payload) {
        this.userId = userId;
        this.teamId = teamId;
        members = extractRawMembersFromPayload(payload);
    }

    /**
     * Method to extract from the payload the list of the added members
     *
     * @param payload The payload used during for the request
     * @return the list of the added members as {@link ArrayList} of {@link AddedMember}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AddedMember> getData() {
        return members;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareQuery(Query query, int index, Collection<AddedMember> members) {
        for (AddedMember member : members) {
            query.setParameter(index++, member.getMemberId());
            query.setParameter(index++, teamId);
            query.setParameter(index++, member.getRole().name());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getColumns() {
        return MEMBERS_TABLE_COLUMNS;
    }

}
