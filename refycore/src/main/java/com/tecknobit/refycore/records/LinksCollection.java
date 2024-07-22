package com.tecknobit.refycore.records;

import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

public class LinksCollection extends RefyItem {

    public LinksCollection(String id, String name) {
        super(id, name);
    }

    public LinksCollection(JSONObject jLinksCollection) {
        super(jLinksCollection);
    }

}
