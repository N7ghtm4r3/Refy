package com.tecknobit.refycore.records;

import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

public class RefyLink extends EquinoxItem {

    public static final String TITLE_KEY = "title_key";

    public static final String REFERENCE_LINK_KEY = "reference_link";

    private final String title;

    private final String referenceLink;

    public RefyLink(String id, String title, String referenceLink) {
        super(id);
        this.title = title;
        this.referenceLink = referenceLink;
    }

    public RefyLink(JSONObject jRefyLink) {
        super(jRefyLink);
        this.title = hItem.getString(TITLE_KEY);
        this.referenceLink = hItem.getString(REFERENCE_LINK_KEY);
    }

    public String getTitle() {
        return title;
    }

    public String getReferenceLink() {
        return referenceLink;
    }

}
