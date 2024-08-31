package com.tecknobit.refycore.records.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyItem;
import com.tecknobit.refycore.records.RefyItem.ListScreenItem;
import com.tecknobit.refycore.records.RefyUser;
import com.tecknobit.refycore.records.Team;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.LinksCollection.returnCollections;
import static com.tecknobit.refycore.records.RefyUser.LINKS_KEY;
import static com.tecknobit.refycore.records.RefyUser.TEAMS_KEY;
import static com.tecknobit.refycore.records.Team.returnTeams;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_KEY;

/**
 * The {@code RefyLink} class is useful to represent a Refy's link
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see RefyItem
 * @see ListScreenItem
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Entity
@Table(name = LINKS_KEY)
@AttributeOverride(
        name = IDENTIFIER_KEY,
        column = @Column(name = LINK_IDENTIFIER_KEY)
)
@DiscriminatorValue(LINK_KEY)
public class RefyLink extends RefyItem implements ListScreenItem {

    /**
     * {@code REFERENCE_LINK_KEY} the key for the <b>"reference_link"</b> field
     */
    public static final String REFERENCE_LINK_KEY = "reference_link";

    /**
     * {@code LINK_KEY} the key for the <b>"link"</b> field
     */
    public static final String LINK_KEY = "link";

    /**
     * {@code LINK_IDENTIFIER_KEY} the key for the <b>"link_id"</b> field
     */
    public static final String LINK_IDENTIFIER_KEY = "link_id";

    /**
     * {@code referenceLink} the link reference value
     */
    @Column(name = REFERENCE_LINK_KEY)
    protected final String referenceLink;

    /**
     * {@code teams} the teams where the link is shared
     */
    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = LINKS_KEY
    )
    @JsonIgnoreProperties({
            LINKS_KEY,
            COLLECTIONS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    protected final List<Team> teams;

    /**
     * {@code collections} the collections where the link is shared
     */
    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = LINKS_KEY
    )
    @JsonIgnoreProperties({
            LINKS_KEY,
            TEAMS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    protected final List<LinksCollection> collections;

    /**
     * Constructor to init the {@link RefyLink} class <br>
     *
     * No-any params required
     * @apiNote empty constructor required
     */
    public RefyLink() {
        this(null, null, null, null, null, List.of(), List.of());
    }

    /**
     * Constructor to init the {@link RefyLink} class
     *
     * @param id: the identifier of the link
     * @param owner: the owner of the link
     * @param title: the title of the link
     * @param description:the description of the link
     * @param referenceLink: the link reference value
     * @param teams: the teams where the link is shared
     * @param collections: the collections where the link is shared
     *
     */
    public RefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                    List<Team> teams, List<LinksCollection> collections) {
        super(id, owner, title, description);
        this.referenceLink = referenceLink;
        this.teams = teams;
        this.collections = collections;
    }

    /**
     * Constructor to init the {@link RefyLink} class
     *
     * @param jRefyLink: the json details of the link as {@link JSONObject}
     *
     */
    public RefyLink(JSONObject jRefyLink) {
        super(jRefyLink);
        referenceLink = hItem.getString(REFERENCE_LINK_KEY);
        teams = returnTeams(hItem.getJSONArray(TEAMS_KEY));
        collections = returnCollections(hItem.getJSONArray(COLLECTIONS_KEY));
    }

    /**
     * Method to get {@link #referenceLink} instance <br>
     * No-any params required
     *
     * @return {@link #referenceLink} instance as {@link String}
     */
    @JsonGetter(REFERENCE_LINK_KEY)
    public String getReferenceLink() {
        return referenceLink;
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
     * {@inheritDoc}
     */
    @Override
    public boolean canBeUpdatedByUser(String loggedUserId) {
        return loggedUserId.equals(owner.getId()) || teams.isEmpty();
    }

    /**
     * Method to assemble and return an {@link ArrayList} of links
     *
     * @param jLinks : links list details formatted as JSON
     * @return the link list as {@link ArrayList} of {@link RefyLink}
     */
    @Returner
    public static ArrayList<RefyLink> returnLinks(JSONArray jLinks) {
        ArrayList<RefyLink> links = new ArrayList<>();
        if (jLinks == null)
            return links;
        for (int j = 0; j < jLinks.length(); j++)
            links.add(new RefyLink(jLinks.getJSONObject(j)));
        return links;
    }

}
