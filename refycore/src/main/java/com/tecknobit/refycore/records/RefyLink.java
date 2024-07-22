package com.tecknobit.refycore.records;

import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

import java.util.List;

public class RefyLink extends EquinoxItem {

    public static final String TITLE_KEY = "title_key";

    public static final String DESCRIPTION_KEY = "description_key";

    public static final String THUMBNAIL_KEY = "thumbnail_key";

    public static final String REFERENCE_LINK_KEY = "reference_link";

    private final String title;

    private final String description;

    private final String referenceLink;

    private final List<Team> teams;

    private final List<LinksCollection> collections;

    public RefyLink() {
        this(null, null, null, null, List.of(), List.of());
    }

    //TODO: TO REMOVE
    public RefyLink(String id, String title, String description, String referenceLink) {
        super(id);
        this.title = title;
        this.description = description;
        this.referenceLink = referenceLink;
        this.teams = List.of();
        this.collections = List.of();
    }

    public RefyLink(String id, String title, String description, String referenceLink,
                    List<Team> teams, List<LinksCollection> collections) {
        super(id);
        this.title = title;
        this.description = description;
        this.referenceLink = referenceLink;
        this.teams = teams;
        this.collections = collections;
    }

    public RefyLink(JSONObject jRefyLink) {
        super(jRefyLink);
        title = hItem.getString(TITLE_KEY);
        description = hItem.getString(DESCRIPTION_KEY);
        referenceLink = hItem.getString(REFERENCE_LINK_KEY);
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of();
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
