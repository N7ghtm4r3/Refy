package com.tecknobit.refy.services.users.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.annotations.MappingPurpose;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.entities.Team.RefyTeamMember;
import jakarta.persistence.*;

import java.util.List;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.OWNER_KEY;
import static com.tecknobit.refy.services.users.entities.UserSettings.DEFAULT_USER_SETTINGS;
import static com.tecknobit.refycore.ConstantsKt.*;
import static com.tecknobit.refycore.helpers.RefyInputsValidator.MAX_TAG_NAME_LENGTH;

/**
 * The {@code RefyUser} class is useful to represent a Refy's system user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see EquinoxUser
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
            mappedBy = OWNER_KEY,
            cascade = CascadeType.ALL
    )
    @Column(name = LINKS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<RefyLink> links;

    /**
     * {@code teams} the teams where the user is a member
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = OWNER_KEY,
            cascade = CascadeType.ALL
    )
    @Column(name = TEAMS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<Team> teams;

    /**
     * {@code collections} the collections of the user
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = OWNER_KEY,
            cascade = CascadeType.ALL
    )
    @Column(name = COLLECTIONS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<LinksCollection> collections;

    /**
     * {@code customLinks} the custom links of the user
     */
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = OWNER_KEY,
            cascade = CascadeType.ALL
    )
    @Column(name = CUSTOM_LINKS_KEY)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<CustomRefyLink> customLinks;

    /**
     * {@code member} the member of teams relationship
     */
    @MappingPurpose
    @JsonIgnore
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = OWNER_KEY,
            cascade = CascadeType.ALL
    )
    private List<RefyTeamMember> member;

    /**
     * {@code settings} The settings of the user
     *
     * @since 1.1.0
     */
    @OneToOne(
            mappedBy = OWNER_KEY
    )
    private final UserSettings settings;

    /**
     * Constructor used to init the {@link RefyUser} class 
     * @apiNote empty constructor required
     */
    @EmptyConstructor
    public RefyUser() {
        this(null, null, null, null, null, null, null, null, null, List.of(), List.of(), List.of(), List.of(), null);
    }

    /**
     * Constructor used to init the {@link RefyUser} class
     *
     * @param id Identifier of the user
     * @param name The name of the user
     * @param surname The surname of the user
     * @param email The email of the user
     * @param profilePic The profile pic of the user
     * @param tagName The name of the user
     */
    public RefyUser(String id, String name, String surname, String email, String profilePic, String tagName) {
        super(id, null, name, surname, email, null, profilePic, null);
        this.tagName = tagName;
        this.links = null;
        this.teams = null;
        this.collections = null;
        this.customLinks = null;
        this.settings = null;
    }

    /**
     * Constructor used to init the {@link RefyUser} class
     *
     * @param id The identifier of the user
     * @param token The token which the user is allowed to operate on server
     * @param name The name of the user
     * @param surname The surname of the user
     * @param email The email of the user
     * @param password The password of the user
     * @param profilePic The profile pic of the user
     * @param language The language of the user
     * @param tagName The name of the user
     * @param links The links of the user
     * @param teams The teams where the user is a member
     * @param collections The collections of the user
     * @param customLinks The custom links of the user
     * @param settings The settings of the user
     */
    public RefyUser(String id, String token, String name, String surname, String email, String password, String profilePic,
                    String language, String tagName, List<RefyLink> links, List<Team> teams,
                    List<LinksCollection> collections, List<CustomRefyLink> customLinks, UserSettings settings) {
        super(id, token, name, surname, email, password, profilePic, language);
        this.tagName = tagName;
        this.links = links;
        this.teams = teams;
        this.collections = collections;
        this.customLinks = customLinks;
        this.settings = settings;
    }

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
     * Method to get {@link #links} instance
     *
     * @return {@link #links} instance as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getLinks() {
        return links;
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
     * Method to get {@link #settings} instance
     *
     * @return {@link #settings} instance as {@link UserSettings}
     *
     * @since 1.1.0
     */
    @JsonGetter(SETTINGS_KEY)
    public UserSettings getSettings() {
        if(settings == null)
            return DEFAULT_USER_SETTINGS;
        return settings;
    }

}
