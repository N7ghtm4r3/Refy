package com.tecknobit.refycore.records;

public class TeamMemberCompositeKey {

    private String id;

    private String sourceTeam;

    public TeamMemberCompositeKey() {
    }

    public TeamMemberCompositeKey(String id, String sourceTeam) {
        this.id = id;
        this.sourceTeam = sourceTeam;
    }

    public String getId() {
        return id;
    }

    public String getSourceTeam() {
        return sourceTeam;
    }

}
