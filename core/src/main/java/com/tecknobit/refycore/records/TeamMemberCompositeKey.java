package com.tecknobit.refycore.records;

import com.tecknobit.refycore.records.Team.RefyTeamMember;

/**
 * The {@code TeamMemberCompositeKey} is useful for the {@link RefyTeamMember} class to specify its ids
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class TeamMemberCompositeKey {

    /**
     * {@code owner} the {@link RefyUser} who the member is linked
     */
    private String owner;

    /**
     * {@code sourceTeam} the team of the member
     */
    private String sourceTeam;

    /**
     * Constructor to init the {@link Team} class <br>
     *
     * No-any params required
     * @apiNote empty constructor required
     */
    public TeamMemberCompositeKey() {
    }

    /**
     * Constructor to init the {@link TeamMemberCompositeKey} class
     *
     * @param owner: the {@link RefyUser} who the member is linked
     * @param sourceTeam: the team of the member
     *
     */
    public TeamMemberCompositeKey(String owner, String sourceTeam) {
        this.owner = owner;
        this.sourceTeam = sourceTeam;
    }

    /**
     * Method to get {@link #owner} instance <br>
     * No-any params required
     *
     * @return {@link #owner} instance as {@link String}
     */
    public String getId() {
        return owner;
    }

    /**
     * Method to get {@link #sourceTeam} instance <br>
     * No-any params required
     *
     * @return {@link #sourceTeam} instance as {@link String}
     */
    public String getSourceTeam() {
        return sourceTeam;
    }

}
