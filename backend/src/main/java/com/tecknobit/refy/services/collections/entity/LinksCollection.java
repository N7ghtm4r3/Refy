package com.tecknobit.refy.services.collections.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.entities.RefyItem;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.users.entity.RefyUser;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code LinksCollection} class is useful to represent a collection of links
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see RefyItem
 * @see RefyLink
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Entity
@Table(name = COLLECTIONS_KEY)
public class LinksCollection extends RefyItem {

    /**
     * {@code color} the color of the collection
     */
    @Column(name = COLLECTION_COLOR_KEY)
    private final String color;

    /**
     * {@code links} the links contained by the collection
     */
    @ManyToMany(
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = COLLECTIONS_LINKS_TABLE,
            joinColumns = {@JoinColumn(name = COLLECTION_IDENTIFIER_KEY)},
            inverseJoinColumns = {@JoinColumn(name = LINK_IDENTIFIER_KEY)},
            uniqueConstraints = @UniqueConstraint(
                    columnNames = { COLLECTION_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY}
            )
    )
    @JsonIgnoreProperties({
            COLLECTIONS_KEY,
            TEAMS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<RefyLink> links;

    /**
     * {@code teams} the teams where the collection is shared
     */
    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = COLLECTIONS_KEY
    )
    @JsonIgnoreProperties({
            COLLECTIONS_KEY,
            LINKS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<Team> teams;

    /**
     * Constructor to init the {@link LinksCollection} class <br>
     *
     * No-any params required
     * @apiNote empty constructor required
     */
    public LinksCollection() {
        this(null, null, null, null, null, 0, null, null);
    }

    /**
     * Constructor to init the {@link RefyItem} class
     *
     * @param id The identifier of the collection
     * @param owner The owner of the collection
     * @param title The title of the collection
     * @param description The description of the collection
     * @param date The date when the item has been inserted in the system
     * @param color:{@code color} the color of the collection
     * @param links:{@code links} the links contained by the collection
     * @param teams:{@code teams} the teams where the collection is shared
     *
     */
    public LinksCollection(String id, RefyUser owner, String title, String color, String description, long date,
                           List<Team> teams, List<RefyLink> links) {
        super(id, owner, title, description, date);
        this.color = color;
        this.teams = teams;
        this.links = links;
    }

    /**
     * Method to get {@link #color} instance
     *
     * @return {@link #color} instance as {@link String}
     */
    @JsonGetter(COLLECTION_COLOR_KEY)
    public String getColor() {
        return color;
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
     * Method to get {@link #teams} instance
     *
     * @return {@link #teams} instance as {@link List} of {@link Team}
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * Method to get the ids of the {@link #teams}
     *
     * @return ids of the {@link #teams} as {@link List} of {@link String}
     */
    @JsonIgnore
    public List<String> getTeamIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (Team team : teams)
            ids.add(team.getId());
        return ids;
    }

    // TODO: 14/02/2025 TO COMMENT
    public boolean isUserAllowedToRemoveTeam(String userId, String teamId) {
        for (Team team : teams) {
            if (team.getId().equals(teamId) && team.getOwner().getId().equals(userId))
                return true;
        }
        return false;
    }

    /**
     * Method to get whether the collection is shared in any teams
     *
     * @return whether the collection is shared in any teams as boolean
     */
    // TODO: 14/02/2025 CHECK TO REMOVE
    public boolean hasTeams() {
        return !teams.isEmpty();
    }

}
