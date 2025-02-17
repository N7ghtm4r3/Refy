package com.tecknobit.refy.services.shared.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.refy.services.users.entity.RefyUser;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.LANGUAGE_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code RefyItem} class is useful to create a Refy's item giving the basis structure to work correctly
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Structure
@MappedSuperclass
public abstract class RefyItem extends EquinoxItem {

    /**
     * {@code owner} the owner of the item
     */
    @ManyToOne
    @JoinColumn(name = OWNER_KEY)
    @JsonIgnoreProperties({
            TOKEN_KEY,
            LANGUAGE_KEY,
            LINKS_KEY,
            COLLECTIONS_KEY,
            TEAMS_KEY,
            CUSTOM_LINKS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected final RefyUser owner;

    /**
     * {@code title} the title of the item
     */
    @Column(name = TITLE_KEY)
    protected final String title;

    /**
     * {@code description} the description of the item
     */
    @Lob
    @Column(
            name = DESCRIPTION_KEY,
            columnDefinition = "MEDIUMTEXT",
            nullable = false
    )
    protected final String description;

    /**
     * {@code date} the date when the item has been inserted in the system
     */
    @Column(name = DATE_KEY)
    protected final long date;

    /**
     * Constructor to init the {@link RefyItem} class
     *
     * @param id The identifier of the item
     * @param owner The owner of the item
     * @param title The title of the item
     * @param description The description of the item
     * @param date The date when the item has been inserted in the system
     *
     */
    public RefyItem(String id, RefyUser owner, String title, String description, long date) {
        super(id);
        this.title = title;
        this.owner = owner;
        this.description = description;
        this.date = date;
    }

    /**
     * Method to get {@link #owner} instance
     *
     * @return {@link #owner} instance as {@link RefyUser}
     */
    public RefyUser getOwner() {
        return owner;
    }

    /**
     * Method to get {@link #title} instance
     *
     * @return {@link #title} instance as {@link String}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method to get {@link #description} instance
     *
     * @return {@link #description} instance as {@link String}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method to get {@link #date} instance
     *
     * @return {@link #date} instance as {@code long}
     */
    public long getDate() {
        return date;
    }

}
