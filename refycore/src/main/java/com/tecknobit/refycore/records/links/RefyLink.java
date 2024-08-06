package com.tecknobit.refycore.records.links;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyItem;
import com.tecknobit.refycore.records.RefyUser;
import com.tecknobit.refycore.records.Team;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.RefyUser.LINKS_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

@Entity
@Table(name = LINKS_KEY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@AttributeOverride(
        name = IDENTIFIER_KEY,
        column = @Column(name = LINK_IDENTIFIER_KEY)
)
public class RefyLink extends RefyItem implements RefyItem.ListScreenItem {

    public static final String REFERENCE_LINK_KEY = "reference_link";

    public static final String LINK_IDENTIFIER_KEY = "link_id";

    @Column(name = REFERENCE_LINK_KEY)
    protected final String referenceLink;

    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = LINKS_KEY
    )
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    protected final List<Team> teams;

    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = LINKS_KEY
    )
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    protected final List<LinksCollection> collections;

    public RefyLink() {
        this(null, null, null, null, null, List.of(), List.of());
    }

    //TODO: TO REMOVE
    public RefyLink(String id, String title, String description, String referenceLink) {
        super(id,
                new RefyUser(
                        "GEGWEGWHWHG",
                        "Greg",
                        "Godzilla",
                        "greg@godzilla",
                        "https://media-assets.wired.it/photos/64f6faa946c2835bd21c9fd3/4:3/w_2880,h_2160,c_limit/ezgif-3-f91e25fbf3.jpg",
                        "@godzilla"
                ),
                title,
                description);
        this.referenceLink = referenceLink;
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of(
                new Team("id", "Ciao", new RefyUser("h"), "https://cdn.mos.cms.futurecdn.net/9UmWCbyxpKaEGXjwFG7dXo-1200-80.jpg", "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam.")//,
                //new Team("id2", "Ciao2")
        );
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of(
                //new LinksCollection("id", "Ciao")
        );
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
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of();
    }

    public String getReferenceLink() {
        return referenceLink;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

    @Override
    public boolean canBeUpdatedByUser(String loggedUserId) {
        return loggedUserId.equals(owner.getId()) || teams.isEmpty();
    }

}
