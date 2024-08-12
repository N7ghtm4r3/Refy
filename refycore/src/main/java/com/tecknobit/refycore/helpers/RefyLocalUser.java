package com.tecknobit.refycore.helpers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinox.environment.records.EquinoxLocalUser;
import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.Team;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import com.tecknobit.refycore.records.links.RefyLink;

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

    private List<RefyLink> links;

    private List<LinksCollection> collections;

    private List<Team> teams;

    private List<CustomRefyLink> customLinks;

    @Override
    protected void initLocalUser() {
        super.initLocalUser();
        links = List.of();
        collections = List.of();
        teams = List.of();
        customLinks = List.of();
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

    public List<RefyLink> getLinks() {
        return links;
    }

    public void setLinks(List<RefyLink> links) {
        this.links = links;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

    public void setCollections(List<LinksCollection> collections) {
        this.collections = collections;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<CustomRefyLink> getCustomLinks() {
        return customLinks;
    }

    public void setCustomLinks(List<CustomRefyLink> customLinks) {
        this.customLinks = customLinks;
    }

}
