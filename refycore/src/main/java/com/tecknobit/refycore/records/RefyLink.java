package com.tecknobit.refycore.records;

import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.refycore.records.RefyItem.DESCRIPTION_KEY;

public class RefyLink extends EquinoxItem {

    public static final String TITLE_KEY = "title_key";

    public static final String THUMBNAIL_KEY = "thumbnail_key";

    public static final String REFERENCE_LINK_KEY = "reference_link";

    private final RefyUser owner;

    private final String title;

    private final String description;

    private final String referenceLink;

    private final List<Team> teams;

    private final List<LinksCollection> collections;

    public RefyLink() {
        this(null, null, null, null, null, List.of(), List.of());
    }

    //TODO: TO REMOVE
    public RefyLink(String id, String title, String description, String referenceLink) {
        super(id);
        this.owner = new RefyUser(
                "GEGWEGWHWHG",
                "Greg",
                "Godzilla",
                "greg@godzilla",
                "https://media-assets.wired.it/photos/64f6faa946c2835bd21c9fd3/4:3/w_2880,h_2160,c_limit/ezgif-3-f91e25fbf3.jpg",
                "@godzilla"
        );
        this.title = title;
        this.description = description;
        this.referenceLink = referenceLink;
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of(
                new Team("id", "Ciao", "https://cdn.mos.cms.futurecdn.net/9UmWCbyxpKaEGXjwFG7dXo-1200-80.jpg")//,
                //new Team("id2", "Ciao2")
        );
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of(
                //new LinksCollection("id", "Ciao")
        );
    }

    public RefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                    List<Team> teams, List<LinksCollection> collections) {
        super(id);
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.referenceLink = referenceLink;
        this.teams = teams;
        this.collections = collections;
    }

    public RefyLink(JSONObject jRefyLink) {
        super(jRefyLink);
        //TODO: TO LOAD CORRECTLY
        owner = null;
        title = hItem.getString(TITLE_KEY);
        description = hItem.getString(DESCRIPTION_KEY);
        referenceLink = hItem.getString(REFERENCE_LINK_KEY);
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of();
    }

    public RefyUser getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

}
