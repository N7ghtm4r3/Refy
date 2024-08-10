package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxUser.PROFILE_PIC_KEY;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
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
            fetch = FetchType.EAGER,
            mappedBy = SOURCE_TEAM_KEY
    )
    @JsonIgnoreProperties({
            SOURCE_TEAM_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<RefyTeamMember> members;

    @Transient
    private HashSet<String> membersMapping;

    @ManyToMany(
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = TEAMS_LINKS_TABLE,
            joinColumns = {@JoinColumn(name = TEAM_IDENTIFIER_KEY)},
            inverseJoinColumns = {@JoinColumn(name = LINK_IDENTIFIER_KEY)},
            uniqueConstraints = @UniqueConstraint(
                    columnNames =  {TEAM_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY }
            )
    )
    @JsonIgnoreProperties({
            TEAMS_KEY,
            COLLECTIONS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<RefyLink> links;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = COLLECTIONS_TEAMS_TABLE,
            joinColumns = {@JoinColumn(name = TEAM_IDENTIFIER_KEY)},
            inverseJoinColumns = {@JoinColumn(name = COLLECTION_IDENTIFIER_KEY)},
            uniqueConstraints = @UniqueConstraint(
                    columnNames = { TEAM_IDENTIFIER_KEY, COLLECTION_IDENTIFIER_KEY }
            )
    )
    @JsonIgnoreProperties({
            TEAMS_KEY,
            LINKS_KEY,
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

    @JsonGetter(LOGO_PIC_KEY)
    public String getLogoPic() {
        return logoPic;
    }

    public List<RefyTeamMember> getMembers() {
        return members;
    }

    public boolean hasMembers() {
        return members.size() > 1;
    }

    public boolean hasAdmins(String exceptId) {
        for (RefyTeamMember member : members) {
            String memberId = member.getId();
            if(!exceptId.equals(memberId) && isAdmin(memberId))
                return true;
        }
        return false;
    }

    @JsonIgnore
    public RefyTeamMember getViewer() {
        for (RefyTeamMember member : members)
            if(!isAdmin(member.getId()))
                return member;
        return null;
    }

    @JsonIgnore
    public List<String> getMembersIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (RefyTeamMember team : members)
            ids.add(team.getId());
        membersMapping = new HashSet<>(ids);
        return ids;
    }

    public boolean hasMember(String memberId) {
        if(membersMapping == null)
            getMembersIds();
        return membersMapping.contains(memberId);
    }

    public List<RefyLink> getLinks() {
        return links;
    }

    @JsonIgnore
    public List<String> getLinkIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (RefyLink link : links)
            ids.add(link.getId());
        return ids;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

    @JsonIgnore
    public List<String> getCollectionsIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (LinksCollection collection : collections)
            ids.add(collection.getId());
        return ids;
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
    public static class RefyTeamMember {

        public static final String MEMBER_IDENTIFIER_KEY = "member_id";

        public static final String TEAM_ROLE_KEY = "team_role";

        @Transient
        protected final JsonHelper hItem;

        public enum TeamRole {

            ADMIN,

            VIEWER

        }

        @Id
        @ManyToOne(
                fetch = FetchType.LAZY,
                cascade = CascadeType.ALL
        )
        @JoinColumn(name = OWNER_KEY)
        @JsonIgnoreProperties({
                "hibernateLazyInitializer",
                "handler"
        })
        @OnDelete(action = OnDeleteAction.CASCADE)
        private final RefyUser owner;

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
        @OnDelete(action = OnDeleteAction.CASCADE)
        protected final Team sourceTeam;

        public RefyTeamMember() {
            this(new RefyUser(), null, null);
        }

        //TODO: TO REMOVE
        public RefyTeamMember(String id) {
            this(new RefyUser(id), null, null);
        }

        //TODO: TO REMOVE
        public RefyTeamMember(String id, String name, String surname, String email, String profilePic, String tagName,
                              TeamRole role) {
            this(id, name, surname, email, profilePic, tagName, role, null);
        }

        //TODO: TO REMOVE
        public RefyTeamMember(String id, String name, String surname, String email, String profilePic, String tagName,
                              TeamRole role, Team sourceTeam) {
            hItem = null;
            owner = new RefyUser(id, true);
            this.role = role;
            this.sourceTeam = sourceTeam;
        }

        public RefyTeamMember(RefyUser owner, TeamRole role, Team sourceTeam) {
            hItem = null;
            this.owner = owner;
            this.role = role;
            this.sourceTeam = sourceTeam;
        }

        public RefyTeamMember(JSONObject jRefyTeamMember) {
            hItem = new JsonHelper(jRefyTeamMember);
            //TODO: TO LOAD CORRECTLY
            owner = null;
            role = TeamRole.valueOf(TEAM_ROLE_KEY);
            sourceTeam = null;
        }

        public String getId() {
            return owner.getId();
        }

        @JsonGetter(TAG_NAME_KEY)
        public String getTagName() {
            return owner.getTagName();
        }

        public String getName() {
            return owner.getName();
        }

        public String getSurname() {
            return owner.getSurname();
        }

        @JsonIgnore
        public String getCompleteName() {
            return owner.getCompleteName();
        }

        public String getEmail() {
            return owner.getEmail();
        }

        @JsonGetter(PROFILE_PIC_KEY)
        public String getProfilePic() {
            return owner.getProfilePic();
        }

        public TeamRole getRole() {
            return role;
        }

    }

}
