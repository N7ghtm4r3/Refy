package com.tecknobit.refycore.records;

import org.json.JSONObject;

import java.util.List;

public class LinksCollection extends RefyItem {

    public static final String COLLECTION_COLOR_KEY = "collection_color";

    private final String color;

    private final List<RefyLink> links;

    private final List<Team> teams;

    public LinksCollection() {
        this(null, null, null, null, null);
    }

    //TODO: TO REMOVE
    public LinksCollection(String id, RefyUser owner, String name, String color, String description) {
        super(id, owner, name, description);
        this.color = color;
        this.links = List.of();
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
    }

    //TODO: TO REMOVE
    public LinksCollection(String id, RefyUser owner, String name, String color, String description, List<Team> teams) {
        super(id, owner, name, description);
        this.color = color;
        this.links = List.of();
        //TODO: TO LOAD CORRECTLY
        this.teams = teams;
    }

    public LinksCollection(String id, RefyUser owner, String name, String color, String description, List<Team> teams, List<RefyLink> links) {
        super(id, owner, name, description);
        this.color = color;
        this.teams = teams;
        this.links = links;
    }

    public LinksCollection(JSONObject jLinksCollection) {
        super(jLinksCollection);
        color = hItem.getString(COLLECTION_COLOR_KEY);
        //TODO: TO LOAD CORRECTLY
        this.links = List.of();
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
    }

    public String getColor() {
        return color;
    }

    public List<RefyLink> getLinks() {
        return links;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public boolean hasTeams() {
        return !teams.isEmpty();
    }

}
