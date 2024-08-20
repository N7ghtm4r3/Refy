package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.equinox.environment.records.EquinoxUser;
import com.tecknobit.refycore.records.Team.RefyTeamMember;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.LinksCollection.returnCollections;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.Team.returnTeams;
import static com.tecknobit.refycore.records.links.CustomRefyLink.returnCustomLinks;
import static com.tecknobit.refycore.records.links.RefyLink.returnLinks;

/**
 * The {@code RefyUser} class is useful to represent a Refy's system user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see EquinoxUser
 *
 */
@Entity
@Table(name = USERS_KEY)
public class RefyUser extends EquinoxUser {

    /**
     * {@code USER_IDENTIFIER_KEY} the key for the <b>"user_id"</b> field
     */
    public static final String USER_IDENTIFIER_KEY = "user_id";

    /**
     * {@code TAG_NAME_KEY} the key for the <b>"tag_name"</b> field
     */
    public static final String TAG_NAME_KEY = "tag_name";

    /**
     * {@code LINKS_KEY} the key for the <b>"links"</b> field
     */
    public static final String LINKS_KEY = "links";

    /**
     * {@code TEAMS_KEY} the key for the <b>"teams"</b> field
     */
    public static final String TEAMS_KEY = "teams";

    /**
     * {@code CUSTOM_LINKS_KEY} the key for the <b>"custom_links"</b> field
     */
    public static final String CUSTOM_LINKS_KEY = "custom_links";

    /**
     * {@code tagName} the tag name of the user
     */
    @Column(
            name = TAG_NAME_KEY,
            columnDefinition = "VARCHAR(15) UNIQUE NOT NULL"
    )
    private final String tagName;

    /**
     * {@code links} the links of the user
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = OWNER_KEY
    )
    @Column(name = LINKS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<RefyLink> links;

    /**
     * {@code teams} the teams where the user is a member
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = OWNER_KEY
    )
    @Column(name = TEAMS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<Team> teams;

    /**
     * {@code collections} the collections of the user
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = OWNER_KEY
    )
    @Column(name = COLLECTIONS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<LinksCollection> collections;

    /**
     * {@code customLinks} the custom links of the user
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = OWNER_KEY
    )
    @Column(name = CUSTOM_LINKS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private List<CustomRefyLink> customLinks;

    /**
     * {@code member} the member of teams relationship
     */
    @JsonIgnore
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = OWNER_KEY
    )
    private List<RefyTeamMember> member;

    /**
     * Constructor to init the {@link RefyUser} class <br>
     *
     * No-any params required
     * @apiNote empty constructor required
     */
    public RefyUser() {
        this(null, null, null, null, null, null, null, null, null, null,
                List.of(), List.of(), List.of(), List.of());
    }

    /**
     * Constructor to init the {@link RefyUser} class
     *
     * @param id:       identifier of the user
     * @param token:    the token which the user is allowed to operate on server
     * @param name:     the name of the user
     * @param surname:  the surname of the user
     * @param email:    the email of the user
     * @param password: the password of the user
     * @param language: the language of the user
     * @param tagName:     the name of the user
     * @param links: the links of the user
     * @param teams: the teams where the user is a member
     * @param customLinks: the custom links of the user
     */
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

    /**
     * Constructor to init the {@link RefyUser} class
     *
     * @param id:       identifier of the user
     * @param name:     the name of the user
     * @param surname:  the surname of the user
     * @param email:    the email of the user
     * @param tagName:     the name of the user
     */
    public RefyUser(String id, String name, String surname, String email, String profilePic, String tagName) {
        super(id, null, name, surname, email, null, profilePic, null, null);
        this.tagName = tagName;
        this.links = null;
        this.teams = null;
        this.collections = null;
    }

    /**
     * Constructor to init the {@link RefyUser} class
     *
     * @param id:       identifier of the user
     * @param token:    the token which the user is allowed to operate on server
     * @param name:     the name of the user
     * @param surname:  the surname of the user
     * @param email:    the email of the user
     * @param password: the password of the user
     * @param language: the language of the user
     * @param theme:      the theme of the user
     * @param tagName:     the name of the user
     * @param links: the links of the user
     * @param teams: the teams where the user is a member
     * @param customLinks: the custom links of the user
     */
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

    /**
     * Constructor to init the {@link RefyUser} class
     *
     * @param jRefyUser: user details formatted as JSON
     */
    public RefyUser(JSONObject jRefyUser) {
        super(jRefyUser);
        tagName = hItem.getString(TAG_NAME_KEY);
        links = returnLinks(hItem.getJSONArray(LINKS_KEY));
        teams = returnTeams(hItem.getJSONArray(TEAMS_KEY));
        collections = returnCollections(hItem.getJSONArray(COLLECTIONS_KEY));
        customLinks = returnCustomLinks(hItem.getJSONArray(CUSTOM_LINKS_KEY));
    }

    /**
     * Method to get {@link #tagName} instance <br>
     * No-any params required
     *
     * @return {@link #tagName} instance as {@link String}
     */
    @JsonGetter(TAG_NAME_KEY)
    public String getTagName() {
        return tagName;
    }

    /**
     * Method to set {@link #links} instance <br>
     *
     * @param links: the links of the user
     *
     */
    public void setLinks(List<RefyLink> links) {
        this.links = links;
    }

    /**
     * Method to get {@link #links} instance <br>
     * No-any params required
     *
     * @return {@link #links} instance as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getLinks() {
        return links;
    }

    /**
     * Method to set {@link #teams} instance <br>
     *
     * @param teams: the teams where the user is a member
     *
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
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
     * Method to set {@link #collections} instance <br>
     *
     * @param collections: the collections of the user
     *
     */
    public void setCollections(List<LinksCollection> collections) {
        this.collections = collections;
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
     * Method to get {@link #customLinks} instance <br>
     * No-any params required
     *
     * @return {@link #customLinks} instance as {@link List} of {@link CustomRefyLink}
     */
    @JsonGetter(CUSTOM_LINKS_KEY)
    public List<CustomRefyLink> getCustomLinks() {
        return customLinks;
    }

    /**
     * Method to set {@link #customLinks} instance <br>
     *
     * @param customLinks: the custom links of the user
     *
     */
    public void setCustomLinks(List<CustomRefyLink> customLinks) {
        this.customLinks = customLinks;
    }

    /**
     * Method to assemble and return a {@link EquinoxUser} instance
     *
     * @param jUser: user details formatted as JSON
     * @return the user instance as {@link EquinoxUser}
     */
    @Returner
    public static RefyUser getInstance(JSONObject jUser) {
        if (jUser != null)
            return new RefyUser(jUser);
        return null;
    }

}
