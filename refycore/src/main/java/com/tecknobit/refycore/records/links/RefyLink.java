package com.tecknobit.refycore.records.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyItem;
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

@Entity
@Table(name = LINKS_KEY)
@AttributeOverride(
        name = IDENTIFIER_KEY,
        column = @Column(name = LINK_IDENTIFIER_KEY)
)
@DiscriminatorValue(LINK_KEY)
public class RefyLink extends RefyItem implements RefyItem.ListScreenItem {

    public static final String REFERENCE_LINK_KEY = "reference_link";

    public static final String LINK_KEY = "link";

    public static final String LINK_IDENTIFIER_KEY = "link_id";

    @Column(name = REFERENCE_LINK_KEY)
    protected final String referenceLink;

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

    public RefyLink() {
        this(null, null, null, null, null, List.of(), List.of());
    }

    public RefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                    List<Team> teams, List<LinksCollection> collections) {
        super(id, owner, title, description);
        this.referenceLink = referenceLink;
        this.teams = teams;
        this.collections = collections;
    }

    public RefyLink(JSONObject jRefyLink) {
        super(jRefyLink);
        referenceLink = hItem.getString(REFERENCE_LINK_KEY);
        teams = returnTeams(hItem.getJSONArray(TEAMS_KEY));
        collections = returnCollections(hItem.getJSONArray(COLLECTIONS_KEY));
    }

    @JsonGetter(REFERENCE_LINK_KEY)
    public String getReferenceLink() {
        return referenceLink;
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
