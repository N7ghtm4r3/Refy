package com.tecknobit.refy.services.teams.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.entities.RefyItem;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refycore.enums.TeamRole;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.PROFILE_PIC_KEY;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.enums.TeamRole.ADMIN;
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
        this(null, null, null, null, null, 0, List.of(), List.of(), List.of());
    }

    /**
     * Constructor to init the {@link RefyItem} class
     *
     * @param id The identifier of the team
     * @param title The title of the team
     * @param author The author of the team
     * @param logoPic The logo picture of the team
     * @param description The description of the team
     * @param date The date when the item has been inserted in the system
     * @param members The members of the team
     * @param links The links shared with the team
     * @param collections The collections shared with the team
     *
     */
    public Team(String id, String title, RefyUser author, String logoPic, String description, long date,
                List<RefyTeamMember> members, List<RefyLink> links, List<LinksCollection> collections) {
        super(id, author, title, description, date);
        this.logoPic = logoPic;
        this.members = members;
        this.links = links;
        this.collections = collections;
    }

    /**
     * Constructor to init the {@link Team} class
     *
     * @param jTeam Team details formatted as JSON
     */
    // TODO: 03/02/2025 CHECK TO REMOVE
    /*public Team(JSONObject jTeam) {
        super(jTeam);
        logoPic = hItem.getString(LOGO_PIC_KEY);
        members = returnMembers(hItem.getJSONArray(MEMBERS_KEY));
        links = returnLinks(hItem.getJSONArray(LINKS_KEY));
        collections = returnCollections(hItem.getJSONArray(COLLECTIONS_KEY));
    }*/

    /**
     * Method to get {@link #logoPic} instance
     *
     * @return {@link #logoPic} instance as {@link String}
     */
    @JsonGetter(LOGO_PIC_KEY)
    public String getLogoPic() {
        return logoPic;
    }

    /**
     * Method to get {@link #members} instance
     *
     * @return {@link #members} instance as {@link List} of {@link RefyTeamMember}
     */
    public List<RefyTeamMember> getMembers() {
        return members;
    }

    /**
     * Method to get whether the team has members apart the author
     *
     * @return whether the team has members apart the author as boolean
     */
    public boolean hasMembers() {
        return members.size() > 1;
    }

    /**
     * Method to get whether the team has admins
     *
     * @param exceptId The identifier to not check, admin who is leaving the team
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
     * Method to get the first member who is a viewer
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
     * Method to get the ids of the {@link #members}
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
     * @param memberId The member to check if is in the team
     *
     * @return whether a member is in the team as boolean
     */
    public boolean hasMember(String memberId) {
        if(membersMapping == null)
            getMembersIds();
        return membersMapping.contains(memberId);
    }

    /**
     * Method to get {@link #links} instance
     *
     * @return {@link #links} instance as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getLinks() {
        return links;
    }

    /**
     * Method to get the ids of the {@link #links}
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
     * Method to get {@link #collections} instance
     *
     * @return {@link #collections} instance as {@link List} of {@link LinksCollection}
     */
    public List<LinksCollection> getCollections() {
        return collections;
    }

    /**
     * Method to get the ids of the {@link #collections}
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
     * @param memberId The member to check if is an admin
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
     * @param memberId The member to check if the team author
     *
     * @return whether a member is the team author as boolean
     */
    public boolean isTheAuthor(String memberId) {
        return memberId.equals(owner.getId());
    }

    /**
     * Method to assemble and return an {@link ArrayList} of teams
     *
     * @param jTeams Teams list details formatted as JSON
     *
     * @return the team list as {@link ArrayList} of {@link Team}
     */
    // TODO: 03/02/2025 CHECK TO REMOVE
    /*@Returner
    public static ArrayList<Team> returnTeams(JSONArray jTeams) {
        ArrayList<Team> teams = new ArrayList<>();
        if (jTeams == null)
            return teams;
        for (int j = 0; j < jTeams.length(); j++)
            teams.add(new Team(jTeams.getJSONObject(j)));
        return teams;
    }*/

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
         * {@code hItem} helper to work with JSON values
         */
        @Transient
        protected final JsonHelper hItem;

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
         * @param owner The {@link RefyUser} who the member is linked
         * @param role The role of the member
         * @param sourceTeam The team of the member
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
        // TODO: 03/02/2025 CHECK TO REMOVE
        /*public RefyTeamMember(JSONObject jRefyTeamMember) {
            hItem = new JsonHelper(jRefyTeamMember);
            owner = RefyUser.getInstance(hItem.getJSONObjectSource());
            String sRole = hItem.getString(TEAM_ROLE_KEY);
            if(sRole != null)
                role = TeamRole.valueOf(sRole);
            else
                role = null;
            sourceTeam = null;
        }*/

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
        // TODO: 03/02/2025 CHECK TO REMOVE
        /*@Returner
        public static ArrayList<RefyTeamMember> returnMembers(JSONArray jMembers) {
            ArrayList<RefyTeamMember> members = new ArrayList<>();
            if (jMembers == null)
                return members;
            for (int j = 0; j < jMembers.length(); j++)
                members.add(new RefyTeamMember(jMembers.getJSONObject(j)));
            return members;
        }*/

    }

}
