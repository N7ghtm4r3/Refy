package com.tecknobit.refycore.records.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.refycore.records.RefyUser;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.links.CustomRefyLink.CUSTOM_LINK_KEY;
import static jakarta.persistence.EnumType.STRING;
import static java.util.concurrent.TimeUnit.*;

@Entity
@Table(name = CUSTOM_LINKS_KEY)
@DiscriminatorValue(CUSTOM_LINK_KEY)
@JsonIgnoreProperties({
    COLLECTIONS_KEY,
    TEAMS_KEY
})
public class CustomRefyLink extends RefyLink {

    public static final String CUSTOM_LINK_KEY = "custom_link";

    public static final String CREATION_DATE_KEY = "creation_date";

    public static final String UNIQUE_ACCESS_KEY = "unique_access";

    public static final String EXPIRED_TIME_KEY = "expired_time";

    public static final String RESOURCES_KEY = "resources";

    public static final String RESOURCE_KEY = "resource_key";

    public static final String RESOURCE_VALUE_KEY = "resource_value";

    public static final String FIELDS_KEY = "fields";

    public static final String FIELD_KEY = "field_key";

    public static final String FIELD_VALUE_KEY = "field_value";

    public enum ExpiredTime {

        NO_EXPIRATION(0, -1),

        ONE_MINUTE(1, MINUTES.toMillis(1)),

        FIFTEEN_MINUTES(15, MINUTES.toMillis(15)),

        THIRTY_MINUTES(30, MINUTES.toMillis(30)),

        ONE_HOUR(1, HOURS.toMillis(1)),

        ONE_DAY(1, DAYS.toMillis(1)),

        ONE_WEEK(1, DAYS.toMillis(7));

        private final int timeValue;

        private final long gap;

        ExpiredTime(int timeValue, long gap) {
            this.timeValue = timeValue;
            this.gap = gap;
        }

        public int getTimeValue() {
            return timeValue;
        }

        public long getGap() {
            return gap;
        }

    }

    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    @Column(name = UNIQUE_ACCESS_KEY)
    private final boolean uniqueAccess;

    @Enumerated(value = STRING)
    @Column(name = EXPIRED_TIME_KEY)
    private final ExpiredTime expiredTime;

    @ElementCollection
    @CollectionTable(
            name = RESOURCES_KEY,
            joinColumns = @JoinColumn(name = IDENTIFIER_KEY),
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + IDENTIFIER_KEY + ") REFERENCES "
                            + LINKS_KEY + "(" + LINK_IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    @MapKeyColumn(name = RESOURCE_KEY)
    @Column(name = RESOURCE_VALUE_KEY)
    private final Map<String, String> resources;

    @ElementCollection
    @CollectionTable(
            name = FIELDS_KEY,
            joinColumns = @JoinColumn(name = IDENTIFIER_KEY),
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (" + IDENTIFIER_KEY + ") REFERENCES "
                            + LINKS_KEY + "(" + LINK_IDENTIFIER_KEY + ") ON DELETE CASCADE"
            )
    )
    @MapKeyColumn(name = FIELD_KEY)
    @Column(name = FIELD_VALUE_KEY)
    private final Map<String, String> fields;

    public CustomRefyLink() {
        this(null, null, null, null, null, -1, false, null, null, null);
    }

    public CustomRefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                          long creationDate, boolean uniqueAccess, ExpiredTime expiredTime,
                          Map<String, String> resources, Map<String, String> fields) {
        super(id, owner, title, description, referenceLink, null, null);
        this.creationDate = creationDate;
        this.uniqueAccess = uniqueAccess;
        this.expiredTime = expiredTime;
        this.resources = resources;
        this.fields = fields;
    }

    public CustomRefyLink(JSONObject jCustomRefyLink) {
        super(jCustomRefyLink);
        creationDate = hItem.getLong(CREATION_DATE_KEY, -1);
        uniqueAccess = hItem.getBoolean(UNIQUE_ACCESS_KEY);
        expiredTime = ExpiredTime.valueOf(EXPIRED_TIME_KEY);
        //TODO: TO LOAD CORRECTLY
        resources = new HashMap<>();
        fields = new HashMap<>();
    }

    @JsonGetter(CREATION_DATE_KEY)
    public long getCreationTimestamp() {
        return creationDate;
    }

    @JsonIgnore
    public String getCreationDate() {
        return timeFormatter.formatAsString(creationDate);
    }

    public boolean hasUniqueAccess() {
        return uniqueAccess;
    }

    public boolean expires() {
        return expiredTime != null && expiredTime != ExpiredTime.NO_EXPIRATION;
    }

    @JsonIgnore
    public long getExpirationTimestamp() {
        if(expires())
            return creationDate + expiredTime.getGap();
        return -1;
    }

    @JsonIgnore
    public String getExpirationDate() {
        long expiration = getExpirationTimestamp();
        if(expiration != -1)
            return timeFormatter.formatAsString(expiration);
        return null;
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
