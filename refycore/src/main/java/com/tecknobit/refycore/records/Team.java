package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxUser.PROFILE_PIC_KEY;
import static com.tecknobit.refycore.records.LinksCollection.IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.LinksCollection.*;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole.ADMIN;
import static com.tecknobit.refycore.records.Team.RefyTeamMember.returnMembers;
import static com.tecknobit.refycore.records.Team.TEAM_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.returnLinks;
import static jakarta.persistence.EnumType.STRING;

/**
 * The {@code Team} class is useful to represent a team
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see RefyItem
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Entity
@Table(name = TEAMS_KEY)
@AttributeOverride(
        name = IDENTIFIER_KEY,
        column = @Column(name = TEAM_IDENTIFIER_KEY)
)
public class Team extends RefyItem {

    /**
     * {@code MAX_TEAMS_DISPLAYED} number of teams displayed 
     */
    public static final int MAX_TEAMS_DISPLAYED = 5;

    /**
     * {@code TEAMS_LINKS_TABLE} the key for the <b>"teams_links"</b> table
     */
    public static final String TEAMS_LINKS_TABLE = "teams_links";

    /**
     * {@code COLLECTIONS_TEAMS_TABLE} the key for the <b>"collections_teams"</b> table
     */
    public static final String COLLECTIONS_TEAMS_TABLE = "collections_teams";

    /**
     * {@code TEAM_IDENTIFIER_KEY} the key for the <b>"team_id"</b> field
     */
    public static final String TEAM_IDENTIFIER_KEY = "team_id";

    /**
     * {@code TEAM_KEY} the key for the <b>"team"</b> field
     */
    public static final String TEAM_KEY = "team";

    /**
     * {@code SOURCE_TEAM_KEY} the key for the <b>"sourceTeam"</b> field
     */
    public static final String SOURCE_TEAM_KEY = "sourceTeam";

    /**
     * {@code LOGO_PIC_KEY} the key for the <b>"logo_pic"</b> field
     */
    public static final String LOGO_PIC_KEY = "logo_pic";

    /**
     * {@code members} the key for the <b>"members"</b> field
     */
    public static final String MEMBERS_KEY = "members";

    /**
     * {@code logoPic} the logo picture of the team
     */
    @Column(name = LOGO_PIC_KEY)
    private final String logoPic;

    /**
     * {@code members} the members of the team
     */
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

    /**
     * {@code membersMapping} the map used by the {@link #hasMember(String)} method
     */
    @Transient
    private HashSet<String> membersMapping;

    /**
     * {@code links} the links shared with the team
     */
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

    /**
     * {@code collections} the collections shared with the team
     */
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

    /**
     * Constructor to init the {@link Team} class <br>
     *
     * No-any params required
     * @apiNote empty constructor required
     */
    public Team() {
        this(null, null, null, null, null, List.of(), List.of(), List.of());
    }

    /**
     * Constructor to init the {@link RefyItem} class
     *
     * @param id: the identifier of the team
     * @param title: the title of the team
     * @param author: the author of the team
     * @param logoPic: the logo picture of the team
     * @param description: the description of the team
     * @param members: the members of the team
     * @param links: the links shared with the team
     * @param collections: the collections shared with the team
     *
     */
    public Team(String id, String title, RefyUser author, String logoPic, String description, List<RefyTeamMember> members,
                List<RefyLink> links, List<LinksCollection> collections) {
        super(id, author, title, description);
        this.logoPic = logoPic;
        this.members = members;
        this.links = links;
        this.collections = collections;
    }

    /**
     * Constructor to init the {@link Team} class
     *
     * @param jTeam: team details formatted as JSON
     */
    public Team(JSONObject jTeam) {
        super(jTeam);
        logoPic = hItem.getString(LOGO_PIC_KEY);
        members = returnMembers(hItem.getJSONArray(MEMBERS_KEY));
        links = returnLinks(hItem.getJSONArray(LINKS_KEY));
        collections = returnCollections(hItem.getJSONArray(COLLECTIONS_KEY));
    }

    /**
     * Method to get {@link #logoPic} instance <br>
     * No-any params required
     *
     * @return {@link #logoPic} instance as {@link String}
     */
    @JsonGetter(LOGO_PIC_KEY)
    public String getLogoPic() {
        return logoPic;
    }

    /**
     * Method to get {@link #members} instance <br>
     * No-any params required
     *
     * @return {@link #members} instance as {@link List} of {@link RefyTeamMember}
     */
    public List<RefyTeamMember> getMembers() {
        return members;
    }

    /**
     * Method to get whether the team has members apart the author <br>
     * No-any params required
     *
     * @return whether the team has members apart the author as boolean
     */
    public boolean hasMembers() {
        return members.size() > 1;
    }

    /**
     * Method to get whether the team has admins
     *
     * @param exceptId: the identifier to not check, admin who is leaving the team
     *
     * @return whether the team has members apart the author as boolean
     */
    public boolean hasAdmins(String exceptId) {
        for (RefyTeamMember member : members) {
            String memberId = member.getId();
            if(!exceptId.equals(memberId) && isAdmin(memberId))
                return true;
        }
        return false;
    }

    /**
     * Method to get the first member who is a viewer <br>
     * No-any params required
     *
     * @return the first member who is a viewer as {@link RefyTeamMember}
     */
    @JsonIgnore
    public RefyTeamMember getViewer() {
        for (RefyTeamMember member : members)
            if(!isAdmin(member.getId()))
                return member;
        return null;
    }

    /**
     * Method to get the ids of the {@link #members} <br>
     * No-any params required
     *
     * @return ids of the {@link #members} as {@link List} of {@link String}
     */
    @JsonIgnore
    public List<String> getMembersIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (RefyTeamMember team : members)
            ids.add(team.getId());
        membersMapping = new HashSet<>(ids);
        return ids;
    }

    /**
     * Method to get whether a member is in the team
     *
     * @param memberId: the member to check if is in the team
     *
     * @return whether a member is in the team as boolean
     */
    public boolean hasMember(String memberId) {
        if(membersMapping == null)
            getMembersIds();
        return membersMapping.contains(memberId);
    }

    /**
     * Method to get {@link #links} instance <br>
     * No-any params required
     *
     * @return {@link #links} instance as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getLinks() {
        return links;
    }

    /**
     * Method to get the ids of the {@link #links} <br>
     * No-any params required
     *
     * @return ids of the {@link #links} as {@link List} of {@link String}
     */
    @JsonIgnore
    public List<String> getLinkIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (RefyLink link : links)
            ids.add(link.getId());
        return ids;
    }

    /**
     * Method to get {@link #collections} instance <br>
     * No-any params required
     *
     * @return {@link #collections} instance as {@link List} of {@link LinksCollection}
     */
    public List<LinksCollection> getCollections() {
        return collections;
    }

    /**
     * Method to get the ids of the {@link #collections} <br>
     * No-any params required
     *
     * @return ids of the {@link #collections} as {@link List} of {@link String}
     */
    @JsonIgnore
    public List<String> getCollectionsIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (LinksCollection collection : collections)
            ids.add(collection.getId());
        return ids;
    }

    /**
     * Method to get whether a member is an admin
     *
     * @param memberId: the member to check if is an admin
     *
     * @return whether a member is in the team as boolean
     */
    public boolean isAdmin(String memberId) {
        if(isTheAuthor(memberId))
            return true;
        for(RefyTeamMember member : members)
            if(member.getId().equals(memberId))
                return member.getRole() == ADMIN;
        return false;
    }

    /**
     * Method to get whether a member is the team author
     *
     * @param memberId: the member to check if the team author
     *
     * @return whether a member is the team author as boolean
     */
    public boolean isTheAuthor(String memberId) {
        return memberId.equals(owner.getId());
    }

    /**
     * Method to assemble and return an {@link ArrayList} of teams
     *
     * @param jTeams: teams list details formatted as JSON
     *
     * @return the team list as {@link ArrayList} of {@link Team}
     */
    @Returner
    public static ArrayList<Team> returnTeams(JSONArray jTeams) {
        ArrayList<Team> teams = new ArrayList<>();
        if (jTeams == null)
            return teams;
        for (int j = 0; j < jTeams.length(); j++)
            teams.add(new Team(jTeams.getJSONObject(j)));
        return teams;
    }

    /**
     * The {@code RefyTeamMember} class is useful to represent a member of a team
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    @Entity
    @Table(name = MEMBERS_KEY)
    @IdClass(TeamMemberCompositeKey.class)
    public static class RefyTeamMember {

        /**
         * {@code MEMBER_IDENTIFIER_KEY} the key for the <b>"member_id"</b> field
         */
        public static final String MEMBER_IDENTIFIER_KEY = "member_id";

        /**
         * {@code TEAM_ROLE_KEY} the key for the <b>"team_role"</b> field
         */
        public static final String TEAM_ROLE_KEY = "team_role";

        /**
         * {@code hItem} helper to work with JSON values
         */
        @Transient
        protected final JsonHelper hItem;

        /**
         * {@code TeamRole} list of available team roles
         */
        public enum TeamRole {

            /**
             * {@code ADMIN} role
             *
             * @apiNote this role allows to manage the members of the team, so add or remove them, and also manage
             * collections and links shared (only personal), so add or remove them
             */
            ADMIN,

            /**
             * {@code VIEWER} role
             *
             * @apiNote this role allows to read the content shared in the team
             */
            VIEWER

        }

        /**
         * {@code owner} the {@link RefyUser} who the member is linked
         */
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

        /**
         * {@code role} the role of the member
         */
        @Enumerated(value = STRING)
        @Column(name = TEAM_ROLE_KEY)
        private final TeamRole role;

        /**
         * {@code sourceTeam} the team of the member
         */
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

        /**
         * Constructor to init the {@link RefyTeamMember} class <br>
         *
         * No-any params required
         * @apiNote empty constructor required
         */
        public RefyTeamMember() {
            this(new RefyUser(), null, null);
        }

        /**
         * Constructor to init the {@link RefyTeamMember} class
         *
         * @param owner: the {@link RefyUser} who the member is linked
         * @param role: the role of the member
         * @param sourceTeam: the team of the member
         *
         */
        public RefyTeamMember(RefyUser owner, TeamRole role, Team sourceTeam) {
            hItem = null;
            this.owner = owner;
            this.role = role;
            this.sourceTeam = sourceTeam;
        }

        /**
         * Constructor to init the {@link RefyTeamMember} class
         *
         * @param jRefyTeamMember: member details formatted as JSON
         */
        public RefyTeamMember(JSONObject jRefyTeamMember) {
            hItem = new JsonHelper(jRefyTeamMember);
            owner = RefyUser.getInstance(hItem.getJSONObjectSource());
            String sRole = hItem.getString(TEAM_ROLE_KEY);
            if(sRole != null)
                role = TeamRole.valueOf(sRole);
            else
                role = null;
            sourceTeam = null;
        }

        /**
         * Constructor to init the {@link RefyTeamMember} class
         *
         * @param member: member details as list
         *
         */
        public RefyTeamMember(List<String> member) {
            hItem = null;
            owner = new RefyUser(
                    member.get(0),
                    member.get(2),
                    member.get(3),
                    null,
                    member.get(1),
                    member.get(4)
            );
            role = null;
            sourceTeam = null;
        }

        /**
         * Method to get the identifier of the member <br>
         * No-any params required
         *
         * @return the identifier of the member as {@link String}
         */
        public String getId() {
            return owner.getId();
        }

        /**
         * Method to get the tag name of the member <br>
         * No-any params required
         *
         * @return the tag name of the member as {@link String}
         */
        @JsonGetter(TAG_NAME_KEY)
        public String getTagName() {
            return owner.getTagName();
        }

        /**
         * Method to get the name of the member <br>
         * No-any params required
         *
         * @return the name of the member as {@link String}
         */
        public String getName() {
            return owner.getName();
        }

        /**
         * Method to get the surname of the member <br>
         * No-any params required
         *
         * @return the surname of the member as {@link String}
         */
        public String getSurname() {
            return owner.getSurname();
        }

        /**
         * Method to get the complete name of the member <br>
         * No-any params required
         *
         * @return the complete name of the member as {@link String}
         */
        @JsonIgnore
        public String getCompleteName() {
            return owner.getCompleteName();
        }

        /**
         * Method to get the email of the member <br>
         * No-any params required
         *
         * @return the email of the member as {@link String}
         */
        public String getEmail() {
            return owner.getEmail();
        }

        /**
         * Method to get the profile pic of the member <br>
         * No-any params required
         *
         * @return the profile pic of the member as {@link String}
         */
        @JsonGetter(PROFILE_PIC_KEY)
        public String getProfilePic() {
            return owner.getProfilePic();
        }

        /**
         * Method to get {@link #role} instance <br>
         * No-any params required
         *
         * @return {@link #role} instance as {@link TeamRole}
         */
        @JsonGetter(TEAM_ROLE_KEY)
        public TeamRole getRole() {
            return role;
        }

        /**
         * Method to assemble and return an {@link ArrayList} of members
         *
         * @param jMembers: members list details formatted as JSON
         *
         * @return the members list as {@link ArrayList} of {@link RefyTeamMember}
         */
        @Returner
        public static ArrayList<RefyTeamMember> returnMembers(JSONArray jMembers) {
            ArrayList<RefyTeamMember> members = new ArrayList<>();
            if (jMembers == null)
                return members;
            for (int j = 0; j < jMembers.length(); j++)
                members.add(new RefyTeamMember(jMembers.getJSONObject(j)));
            return members;
        }

    }

}
