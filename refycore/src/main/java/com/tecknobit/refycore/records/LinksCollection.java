package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.equinox.environment.records.EquinoxUser;
import com.tecknobit.refycore.records.RefyItem.ListScreenItem;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyUser.LINKS_KEY;
import static com.tecknobit.refycore.records.RefyUser.TEAMS_KEY;
import static com.tecknobit.refycore.records.Team.returnTeams;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.returnLinks;

/**
 * The {@code LinksCollection} class is useful to represent a collection of links
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see RefyItem
 * @see ListScreenItem
 * @see RefyLink
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Entity
@Table(name = COLLECTIONS_KEY)
public class LinksCollection extends RefyItem implements ListScreenItem {

    /**
     * {@code COLLECTIONS_LINKS_TABLE} the key for the <b>"collections_links"</b> field
     */
    public static final String COLLECTIONS_LINKS_TABLE = "collections_links";

    /**
     * {@code COLLECTIONS_KEY} the key for the <b>"collections"</b> field
     */
    public static final String COLLECTIONS_KEY = "collections";

    /**
     * {@code COLLECTION_COLOR_KEY} the key for the <b>"collection_color"</b> field
     */
    public static final String COLLECTION_COLOR_KEY = "collection_color";

    /**
     * {@code COLLECTION_IDENTIFIER_KEY} the key for the <b>"collection_id"</b> field
     */
    public static final String COLLECTION_IDENTIFIER_KEY = "collection_id";

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
        this(null, null, null, null, null, null, null);
    }

    /**
     * Constructor to init the {@link RefyItem} class
     *
     * @param id: the identifier of the collection
     * @param owner: the owner of the collection
     * @param title: the title of the collection
     * @param description:the description of the collection
     * @param color:{@code color} the color of the collection
     * @param links:{@code links} the links contained by the collection
     * @param teams:{@code teams} the teams where the collection is shared
     *
     */
    public LinksCollection(String id, RefyUser owner, String title, String color, String description, List<Team> teams,
                           List<RefyLink> links) {
        super(id, owner, title, description);
        this.color = color;
        this.teams = teams;
        this.links = links;
    }

    /**
     * Constructor to init the {@link LinksCollection} class
     *
     * @param jLinksCollection: the json details of the collection as {@link JSONObject}
     *
     */
    public LinksCollection(JSONObject jLinksCollection) {
        super(jLinksCollection);
        color = hItem.getString(COLLECTION_COLOR_KEY);
        links = returnLinks(hItem.getJSONArray(LINKS_KEY));
        teams = returnTeams(hItem.getJSONArray(TEAMS_KEY));
    }

    /**
     * Method to get {@link #color} instance <br>
     * No-any params required
     *
     * @return {@link #color} instance as {@link String}
     */
    @JsonGetter(COLLECTION_COLOR_KEY)
    public String getColor() {
        return color;
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
     * Method to get {@link #teams} instance <br>
     * No-any params required
     *
     * @return {@link #teams} instance as {@link List} of {@link Team}
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * Method to get the ids of the {@link #teams} <br>
     * No-any params required
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

    /**
     * Method to get whether the collection is shared in any teams <br>
     * No-any params required
     *
     * @return whether the collection is shared in any teams as boolean
     */
    public boolean hasTeams() {
        return !teams.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeUpdatedByUser(String loggedUserId) {
        return loggedUserId.equals(owner.getId()) || teams.isEmpty();
    }

    /**
     * Method to assemble and return an {@link ArrayList} of collection
     *
     * @param jCollections: collection list details formatted as JSON
     *
     * @return the team list as {@link ArrayList} of {@link LinksCollection}
     */
    @Returner
    public static ArrayList<LinksCollection> returnCollections(JSONArray jCollections) {
        ArrayList<LinksCollection> collections = new ArrayList<>();
        if (jCollections == null)
            return collections;
        for (int j = 0; j < jCollections.length(); j++)
            collections.add(new LinksCollection(jCollections.getJSONObject(j)));
        return collections;
    }

    /**
     * Method to assemble and return a {@link LinksCollection} instance
     *
     * @param jCollection: collection formatted as JSON
     * @return the collection as {@link EquinoxUser}
     */
    @Returner
    public static LinksCollection getInstance(JSONObject jCollection) {
        if (jCollection != null)
            return new LinksCollection(jCollection);
        return null;
    }

}
