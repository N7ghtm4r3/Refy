package com.tecknobit.refycore.records;

import org.json.JSONObject;

import java.util.List;

public class RefyLink extends RefyItem implements RefyItem.ListScreenItem {

    public static final String REFERENCE_LINK_KEY = "reference_link";

    private final String referenceLink;

    private final List<Team> teams;

    private final List<LinksCollection> collections;

    public RefyLink() {
        this(null, null, null, null, null, List.of(), List.of());
    }

    //TODO: TO REMOVE
    public RefyLink(String id, String title, String description, String referenceLink) {
        super(id,
                new RefyUser(
                        "GEGWEGWHWHG",
                        "Greg",
                        "Godzilla",
                        "greg@godzilla",
                        "https://media-assets.wired.it/photos/64f6faa946c2835bd21c9fd3/4:3/w_2880,h_2160,c_limit/ezgif-3-f91e25fbf3.jpg",
                        "@godzilla"
                ),
                title,
                description);
        this.referenceLink = referenceLink;
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of(
                new Team("id", "Ciao", new RefyUser("h"), "https://cdn.mos.cms.futurecdn.net/9UmWCbyxpKaEGXjwFG7dXo-1200-80.jpg", "*Lorem* ipsum dolor sit amet, consectetur adipiscing elit. Duis non turpis quis leo pharetra ullamcorper. Fusce ut justo egestas, consectetur ipsum eget, suscipit felis. Vivamus sodales iaculis ligula vitae pretium. Suspendisse interdum varius sem, sed porta elit hendrerit sed. Suspendisse accumsan auctor lectus a venenatis. Maecenas id fermentum leo. Praesent aliquam sagittis aliquam.")//,
                //new Team("id2", "Ciao2")
        );
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of(
                //new LinksCollection("id", "Ciao")
        );
    }

    public RefyLink(String id, RefyUser owner, String title, String description, String referenceLink,
                    List<Team> teams, List<LinksCollection> collections) {
        super(id, owner, title, description);
        this.referenceLink = referenceLink;
        this.teams = teams;
        this.collections = collections;
    }

    public RefyLink(JSONObject jRefyLink) {
        super(jRefyLink);
        referenceLink = hItem.getString(REFERENCE_LINK_KEY);
        //TODO: TO LOAD CORRECTLY
        this.teams = List.of();
        //TODO: TO LOAD CORRECTLY
        this.collections = List.of();
    }

    public String getReferenceLink() {
        return referenceLink;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<LinksCollection> getCollections() {
        return collections;
    }

    @Override
    public boolean canBeUpdatedByUser(String loggedUserId) {
        return loggedUserId.equals(owner.getId()) || teams.isEmpty();
    }

}
