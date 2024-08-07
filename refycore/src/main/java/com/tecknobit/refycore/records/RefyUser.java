package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinox.environment.records.EquinoxUser;
import com.tecknobit.refycore.records.Team.RefyTeamMember;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;

@Entity
@Table(name = USERS_KEY)
public class RefyUser extends EquinoxUser {

    public static final String USER_IDENTIFIER_KEY = "user_id";

    public static final String TAG_NAME_KEY = "tag_name";

    public static final String LINKS_KEY = "links";

    public static final String TEAMS_KEY = "teams";

    public static final String CUSTOM_LINKS_KEY = "custom_links";

    @Column(
            name = TAG_NAME_KEY,
            columnDefinition = "VARCHAR(15) UNIQUE NOT NULL"
    )
    private final String tagName;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = OWNER_KEY
    )
    @Column(name = LINKS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<RefyLink> links;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = OWNER_KEY
    )
    @Column(name = TEAMS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<Team> teams;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = OWNER_KEY
    )
    @Column(name = COLLECTIONS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<LinksCollection> collections;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = OWNER_KEY
    )
    @Column(name = CUSTOM_LINKS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<CustomRefyLink> customLinks;

    @JsonIgnore
    @OneToOne(
            fetch = FetchType.LAZY,
            mappedBy = OWNER_KEY
    )
    private RefyTeamMember member;

    public RefyUser() {
        super();
        tagName = "@tagName";
        this.links = List.of();
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of(
                new Team("id", "Ciao", this,
                        "https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg",
                        "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam."
                        ),
                new Team("id2", "Ciao2", this, "https://cdn.mos.cms.futurecdn.net/9UmWCbyxpKaEGXjwFG7dXo-1200-80.jpg",
                        "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam.")
        );
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of(
                new LinksCollection("#gegw", this, "id", "#FFFFFF",
                        "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam."
                        )
        );
    }

    //TODO: TO REMOVE
    public RefyUser(String id) {
        super(id, null, "Bello", "Utente", "Prova@gmail.com", "12345678", "https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg", "en",
                ApplicationTheme.Auto);
        tagName = "@tagName";
        this.links = List.of();
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of(
                new Team("id", "Ciao", this,
                        "https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg",
                        "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam."
                ),
                new Team("id2", "Ciao2", this, "https://cdn.mos.cms.futurecdn.net/9UmWCbyxpKaEGXjwFG7dXo-1200-80.jpg",
                        "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam.")
        );
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of(
                new LinksCollection("#gegw", this, "id", "#FFFFFF",
                        "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam."
                )
        );
    }

    public RefyUser(String id, String token, String name, String surname, String email, String password, String language,
                    String tagName, List<RefyLink> links, List<Team> teams, List<LinksCollection> collections,
                    List<CustomRefyLink> customLinks) {
        super(id, token, name, surname, email, password, language);
        this.tagName = tagName;
        this.links = links;
        this.teams = teams;
        this.collections = collections;
        this.customLinks = customLinks;
    }

    public RefyUser(String id, String name, String surname, String email, String profilePic, String tagName) {
        super(id, null, name, surname, email, null, profilePic, null, null);
        this.tagName = tagName;
        this.links = null;
        this.teams = null;
        this.collections = null;
    }

    public RefyUser(String id, String token, String name, String surname, String email, String password, String profilePic,
                    String language, ApplicationTheme theme, String tagName, List<RefyLink> links, List<Team> teams,
                    List<LinksCollection> collections, List<CustomRefyLink> customLinks) {
        super(id, token, name, surname, email, password, profilePic, language, theme);
        this.tagName = tagName;
        this.links = links;
        this.teams = teams;
        this.collections = collections;
        this.customLinks = customLinks;
    }

    public RefyUser(JSONObject jRefyUser) {
        super(jRefyUser);
        tagName = hItem.getString(TAG_NAME_KEY);
        //TODO: TO LOAD CORRECTLY
        this.links = List.of();
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of();
        //TODO: TO LOAD CORRECTLY
        this.customLinks = List.of();
    }

    public String getTagName() {
        return tagName;
    }

    public void setLinks(List<RefyLink> links) {
        this.links = links;
    }

    public List<RefyLink> getLinks() {
        return links;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setCollections(List<LinksCollection> collections) {
        this.collections = collections;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

    @JsonGetter(CUSTOM_LINKS_KEY)
    public List<CustomRefyLink> getCustomLinks() {
        return customLinks;
    }

    public void setCustomLinks(List<CustomRefyLink> customLinks) {
        this.customLinks = customLinks;
    }

}
