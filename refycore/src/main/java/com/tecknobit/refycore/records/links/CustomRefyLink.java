package com.tecknobit.refycore.records.links;

import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyUser;
import com.tecknobit.refycore.records.Team;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class CustomRefyLink extends RefyLink {

    public static final String UNIQUE_ACCESS_KEY = "unique_access";

    public static final String EXPIRED_TIME_KEY = "expired_time";

    public static final String RESOURCES_KEY = "resources";

    public static final String FIELDS_KEY = "fields";

    public enum ExpiredTime {

        NO_EXPIRATION,

        FIFTEEN_MINUTES,

        THIRTY_MINUTES,

        ONE_HOUR,

        ONE_DAY,

        ONE_WEEK

    }

    private final boolean uniqueAccess;

    private final ExpiredTime expiredTime;

    private final HashMap<String, Object> resources;

    private final HashMap<String, Object> fields;

    public CustomRefyLink() {
        this(null, null, null, null, null, List.of(), List.of(), false, null, null, null);
    }

    public CustomRefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                          List<Team> teams, List<LinksCollection> collections, boolean uniqueAccess,
                          ExpiredTime expiredTime, HashMap<String, Object> resources, HashMap<String, Object> fields) {
        super(id, owner, title, description, referenceLink, teams, collections);
        this.uniqueAccess = uniqueAccess;
        this.expiredTime = expiredTime;
        this.resources = resources;
        this.fields = fields;
    }

    public CustomRefyLink(JSONObject jCustomRefyLink) {
        super(jCustomRefyLink);
        uniqueAccess = hItem.getBoolean(UNIQUE_ACCESS_KEY);
        expiredTime = ExpiredTime.valueOf(EXPIRED_TIME_KEY);
        resources = (HashMap<String, Object>) hItem.getJSONObject(RESOURCES_KEY, new JSONObject()).toMap();
        fields = (HashMap<String, Object>) hItem.getJSONObject(FIELDS_KEY, new JSONObject()).toMap();
    }

    public boolean hasUniqueAccess() {
        return uniqueAccess;
    }

    public ExpiredTime getExpiredTime() {
        return expiredTime;
    }

    public HashMap<String, Object> getResources() {
        return resources;
    }

    public HashMap<String, Object> getFields() {
        return fields;
    }

}
