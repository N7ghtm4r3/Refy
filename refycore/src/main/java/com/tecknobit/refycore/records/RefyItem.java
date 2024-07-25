package com.tecknobit.refycore.records;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

import java.util.Objects;

import static com.tecknobit.equinox.environment.records.EquinoxUser.NAME_KEY;

@Structure
public abstract class RefyItem extends EquinoxItem {

    public static final String DESCRIPTION_KEY = "description_key";

    protected final String title;

    public RefyItem(String id, String title) {
        super(id);
        this.title = title;
    }

    public RefyItem(JSONObject jRefyItem) {
        super(jRefyItem);
        title = hItem.getString(NAME_KEY);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefyItem refyItem = (RefyItem) o;
        return Objects.equals(title, refyItem.title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title);
    }

}
