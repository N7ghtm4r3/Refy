package com.tecknobit.refycore.helpers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinox.environment.records.EquinoxLocalUser;

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

    @Override
    protected void initLocalUser() {
        super.initLocalUser();
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

}
