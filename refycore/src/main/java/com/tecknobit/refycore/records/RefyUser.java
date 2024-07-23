package com.tecknobit.refycore.records;

import com.tecknobit.equinox.environment.records.EquinoxUser;
import org.json.JSONObject;

import java.util.List;

public class RefyUser extends EquinoxUser {

    public static final String TAG_NAME_KEY = "tag_name";

    private final String tagName;

    private final List<Team> teams;

    private final List<LinksCollection> collections;

    public RefyUser() {
        super();
        tagName = "@tagName";
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of(
                new Team("id", "Ciao"),
                new Team("id2", "Ciao2")
        );
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of(
                new LinksCollection("#FFFFFF","id", "Ciao", "ga")
        );
    }

    public RefyUser(String id, String token, String name, String surname, String email, String password, String language, String tagName,
                    List<Team> teams, List<LinksCollection> collections) {
        super(id, token, name, surname, email, password, language);
        this.tagName = tagName;
        this.teams = teams;
        this.collections = collections;
    }

    public RefyUser(String id, String name, String surname, String email, String profilePic, String tagName) {
        super(id, null, name, surname, email, null, profilePic, null, null);
        this.tagName = tagName;
        this.teams = null;
        this.collections = null;
    }

    public RefyUser(String id, String token, String name, String surname, String email, String password, String profilePic,
                    String language, ApplicationTheme theme, String tagName, List<Team> teams, List<LinksCollection> collections) {
        super(id, token, name, surname, email, password, profilePic, language, theme);
        this.tagName = tagName;
        this.teams = teams;
        this.collections = collections;
    }

    public RefyUser(JSONObject jRefyUser) {
        super(jRefyUser);
        tagName = hItem.getString(TAG_NAME_KEY);
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of();
    }

    public String getTagName() {
        return tagName;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

}
