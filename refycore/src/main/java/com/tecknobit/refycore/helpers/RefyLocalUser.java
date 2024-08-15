package com.tecknobit.refycore.helpers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinox.environment.records.EquinoxLocalUser;
import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyItem;
import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import com.tecknobit.refycore.records.links.RefyLink;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.refycore.records.RefyUser.TAG_NAME_KEY;

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

    protected ArrayList<RefyLink> links;

    protected ArrayList<LinksCollection> collections;

    protected ArrayList<Team> teams;

    protected ArrayList<CustomRefyLink> customLinks;

    @Override
    protected void initLocalUser() {
        super.initLocalUser();
        links = new ArrayList<>();
        collections = new ArrayList<>();
        teams = new ArrayList<>();
        customLinks = new ArrayList<>();
        tagName = getPreference(TAG_NAME_KEY);
    }

    public void insertNewUser(String hostAddress, String name, String surname, String email, String password,
                              String language, JsonHelper hResponse, String tagName) {
        setTagName(tagName);
        super.insertNewUser(hostAddress, name, surname, email, password, language, hResponse);
    }

    public void setTagName(String tagName) {
        setPreference(TAG_NAME_KEY, tagName);
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public List<RefyLink> getLinks(boolean ownedOnly) {
        return filterOwnedOnlyUserList(links, ownedOnly);
    }

    public void setLinks(List<RefyLink> links) {
        setUserList(this.links, links);
    }

    public List<LinksCollection> getCollections(boolean ownedOnly) {
        return filterOwnedOnlyUserList(collections, ownedOnly);
    }

    public void setCollections(List<LinksCollection> collections) {
        setUserList(this.collections, collections);
    }

    public List<Team> getTeams(boolean ownedOnly) {
        return filterOwnedOnlyUserList(teams, ownedOnly);
    }

    public void setTeams(List<Team> teams) {
        setUserList(this.teams, teams);
    }

    public List<CustomRefyLink> getCustomLinks(boolean ownedOnly) {
        return filterOwnedOnlyUserList(customLinks, ownedOnly);
    }

    public void setCustomLinks(List<CustomRefyLink> customLinks) {
        setUserList(this.customLinks, customLinks);
    }

    private <T extends RefyItem> void setUserList(List<T> currentList, List<T> newList) {
        currentList.clear();
        currentList.addAll(newList);
    }

    private <T extends RefyItem> List<T> filterOwnedOnlyUserList(List<T> userList, boolean filter) {
        if(!filter)
            return userList;
        List<T> ownedOnly = new ArrayList<>();
        for (T item : userList)
            if(item.getOwner().getId().equals(userId))
                ownedOnly.add(item);
        return ownedOnly;
    }

    @Override
    public void clear() {
        links.clear();
        collections.clear();
        teams.clear();
        customLinks.clear();
    }

}
