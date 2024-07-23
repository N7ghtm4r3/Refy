package com.tecknobit.refycore.records;

import org.json.JSONObject;

import java.util.List;

public class LinksCollection extends RefyItem {

    public static final String COLLECTION_COLOR_KEY = "collection_color";

    private final String color;

    private final String description;

    private final List<Team> teams;

    public LinksCollection() {
        this(null, null, null, null, List.of());
    }

    //TODO: TO REMOVE
    public LinksCollection(String id, String name, String color, String description) {
        super(id, name);
        this.color = color;
        this.description = description;
        this.teams = List.of(
                new Team("id", "Ciao"),
                new Team("id2", "Ciao2")
        );
    }

    public LinksCollection(String id, String name, String color, String description, List<Team> teams) {
        super(id, name);
        this.color = color;
        this.description = description;
        this.teams = teams;
    }

    public LinksCollection(JSONObject jLinksCollection) {
        super(jLinksCollection);
        color = hItem.getString(COLLECTION_COLOR_KEY);
        description = hItem.getString(DESCRIPTION_KEY);
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public List<Team> getTeams() {
        return teams;
    }

}
