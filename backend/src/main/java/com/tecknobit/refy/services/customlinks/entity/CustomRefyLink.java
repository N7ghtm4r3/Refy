package com.tecknobit.refy.services.customlinks.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.entities.RefyItem;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refycore.enums.ExpiredTime;
import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.*;
import static jakarta.persistence.EnumType.STRING;

/**
 * The {@code CustomRefyLink} class is useful to represent a custom Refy's link, useful to share resources and allowing
 * to protect them with an authentication form with custom fields
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see RefyItem
 * @see RefyLink
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Entity
@DiscriminatorValue(CUSTOM_LINK_KEY)
@JsonIgnoreProperties({
        COLLECTIONS_KEY,
        TEAMS_KEY,
        THUMBNAIL_PREVIEW_KEY
})
public class CustomRefyLink extends RefyLink {

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
     * Constructor used to init the {@link CustomRefyLink} class 
     * @apiNote empty constructor required
     */
    @EmptyConstructor
    public CustomRefyLink() {
        this(null, null, null, null, null, -1, false, null, null, null, null);
    }

    /**
     * Constructor used to init the {@link CustomRefyLink} class
     *
     * @param id The identifier of the link
     * @param owner The owner of the link
     * @param title The title of the link
     * @param description The description of the link
     * @param referenceLink The link reference value
     * @param uniqueAccess: whether the link, when requested for the first time, must be deleted and no more accessible
     * @param expiredTime: if set, when the link expires and automatically deleted
     * @param resources The resources to share with the link
     * @param fields The fields used to protect the {@link #resources} with a validation form
     * @param previewToken The token used to entry in the preview mode
     *
     */
    public CustomRefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                          long creationDate, boolean uniqueAccess, ExpiredTime expiredTime, Map<String, String> resources,
                          Map<String, String> fields, String previewToken) {
        super(id, owner, title, description, creationDate, null, referenceLink, List.of(), List.of());
        this.uniqueAccess = uniqueAccess;
        this.expiredTime = expiredTime;
        this.resources = resources;
        this.fields = fields;
        this.previewToken = previewToken;
    }

    /**
     * Method to get {@link #uniqueAccess} instance
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
    @JsonIgnore
    public boolean expires() {
        return expiredTime != null && expiredTime != ExpiredTime.NO_EXPIRATION;
    }

    /**
     * Method to get the expiration timestamp value
     *
     * @return expiration timestamp instance as long
     */
    @JsonIgnore
    public long getExpirationTimestamp() {
        if(expires())
            return date + expiredTime.getGap();
        return -1;
    }

    /**
     * Method to get whether the link has been expired
     *
     * @return whether the link has been expired as boolean
     */
    @JsonIgnore
    public boolean isExpired() {
        return expires() && System.currentTimeMillis() >= getExpirationTimestamp();
    }

    /**
     * Method to get {@link #expiredTime} instance
     *
     * @return {@link #expiredTime} instance as {@link ExpiredTime}
     */
    @JsonGetter(EXPIRED_TIME_KEY)
    public ExpiredTime getExpiredTime() {
        return expiredTime;
    }

    /**
     * Method to get {@link #resources} instance
     *
     * @return {@link #resources} instance as {@link Map} of {@link String} and {@link String}
     */
    public Map<String, String> getResources() {
        return resources;
    }

    /**
     * Method to get {@link #fields} instance
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
     * Method to get {@link #previewToken} instance
     *
     * @return {@link #previewToken} instance as {@link String}
     */
    @JsonGetter(PREVIEW_TOKEN_KEY)
    public String getPreviewToken() {
        return previewToken;
    }

}
