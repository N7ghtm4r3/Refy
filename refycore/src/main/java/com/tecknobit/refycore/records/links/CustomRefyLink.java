package com.tecknobit.refycore.records.links;

import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyUser;
import com.tecknobit.refycore.records.Team;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRefyLink extends RefyLink {

    public static final String UNIQUE_ACCESS_KEY = "unique_access";

    public static final String EXPIRED_TIME_KEY = "expired_time";

    public static final String RESOURCES_KEY = "resources";

    public static final String FIELDS_KEY = "fields";

    public enum ExpiredTime {

        NO_EXPIRATION(0),

        ONE_MINUTE(1),

        FIFTEEN_MINUTES(15),

        THIRTY_MINUTES(30),

        ONE_HOUR(1),

        ONE_DAY(1),

        ONE_WEEK(1);

        private final int timeValue;

        ExpiredTime(int timeValue) {
            this.timeValue = timeValue;
        }

        public int getTimeValue() {
            return timeValue;
        }

    }

    private final boolean uniqueAccess;

    private final ExpiredTime expiredTime;

    private final Map<String, String> resources;

    private final Map<String, String> fields;

    public CustomRefyLink() {
        this(null, null, null, null, null, List.of(), List.of(), false, null, null, null);
    }

    public CustomRefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                          List<Team> teams, List<LinksCollection> collections, boolean uniqueAccess,
                          ExpiredTime expiredTime, Map<String, String> resources, Map<String, String> fields) {
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
        //TODO: TO LOAD CORRECTLY
        resources = new HashMap<>();
        fields = new HashMap<>();
    }

    public boolean hasUniqueAccess() {
        return uniqueAccess;
    }

    public boolean expires() {
        return expiredTime != null && expiredTime != ExpiredTime.NO_EXPIRATION;
    }

    public ExpiredTime getExpiredTime() {
        return expiredTime;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public Map<String, String> getFields() {
        return fields;
    }

}
