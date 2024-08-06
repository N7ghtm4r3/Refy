package com.tecknobit.refycore.records;

public class TeamMemberCompositeKey {

    private String owner;

    private String sourceTeam;

    public TeamMemberCompositeKey() {
    }

    public TeamMemberCompositeKey(String owner, String sourceTeam) {
        this.owner = owner;
        this.sourceTeam = sourceTeam;
    }

    public String getId() {
        return owner;
    }

    public String getSourceTeam() {
        return sourceTeam;
    }

}
