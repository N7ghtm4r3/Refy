package com.tecknobit.refycore.records;

import com.tecknobit.equinox.environment.records.EquinoxItem;
import org.json.JSONObject;

import static com.tecknobit.equinox.environment.records.EquinoxUser.NAME_KEY;

public class Team extends RefyItem {

    public Team(String id, String name) {
        super(id, name);
    }

    public Team(JSONObject jTeam) {
        super(jTeam);
    }

}
