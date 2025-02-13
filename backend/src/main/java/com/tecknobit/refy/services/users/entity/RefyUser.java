package com.tecknobit.refy.services.users.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.entities.Team.RefyTeamMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.util.List;

import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.MAX_TAG_NAME_LENGTH;

/**
 * The {@code RefyUser} class is useful to represent a Refy's system user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see EquinoxUser
 *
 */
@Entity
public class RefyUser extends EquinoxUser {

    /**
     * {@code tagName} the tag name of the user
     */
    @Column(
            name = TAG_NAME_KEY,
            length = MAX_TAG_NAME_LENGTH,
            unique = true
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
        this(null, null, null, null, null, null, null, null, null,
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
     * @param password The password of the user
     * @param language The language of the user
     * @param tagName:     the name of the user
     * @param links The links of the user
     * @param teams The teams where the user is a member
     * @param customLinks The custom links of the user
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
        super(id, null, name, surname, email, null, profilePic, null);
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
     * @param password The password of the user
     * @param language The language of the user
     * @param tagName:     the name of the user
     * @param links The links of the user
     * @param teams The teams where the user is a member
     * @param customLinks The custom links of the user
     */
    public RefyUser(String id, String token, String name, String surname, String email, String password, String profilePic,
                    String language, String tagName, List<RefyLink> links, List<Team> teams,
                    List<LinksCollection> collections, List<CustomRefyLink> customLinks) {
        super(id, token, name, surname, email, password, profilePic, language);
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
    // TODO: 03/02/2025 CHECK TO REMOVE
    /*public RefyUser(JSONObject jRefyUser) {
        super(jRefyUser);
        tagName = hItem.getString(TAG_NAME_KEY);
        links = returnLinks(hItem.getJSONArray(LINKS_KEY));
        teams = returnTeams(hItem.getJSONArray(TEAMS_KEY));
        collections = returnCollections(hItem.getJSONArray(COLLECTIONS_KEY));
        customLinks = returnCustomLinks(hItem.getJSONArray(CUSTOM_LINKS_KEY));
    }*/

    /**
     * Method to get {@link #tagName} instance
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
     * @param links The links of the user
     *
     */
    public void setLinks(List<RefyLink> links) {
        this.links = links;
    }

    /**
     * Method to get {@link #links} instance
     *
     * @return {@link #links} instance as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getLinks() {
        return links;
    }

    /**
     * Method to set {@link #teams} instance <br>
     *
     * @param teams The teams where the user is a member
     *
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    /**
     * Method to get {@link #teams} instance
     *
     * @return {@link #teams} instance as {@link List} of {@link Team}
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * Method to set {@link #collections} instance <br>
     *
     * @param collections The collections of the user
     *
     */
    public void setCollections(List<LinksCollection> collections) {
        this.collections = collections;
    }

    /**
     * Method to get {@link #collections} instance
     *
     * @return {@link #collections} instance as {@link List} of {@link LinksCollection}
     */
    public List<LinksCollection> getCollections() {
        return collections;
    }

    /**
     * Method to get {@link #customLinks} instance
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
     * @param customLinks The custom links of the user
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
    // TODO: 03/02/2025 CHECK TO REMOVE
    /*@Returner
    public static RefyUser getInstance(JSONObject jUser) {
        if (jUser != null)
            return new RefyUser(jUser);
        return null;
    }*/

}
