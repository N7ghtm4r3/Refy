package com.tecknobit.refycore.helpers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinox.environment.records.EquinoxLocalUser;
import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyItem;
import com.tecknobit.refycore.records.RefyUser;
import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import com.tecknobit.refycore.records.links.RefyLink;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.refycore.records.RefyUser.TAG_NAME_KEY;

/**
 * The {@code RefyLocalUser} class is useful to represent a {@link RefyUser} in the client application
 *
 * @see EquinoxLocalUser
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Structure
public abstract class RefyLocalUser extends EquinoxLocalUser {

    /**
     * {@code REFY_PREFERENCES_FILE} the name of the preferences file
     */
    protected static final String REFY_PREFERENCES_FILE = "Refy";

    /**
     * {@code tagName} the tag name of the user
     */
    protected String tagName;

    /**
     * {@code links} the links of the user
     */
    protected ArrayList<RefyLink> links;

    /**
     * {@code collections} the collections of the user
     */
    protected ArrayList<LinksCollection> collections;

    /**
     * {@code teams} the teams of the user
     */
    protected ArrayList<Team> teams;

    /**
     * {@code customLinks} the custom links of the user
     */
    protected ArrayList<CustomRefyLink> customLinks;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLocalUser() {
        super.initLocalUser();
        links = new ArrayList<>();
        collections = new ArrayList<>();
        teams = new ArrayList<>();
        customLinks = new ArrayList<>();
        tagName = getPreference(TAG_NAME_KEY);
    }

    /**
     * Method to insert and init a new local user
     *
     * @param hostAddress: the host address which the user communicate
     * @param name:        the name of the user
     * @param surname:     the surname of the user
     * @param email:       the email of the user
     * @param password:    the password of the user
     * @param language:    the language of the user
     * @param hResponse:   the payload response received from an authentication request
     * @param tagName: the tag name of the user
     */
    public void insertNewUser(String hostAddress, String name, String surname, String email, String password,
                              String language, JsonHelper hResponse, String tagName) {
        setTagName(tagName);
        super.insertNewUser(hostAddress, name, surname, email, password, language, hResponse);
    }

    /**
     * Method to set the {@link #tagName} instance <br>
     *
     * @param tagName: the tag name of the user
     */
    public void setTagName(String tagName) {
        setPreference(TAG_NAME_KEY, tagName);
        this.tagName = tagName;
    }

    /**
     * Method to get {@link #tagName} instance <br>
     * No-any params required
     *
     * @return {@link #tagName} instance as {@link String}
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Method to get {@link #links} instance <br>
     * No-any params required
     *
     * @return {@link #links} instance as {@link List} of {@link RefyLink}
     */
    public List<RefyLink> getLinks(boolean ownedOnly) {
        return filterOwnedOnlyUserList(links, ownedOnly);
    }

    /**
     * Method to set {@link #links} instance <br>
     *
     * @param links: the links of the user
     *
     */
    public void setLinks(List<RefyLink> links) {
        setUserList(this.links, links);
    }

    /**
     * Method to get {@link #collections} instance <br>
     * No-any params required
     *
     * @return {@link #collections} instance as {@link List} of {@link LinksCollection}
     */
    public List<LinksCollection> getCollections(boolean ownedOnly) {
        return filterOwnedOnlyUserList(collections, ownedOnly);
    }

    /**
     * Method to set {@link #collections} instance <br>
     *
     * @param collections: the collections of the user
     *
     */
    public void setCollections(List<LinksCollection> collections) {
        setUserList(this.collections, collections);
    }

    /**
     * Method to get {@link #teams} instance <br>
     * No-any params required
     *
     * @return {@link #teams} instance as {@link List} of {@link Team}
     */
    public List<Team> getTeams(boolean ownedOnly) {
        return filterOwnedOnlyUserList(teams, ownedOnly);
    }

    /**
     * Method to set {@link #teams} instance <br>
     *
     * @param teams: the teams where the user is a member
     *
     */
    public void setTeams(List<Team> teams) {
        setUserList(this.teams, teams);
    }

    /**
     * Method to get {@link #customLinks} instance <br>
     * No-any params required
     *
     * @return {@link #customLinks} instance as {@link List} of {@link CustomRefyLink}
     */
    public List<CustomRefyLink> getCustomLinks(boolean ownedOnly) {
        return filterOwnedOnlyUserList(customLinks, ownedOnly);
    }

    /**
     * Method to set {@link #customLinks} instance <br>
     *
     * @param customLinks: the custom links of the user
     *
     */
    public void setCustomLinks(List<CustomRefyLink> customLinks) {
        setUserList(this.customLinks, customLinks);
    }

    /**
     * Method to set a user list
     *
     * @param currentList: the current list of the user
     * @param newList: the new list to set
     *
     * @param <T>: the {@link RefyItem} contained in the list
     */
    private <T extends RefyItem> void setUserList(List<T> currentList, List<T> newList) {
        currentList.clear();
        currentList.addAll(newList);
    }

    /**
     * Method to get a user list with only the item who is the author if it needs to be filtered
     *
     * @param userList: the list of the user
     * @param filter: whether filter, so fetch only the items where the user is the author
     *
     * @param <T>: the {@link RefyItem} contained in the list
     *
     * @return the list of the items filtered as {@link List} of {@link T}
     */
    private <T extends RefyItem> List<T> filterOwnedOnlyUserList(List<T> userList, boolean filter) {
        if(!filter)
            return userList;
        List<T> ownedOnly = new ArrayList<>();
        for (T item : userList)
            if(item.getOwner().getId().equals(userId))
                ownedOnly.add(item);
        return ownedOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        links.clear();
        collections.clear();
        teams.clear();
        customLinks.clear();
    }

}
