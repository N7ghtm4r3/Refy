package com.tecknobit.refycore.records;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

import static com.tecknobit.equinox.environment.records.EquinoxUser.NAME_KEY;

@Structure
public abstract class RefyItem extends EquinoxItem {

    public static final String DESCRIPTION_KEY = "description_key";

    protected final RefyUser owner;

    protected final String title;

    protected final String description;

    public RefyItem(String id, RefyUser owner, String title, String description) {
        super(id);
        this.title = title;
        this.owner = owner;
        this.description = description;
    }

    public RefyItem(JSONObject jRefyItem) {
        super(jRefyItem);
        //TODO: TO LOAD CORRECTLY
        owner = null;
        title = hItem.getString(NAME_KEY);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefyItem refyItem = (RefyItem) o;
        return refyItem.id.equals(id);
    }

}
