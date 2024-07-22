package com.tecknobit.refycore.records;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

import static com.tecknobit.equinox.environment.records.EquinoxUser.NAME_KEY;

@Structure
public abstract class RefyItem extends EquinoxItem {

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

}
