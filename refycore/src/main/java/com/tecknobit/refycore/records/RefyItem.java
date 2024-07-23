package com.tecknobit.refycore.records;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

import java.util.Objects;

import static com.tecknobit.equinox.environment.records.EquinoxUser.NAME_KEY;

@Structure
public abstract class RefyItem extends EquinoxItem {

    public static final String DESCRIPTION_KEY = "description_key";

    protected final String name;

    public RefyItem(String id, String name) {
        super(id);
        this.name = name;
    }

    public RefyItem(JSONObject jRefyItem) {
        super(jRefyItem);
        name = hItem.getString(NAME_KEY);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefyItem refyItem = (RefyItem) o;
        return Objects.equals(name, refyItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
