package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.equinox.environment.records.EquinoxUser;
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

@Entity
@Table(name = COLLECTIONS_KEY)
public class LinksCollection extends RefyItem implements RefyItem.ListScreenItem {

    public static final String COLLECTIONS_LINKS_TABLE = "collections_links";

    public static final String COLLECTIONS_KEY = "collections";

    public static final String COLLECTION_COLOR_KEY = "collection_color";

    public static final String COLLECTION_IDENTIFIER_KEY = "collection_id";

    @Column(name = COLLECTION_COLOR_KEY)
    private final String color;

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

    public LinksCollection() {
        this(null, null, null, null, null, null, null);
    }

    public LinksCollection(String id, RefyUser owner, String name, String color, String description, List<Team> teams,
                           List<RefyLink> links) {
        super(id, owner, name, description);
        this.color = color;
        this.teams = teams;
        this.links = links;
    }

    public LinksCollection(JSONObject jLinksCollection) {
        super(jLinksCollection);
        color = hItem.getString(COLLECTION_COLOR_KEY);
        links = returnLinks(hItem.getJSONArray(LINKS_KEY));
        teams = returnTeams(hItem.getJSONArray(TEAMS_KEY));
    }

    @JsonGetter(COLLECTION_COLOR_KEY)
    public String getColor() {
        return color;
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

    public List<Team> getTeams() {
        return teams;
    }

    @JsonIgnore
    public List<String> getTeamIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (Team team : teams)
            ids.add(team.getId());
        return ids;
    }

    public boolean hasTeams() {
        return !teams.isEmpty();
    }

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
