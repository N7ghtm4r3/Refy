package com.tecknobit.refycore.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

import static com.tecknobit.equinox.environment.records.EquinoxUser.*;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.RefyUser.getInstance;

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
     * {@code OWNED_ONLY_KEY} the key for the <b>"owned_only"</b> field
     */
    public static final String OWNED_ONLY_KEY = "owned_only";

    /**
     * {@code OWNER_KEY} the key for the <b>"owner"</b> field
     */
    public static final String OWNER_KEY = "owner";

    /**
     * {@code TITLE_KEY} the key for the <b>"title"</b> field
     */
    public static final String TITLE_KEY = "title";

    /**
     * {@code DESCRIPTION_KEY} the key for the <b>"description"</b> field
     */
    public static final String DESCRIPTION_KEY = "description";

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
    @Column(name = DESCRIPTION_KEY)
    protected final String description;

    /**
     * Constructor to init the {@link RefyItem} class
     *
     * @param id: the identifier of the item
     * @param owner: the owner of the item
     * @param title: the title of the item
     * @param description:the description of the item
     *
     */
    public RefyItem(String id, RefyUser owner, String title, String description) {
        super(id);
        this.title = title;
        this.owner = owner;
        this.description = description;
    }

    /**
     * Constructor to init the {@link RefyItem} class
     *
     * @param jRefyItem: the json details of the item as {@link JSONObject}
     *
     */
    public RefyItem(JSONObject jRefyItem) {
        super(jRefyItem);
        owner = getInstance(hItem.getJSONObject(OWNER_KEY));
        title = hItem.getString(TITLE_KEY);
        description = hItem.getString(DESCRIPTION_KEY);
    }

    /**
     * Method to get {@link #owner} instance <br>
     * No-any params required
     *
     * @return {@link #owner} instance as {@link RefyUser}
     */
    public RefyUser getOwner() {
        return owner;
    }

    /**
     * Method to get {@link #title} instance <br>
     * No-any params required
     *
     * @return {@link #title} instance as {@link String}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method to get {@link #description} instance <br>
     * No-any params required
     *
     * @return {@link #description} instance as {@link String}
     */
    public String getDescription() {
        return description;
    }

    /**
     * The {@code ListScreenItem} interface useful to manage the item of the items displayed
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public interface ListScreenItem {

        /**
         * Method to check if the user can update the current item
         *
         * @param loggedUserId: the current user logged user identifier
         * @return whether use can update the item as boolean
         */
        boolean canBeUpdatedByUser(String loggedUserId);

    }

}
