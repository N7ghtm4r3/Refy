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

@Structure
@MappedSuperclass
public abstract class RefyItem extends EquinoxItem {

    public static final String OWNED_ONLY_KEY = "owned_only";

    public static final String OWNER_KEY = "owner";

    public static final String TITLE_KEY = "title";

    public static final String DESCRIPTION_KEY = "description";

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

    @Column(name = TITLE_KEY)
    protected final String title;

    @Column(name = DESCRIPTION_KEY)
    protected final String description;

    public RefyItem(String id, RefyUser owner, String title, String description) {
        super(id);
        this.title = title;
        this.owner = owner;
        this.description = description;
    }

    public RefyItem(JSONObject jRefyItem) {
        super(jRefyItem);
        owner = getInstance(hItem.getJSONObject(OWNER_KEY));
        title = hItem.getString(TITLE_KEY);
        description = hItem.getString(DESCRIPTION_KEY);
    }

    public RefyUser getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public interface ListScreenItem {

        boolean canBeUpdatedByUser(String loggedUserId);

    }

}
