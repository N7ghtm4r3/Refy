package com.tecknobit.refycore.records;

import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.ADMIN;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.VIEWER;

public class Team extends RefyItem {

    public static final int MAX_TEAMS_DISPLAYED = 5;

    public static final String LOGO_PIC_KEY = "logo_pic";

    private final String logoPic;

    private final List<RefyTeamMember> members;

    private final List<RefyLink> links;

    private final List<LinksCollection> collections;

    public Team() {
        this(null, null, null, null, null, List.of(), List.of(), List.of());
    }

    //TODO: TO REMOVE
    public Team(String id, String name, RefyUser author, String logoPic, String description) {
        super(id, author, name, description);
        this.logoPic = logoPic;
        this.links = List.of();
        this.collections = List.of();
        this.members = List.of(
                new RefyTeamMember(
                        "id",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://t4.ftcdn.net/jpg/03/86/82/73/360_F_386827376_uWOOhKGk6A4UVL5imUBt20Bh8cmODqzx.jpg",
                        "@id",
                        VIEWER
                ),
                new RefyTeamMember(
                        "id3213",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id3213",
                        ADMIN
                ),
                new RefyTeamMember(
                        "id2",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2",
                        TeamRole.VIEWER
                ),
                new RefyTeamMember(
                        "idwgewgw2",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2",
                        VIEWER
                ),
                new RefyTeamMember(
                        "igwegwgwegwegwegewgd2",
                        "User",
                        "LAST",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2",
                        ADMIN
                ),
                new RefyTeamMember(
                        "iegwgwed2",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2",
                        TeamRole.VIEWER
                )
        );
    }

    public Team(String id, String name, RefyUser author, String logoPic, String description, List<RefyTeamMember> members,
                List<RefyLink> links, List<LinksCollection> collections) {
        super(id, author, name, description);
        this.logoPic = logoPic;
        this.members = members;
        this.links = links;
        this.collections = collections;
    }

    public Team(JSONObject jTeam) {
        super(jTeam);
        logoPic = hItem.getString(LOGO_PIC_KEY);
        //TODO: TO LOAD CORRECTLY
        members = List.of();
        links = List.of();
        collections = List.of();
    }

    public String getLogoPic() {
        return logoPic;
    }

    public List<RefyTeamMember> getMembers() {
        return members;
    }

    public List<RefyLink> getLinks() {
        return links;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

    public boolean isAdmin(String userId) {
        if(isTheAuthor(userId))
            return true;
        for(RefyTeamMember member : members)
            if(member.getId().equals(userId))
                return member.getRole() == ADMIN;
        return false;
    }

    public boolean isTheAuthor(String userId) {
        return userId.equals(owner.getId());
    }

    public static class RefyTeamMember extends RefyUser {

        public static final String TEAM_ROLE_KEY = "team_role";

        public enum TeamRole {

            ADMIN,

            VIEWER

        }

        private final TeamRole role;

        public RefyTeamMember() {
            super();
            role = null;
        }

        public RefyTeamMember(String id, String name, String surname, String email, String profilePic, String tagName,
                              TeamRole role) {
            super(id, name, surname, email, profilePic, tagName);
            this.role = role;
        }

        public RefyTeamMember(JSONObject jRefyTeamMember) {
            super(jRefyTeamMember);
            role = TeamRole.valueOf(TEAM_ROLE_KEY);
        }

        public TeamRole getRole() {
            return role;
        }

    }

}
