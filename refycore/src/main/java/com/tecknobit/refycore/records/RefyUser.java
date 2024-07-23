package com.tecknobit.refycore.records;

import com.tecknobit.equinox.environment.records.EquinoxUser;
import org.json.JSONObject;

import java.util.List;

public class RefyUser extends EquinoxUser {

    private final List<Team> teams;

    private final List<LinksCollection> collections;

    public RefyUser() {
        super();
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

    public RefyUser(String id, String token, String name, String surname, String email, String password, String language,
                    List<Team> teams, List<LinksCollection> collections) {
        super(id, token, name, surname, email, password, language);
        this.teams = teams;
        this.collections = collections;
    }

    public RefyUser(String id, String token, String name, String surname, String email, String password, String profilePic,
                    String language, ApplicationTheme theme, List<Team> teams, List<LinksCollection> collections) {
        super(id, token, name, surname, email, password, profilePic, language, theme);
        this.teams = teams;
        this.collections = collections;
    }

    public RefyUser(JSONObject jRefyUser) {
        super(jRefyUser);
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of();
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

}
