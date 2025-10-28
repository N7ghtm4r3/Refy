package com.tecknobit.refy.services.users.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.annotations.MappingPurpose;
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.CLOSE_APPLICATION_ON_LINK_OPEN_KEY;
import static com.tecknobit.refycore.ConstantsKt.SETTINGS_KEY;

@RequiresDocumentation(
        additionalNotes = "INSERT SINCE"
)
@Entity
@Table(name = SETTINGS_KEY)
public class UserSettings {

    public static final UserSettings DEFAULT_USER_SETTINGS = new UserSettings(null, false);

    @Id
    @MappingPurpose
    private final String id;

    @MapsId
    @OneToOne
    @JoinColumn(name = IDENTIFIER_KEY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final RefyUser owner;

    @Column(
            name = CLOSE_APPLICATION_ON_LINK_OPEN_KEY,
            columnDefinition = "BOOL DEFAULT false"
    )
    private final boolean closeApplicationOnLinkOpen;

    @EmptyConstructor
    public UserSettings() {
        this(null, false);
    }

    public UserSettings(RefyUser owner, boolean closeApplicationOnLinkOpen) {
        String ownerId = null;
        if(owner != null)
            ownerId = owner.getId();
        this.id = ownerId;
        this.owner = owner;
        this.closeApplicationOnLinkOpen = closeApplicationOnLinkOpen;
    }

    @JsonIgnore
    public RefyUser getOwner() {
        return owner;
    }

    @JsonGetter(CLOSE_APPLICATION_ON_LINK_OPEN_KEY)
    public boolean closeApplicationOnLinkOpen() {
        return closeApplicationOnLinkOpen;
    }

}
