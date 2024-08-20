package com.tecknobit.refycore.records.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import com.tecknobit.refycore.records.RefyItem;
import com.tecknobit.refycore.records.RefyUser;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.links.CustomRefyLink.CUSTOM_LINK_KEY;
import static jakarta.persistence.EnumType.STRING;
import static java.util.concurrent.TimeUnit.*;

/**
 * The {@code CustomRefyLink} class is useful to represent a custom Refy's link, useful to share resources and allowing
 * to protect them with an authentication form with custom fields
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see RefyItem
 * @see ListScreenItem
 * @see RefyLink
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Entity
@Table(name = CUSTOM_LINKS_KEY)
@DiscriminatorValue(CUSTOM_LINK_KEY)
@JsonIgnoreProperties({
    COLLECTIONS_KEY,
    TEAMS_KEY
})
public class CustomRefyLink extends RefyLink {

    /**
     * {@code CUSTOM_LINKS_PATH} the key for the <b>"customLinks"</b> url path
     */
    public static final String CUSTOM_LINKS_PATH = "customLinks";

    /**
     * {@code CUSTOM_LINK_KEY} the key for the <b>"custom_link"</b> field
     */
    public static final String CUSTOM_LINK_KEY = "custom_link";

    /**
     * {@code CREATION_DATE_KEY} the key for the <b>"creation_date"</b> field
     */
    public static final String CREATION_DATE_KEY = "creation_date";

    /**
     * {@code UNIQUE_ACCESS_KEY} the key for the <b>"unique_access"</b> field
     */
    public static final String UNIQUE_ACCESS_KEY = "unique_access";

    /**
     * {@code EXPIRED_TIME_KEY} the key for the <b>"expired_time"</b> field
     */
    public static final String EXPIRED_TIME_KEY = "expired_time";

    /**
     * {@code RESOURCES_KEY} the key for the <b>"resources"</b> field
     */
    public static final String RESOURCES_KEY = "resources";

    /**
     * {@code RESOURCE_KEY} the key for the <b>"resource_key"</b> field
     */
    public static final String RESOURCE_KEY = "resource_key";

    /**
     * {@code RESOURCE_VALUE_KEY} the key for the <b>"resource_value"</b> field
     */
    public static final String RESOURCE_VALUE_KEY = "resource_value";

    /**
     * {@code FIELDS_KEY} the key for the <b>"fields"</b> field
     */
    public static final String FIELDS_KEY = "fields";

    /**
     * {@code FIELD_KEY} the key for the <b>"field_key"</b> field
     */
    public static final String FIELD_KEY = "field_key";

    /**
     * {@code FIELD_VALUE_KEY} the key for the <b>"field_value"</b> field
     */
    public static final String FIELD_VALUE_KEY = "field_value";

    /**
     * {@code PREVIEW_TOKEN_KEY} the key for the <b>"preview_token"</b> field
     */
    public static final String PREVIEW_TOKEN_KEY = "preview_token";

    /**
     * {@code ExpiredTime} list of available expiration time
     */
    public enum ExpiredTime {

        /**
         * {@code NO_EXPIRATION} the link not expires
         */
        NO_EXPIRATION(0, -1),

        /**
         * {@code ONE_MINUTE} the link expires after one minute after creation
         */
        ONE_MINUTE(1, MINUTES.toMillis(1)),

        /**
         * {@code FIFTEEN_MINUTES} the link expires after fifteen minutes after creation
         */
        FIFTEEN_MINUTES(15, MINUTES.toMillis(15)),

        /**
         * {@code THIRTY_MINUTES} the link expires after thirty minutes after creation
         */
        THIRTY_MINUTES(30, MINUTES.toMillis(30)),

        /**
         * {@code ONE_HOUR} the link expires after one hour after creation
         */
        ONE_HOUR(1, HOURS.toMillis(1)),

        /**
         * {@code ONE_DAY} the link expires after one day after creation
         */
        ONE_DAY(1, DAYS.toMillis(1)),

        /**
         * {@code ONE_WEEK} the link expires after one week after creation
         */
        ONE_WEEK(1, DAYS.toMillis(7));

        /**
         * {@code timeValue} temporal value of the expiration
         */
        private final int timeValue;

        /**
         * {@code gap} the temporal gap value used to calculated if the link has been expired
         */
        private final long gap;

        /**
         * Constructor to init the {@link ExpiredTime} class
         *
         * @param timeValue: temporal value of the expiration
         * @param gap: the temporal gap value used to calculated if the link has been expired
         *
         */
        ExpiredTime(int timeValue, long gap) {
            this.timeValue = timeValue;
            this.gap = gap;
        }

        /**
         * Method to get {@link #timeValue} instance <br>
         * No-any params required
         *
         * @return {@link #timeValue} instance as int
         */
        public int getTimeValue() {
            return timeValue;
        }

        /**
         * Method to get {@link #gap} instance <br>
         * No-any params required
         *
         * @return {@link #gap} instance as long
         */
        public long getGap() {
            return gap;
        }

    }

    /**
     * {@code creationDate} when the link has been created
     */
    @Column(name = CREATION_DATE_KEY)
    private final long creationDate;

    /**
     * {@code uniqueAccess} whether the link, when requested for the first time, must be deleted and no more accessible
     */
    @Column(name = UNIQUE_ACCESS_KEY)
    private final boolean uniqueAccess;

    /**
     * {@code expiredTime} if set, when the link expires and automatically deleted
     */
    @Enumerated(value = STRING)
    @Column(name = EXPIRED_TIME_KEY)
    private final ExpiredTime expiredTime;

    /**
     * {@code resources} the resources to share with the link
     */
    @ElementCollection(
            fetch = FetchType.EAGER
    )
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

    /**
     * {@code fields} the fields used to protect the {@link #resources} with a validation form
     */
    @ElementCollection(
            fetch = FetchType.EAGER
    )
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

    /**
     * {@code previewToken} the token used to entry in the preview mode
     */
    @Column(
            name = PREVIEW_TOKEN_KEY,
            columnDefinition = "VARCHAR(32) DEFAULT NULL",
            unique = true
    )
    private final String previewToken;

    /**
     * Constructor to init the {@link CustomRefyLink} class <br>
     *
     * No-any params required
     * @apiNote empty constructor required
     */
    public CustomRefyLink() {
        this(null, null, null, null, null, -1, false, null, null, null, null);
    }

    /**
     * Constructor to init the {@link CustomRefyLink} class
     *
     * @param id: the identifier of the link
     * @param owner: the owner of the link
     * @param title: the title of the link
     * @param description:the description of the link
     * @param referenceLink: the link reference value
     * @param uniqueAccess: whether the link, when requested for the first time, must be deleted and no more accessible
     * @param expiredTime: if set, when the link expires and automatically deleted
     * @param resources: the resources to share with the link
     * @param fields: the fields used to protect the {@link #resources} with a validation form
     * @param previewToken: the token used to entry in the preview mode
     *
     */
    public CustomRefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                          long creationDate, boolean uniqueAccess, ExpiredTime expiredTime, Map<String, String> resources,
                          Map<String, String> fields, String previewToken) {
        super(id, owner, title, description, referenceLink, List.of(), List.of());
        this.creationDate = creationDate;
        this.uniqueAccess = uniqueAccess;
        this.expiredTime = expiredTime;
        this.resources = resources;
        this.fields = fields;
        this.previewToken = previewToken;
    }

    /**
     * Constructor to init the {@link CustomRefyLink} class
     *
     * @param jCustomRefyLink: the json details of the custom link as {@link JSONObject}
     *
     */
    public CustomRefyLink(JSONObject jCustomRefyLink) {
        super(jCustomRefyLink);
        creationDate = hItem.getLong(CREATION_DATE_KEY, -1);
        uniqueAccess = hItem.getBoolean(UNIQUE_ACCESS_KEY);
        expiredTime = ExpiredTime.valueOf(hItem.getString(EXPIRED_TIME_KEY));
        resources = loadMap(hItem.getJSONObject(RESOURCES_KEY));
        fields = loadMap(hItem.getJSONObject(FIELDS_KEY));
        previewToken = hItem.getString(PREVIEW_TOKEN_KEY);
    }

    /**
     * Method to load a map instance
     *
     * @param jMap: the json object from fetch the data to load the map
     * @return map loaded as {@link Map} of {@link String} and {@link String}
     */
    private Map<String, String> loadMap(JSONObject jMap) {
        HashMap<String, String> map = new HashMap<>();
        if(jMap != null)
            for (String key : jMap.keySet())
                map.put(key, jMap.getString(key));
        return map;
    }

    /**
     * Method to get {@link #creationDate} instance <br>
     * No-any params required
     *
     * @return {@link #creationDate} instance as long
     */
    @JsonGetter(CREATION_DATE_KEY)
    public long getCreationTimestamp() {
        return creationDate;
    }

    /**
     * Method to get {@link #creationDate} instance <br>
     * No-any params required
     *
     * @return {@link #creationDate} instance as {@link String}
     */
    @JsonIgnore
    public String getCreationDate() {
        return timeFormatter.formatAsString(creationDate);
    }

    /**
     * Method to get {@link #uniqueAccess} instance <br>
     * No-any params required
     *
     * @return {@link #uniqueAccess} instance as boolean
     */
    @JsonGetter(UNIQUE_ACCESS_KEY)
    public boolean hasUniqueAccess() {
        return uniqueAccess;
    }

    /**
     * Method to get whether the link expires or not<br>
     * No-any params required
     *
     * @return whether the link expires or not as boolean
     */
    public boolean expires() {
        return expiredTime != null && expiredTime != ExpiredTime.NO_EXPIRATION;
    }

    /**
     * Method to get the expiration timestamp value <br>
     * No-any params required
     *
     * @return {@link #creationDate+expiredTime.getGap()} instance as long
     */
    @JsonIgnore
    public long getExpirationTimestamp() {
        if(expires())
            return creationDate + expiredTime.getGap();
        return -1;
    }

    /**
     * Method to get the expiration date <br>
     * No-any params required
     *
     * @return the expiration date as {@link String}
     */
    @JsonIgnore
    public String getExpirationDate() {
        long expiration = getExpirationTimestamp();
        if(expiration != -1)
            return timeFormatter.formatAsString(expiration);
        return null;
    }

    /**
     * Method to get whether the link has been expired <br>
     * No-any params required
     *
     * @return whether the link has been expired as boolean
     */
    public boolean isExpired() {
        return expires() && System.currentTimeMillis() >= getExpirationTimestamp();
    }

    /**
     * Method to get {@link #expiredTime} instance <br>
     * No-any params required
     *
     * @return {@link #expiredTime} instance as {@link ExpiredTime}
     */
    @JsonGetter(EXPIRED_TIME_KEY)
    public ExpiredTime getExpiredTime() {
        return expiredTime;
    }

    /**
     * Method to get {@link #resources} instance <br>
     * No-any params required
     *
     * @return {@link #resources} instance as {@link Map} of {@link String} and {@link String}
     */
    public Map<String, String> getResources() {
        return resources;
    }

    /**
     * Method to get {@link #fields} instance <br>
     * No-any params required
     *
     * @return {@link #fields} instance as {@link Map} of {@link String} and {@link String}
     */
    public Map<String, String> getFields() {
        return fields;
    }

    /**
     * Method to get whether the resources are protected or not<br>
     * No-any params required
     *
     * @return whether the resources are protected or not as boolean
     */
    public boolean mustValidateFields() {
        return !fields.isEmpty();
    }

    /**
     * Method to get {@link #previewToken} instance <br>
     * No-any params required
     *
     * @return {@link #previewToken} instance as {@link String}
     */
    @JsonGetter(PREVIEW_TOKEN_KEY)
    public String getPreviewToken() {
        return previewToken;
    }

    /**
     * Method to get the url to enter the preview mode <br>
     * No-any params required
     *
     * @return the url to enter the preview mode as {@link String}
     */
    @JsonIgnore
    public String getPreviewModeUrl(String hostAddress) {
        return hostAddress + BASE_EQUINOX_ENDPOINT + CUSTOM_LINKS_PATH + "/" + id + "?" + PREVIEW_TOKEN_KEY + "="
                + previewToken;
    }

    /**
     * Method to assemble and return an {@link ArrayList} of links
     *
     * @param jLinks : links list details formatted as JSON
     * @return the link list as {@link ArrayList} of {@link CustomRefyLink}
     */
    @Returner
    public static ArrayList<CustomRefyLink> returnCustomLinks(JSONArray jLinks) {
        ArrayList<CustomRefyLink> links = new ArrayList<>();
        if (jLinks == null)
            return links;
        for (int j = 0; j < jLinks.length(); j++)
            links.add(new CustomRefyLink(jLinks.getJSONObject(j)));
        return links;
    }

}
