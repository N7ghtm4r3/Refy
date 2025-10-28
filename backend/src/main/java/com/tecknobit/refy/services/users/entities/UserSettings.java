package com.tecknobit.refy.services.users.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.annotations.MappingPurpose;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.CLOSE_APPLICATION_ON_LINK_OPEN_KEY;
import static com.tecknobit.refycore.ConstantsKt.SETTINGS_KEY;

/**
 * The {@code UserSettings} class represents the settings of the user
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @since 1.1.0
 */
@Entity
@Table(name = SETTINGS_KEY)
public class UserSettings {

    /**
     * {@code DEFAULT_USER_SETTINGS} The default settings value if the user did not change anything
     */
    public static final UserSettings DEFAULT_USER_SETTINGS = new UserSettings(null, false);

    /**
     * {@code id} The identifier of the user, owner of the settings
     */
    @Id
    @MappingPurpose
    private final String id;

    /**
     * {@code owner} The owner of the settings
     */
    @MapsId
    @OneToOne
    @JoinColumn(name = IDENTIFIER_KEY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final RefyUser owner;

    /**
     * {@code closeApplicationOnLinkOpen} Whether the user requires to close the application when a link has been opened
     */
    @Column(
            name = CLOSE_APPLICATION_ON_LINK_OPEN_KEY,
            columnDefinition = "BOOL DEFAULT false",
            insertable = false
    )
    private final boolean closeApplicationOnLinkOpen;

    /**
     * Constructor used to init the {@link UserSettings} class
     *
     * @apiNote empty constructor required
     */
    @EmptyConstructor
    public UserSettings() {
        this(null, false);
    }

    /**
     * Constructor used to init the {@link UserSettings} class
     *
     * @param owner The owner of the settings
     * @param closeApplicationOnLinkOpen Whether the user requires to close the application when a link has been opened
     */
    public UserSettings(RefyUser owner, boolean closeApplicationOnLinkOpen) {
        String ownerId = null;
        if(owner != null)
            ownerId = owner.getId();
        System.out.println(ownerId);
        this.id = ownerId;
        this.owner = owner;
        this.closeApplicationOnLinkOpen = closeApplicationOnLinkOpen;
    }

    /**
     * Method to get {@link #id} instance
     *
     * @return {@link #id} instance as {@link String}
     */
    @JsonIgnore
    public String getId() {
        return id;
    }

    /**
     * Method to get {@link #owner} instance
     *
     * @return {@link #owner} instance as {@link RefyUser}
     */
    @JsonIgnore
    public RefyUser getOwner() {
        return owner;
    }

    /**
     * Method to get {@link #closeApplicationOnLinkOpen} instance
     *
     * @return {@link #closeApplicationOnLinkOpen} instance as {@code boolean}
     */
    @JsonGetter(CLOSE_APPLICATION_ON_LINK_OPEN_KEY)
    public boolean closeApplicationOnLinkOpen() {
        return closeApplicationOnLinkOpen;
    }

}
