package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.ADMIN;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.VIEWER;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = TEAMS_KEY)
@AttributeOverride(
        name = IDENTIFIER_KEY,
        column = @Column(name = TEAM_IDENTIFIER_KEY)
)
public class Team extends RefyItem {

    public static final int MAX_TEAMS_DISPLAYED = 5;

    public static final String TEAMS_LINKS_TABLE = "teams_links";

    public static final String COLLECTIONS_TEAMS_TABLE = "collections_teams";

    public static final String TEAM_IDENTIFIER_KEY = "team_id";

    public static final String TEAM_KEY = "team";

    public static final String SOURCE_TEAM_KEY = "sourceTeam";

    public static final String LOGO_PIC_KEY = "logo_pic";

    public static final String MEMBERS_KEY = "members";

    @Column(name = LOGO_PIC_KEY)
    private final String logoPic;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = SOURCE_TEAM_KEY
    )
    @JsonIgnoreProperties({
            SOURCE_TEAM_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<RefyTeamMember> members;

    @ManyToMany(
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = TEAMS_LINKS_TABLE,
            joinColumns = {@JoinColumn(name = TEAM_IDENTIFIER_KEY)},
            inverseJoinColumns = {@JoinColumn(name = LINK_IDENTIFIER_KEY)}
    )
    @JsonIgnoreProperties({
            OWNER_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<RefyLink> links;

    @ManyToMany(
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = COLLECTIONS_TEAMS_TABLE,
            joinColumns = {@JoinColumn(name = TEAM_IDENTIFIER_KEY)},
            inverseJoinColumns = {@JoinColumn(name = COLLECTION_IDENTIFIER_KEY)}
    )
    @JsonIgnoreProperties({
            OWNER_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
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

    @Entity
    @Table(name = MEMBERS_KEY)
    @IdClass(TeamMemberCompositeKey.class)
    public static class RefyTeamMember extends EquinoxItem {

        public static final String MEMBER_IDENTIFIER_KEY = "member_id";

        public static final String TEAM_ROLE_KEY = "team_role";

        public enum TeamRole {

            ADMIN,

            VIEWER

        }

        @Column(
                name = TAG_NAME_KEY,
                columnDefinition = "VARCHAR(15) UNIQUE NOT NULL"
        )
        private final String tagName;

        /**
         * {@code name} the name of the user
         */
        @Column(
                name = NAME_KEY,
                columnDefinition = "VARCHAR(20) NOT NULL"
        )
        private final String name;

        /**
         * {@code surname} the surname of the user
         */
        @Column(
                name = SURNAME_KEY,
                columnDefinition = "VARCHAR(30) NOT NULL"
        )
        private final String surname;

        /**
         * {@code email} the email of the user
         */
        @Column(
                name = EMAIL_KEY,
                columnDefinition = "VARCHAR(75) NOT NULL",
                unique = true
        )
        private final String email;

        /**
         * {@code profilePic} the profile pic of the user
         */
        @Column(
                name = PROFILE_PIC_KEY,
                columnDefinition = "TEXT DEFAULT '" + DEFAULT_PROFILE_PIC + "'",
                insertable = false
        )
        protected final String profilePic;

        @Enumerated(value = STRING)
        @Column(name = TEAM_ROLE_KEY)
        private final TeamRole role;

        @Id
        @ManyToOne(
                cascade = CascadeType.ALL
        )
        @JoinColumn(name = TEAM_IDENTIFIER_KEY)
        @JsonIgnoreProperties({
                "hibernateLazyInitializer",
                "handler"
        })
        protected final Team sourceTeam;

        public RefyTeamMember() {
            this(null, null, null, null, null, null, null);
        }

        public RefyTeamMember(String id, String name, String surname, String email, String profilePic, String tagName,
                              TeamRole role) {
            this(id, name, surname, email, profilePic, tagName, role, null);
        }

        public RefyTeamMember(String id, String name, String surname, String email, String profilePic, String tagName,
                              TeamRole role, Team sourceTeam) {
            super(id);
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.profilePic = profilePic;
            this.tagName = tagName;
            this.role = role;
            this.sourceTeam = sourceTeam;
        }

        public RefyTeamMember(JSONObject jRefyTeamMember) {
            super(jRefyTeamMember);
            tagName = hItem.getString(TAG_NAME_KEY);
            name = hItem.getString(NAME_KEY);
            surname = hItem.getString(SURNAME_KEY);
            email = hItem.getString(EMAIL_KEY);
            profilePic = hItem.getString(PROFILE_PIC_KEY);
            role = TeamRole.valueOf(TEAM_ROLE_KEY);
            sourceTeam = null;
        }

        public String getTagName() {
            return tagName;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public String getCompleteName() {
            return name + " " + surname;
        }

        public String getEmail() {
            return email;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public Team getSourceTeam() {
            return sourceTeam;
        }

        public TeamRole getRole() {
            return role;
        }

    }

}
