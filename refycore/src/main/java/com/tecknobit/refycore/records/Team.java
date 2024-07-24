package com.tecknobit.refycore.records;

import org.json.JSONObject;

import java.util.List;

public class Team extends RefyItem {

    public static final int MAX_MEMBERS_DISPLAYED = 5;

    private final List<RefyUser> members;

    public Team() {
        this(null, null, List.of());
    }

    //TODO: TO REMOVE
    public Team(String id, String name) {
        super(id, name);
        this.members = List.of(
                new RefyUser(
                        "id",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://t4.ftcdn.net/jpg/03/86/82/73/360_F_386827376_uWOOhKGk6A4UVL5imUBt20Bh8cmODqzx.jpg",
                        "@id"
                ),
                new RefyUser(
                        "id3213",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id3213"
                ),
                new RefyUser(
                        "id2",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2"
                ),
                new RefyUser(
                        "idwgewgw2",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2"
                ),
                new RefyUser(
                        "igwegwgwegwegwegewgd2",
                        "User",
                        "LAST",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2"
                ),
                new RefyUser(
                        "iegwgwed2",
                        "User",
                        "One",
                        "p@gmail.com",
                        "https://images.photowall.com/products/56987/outer-space-4.jpg?h=699&q=85",
                        "@id2"
                )
        );
    }

    public Team(String id, String name, List<RefyUser> members) {
        super(id, name);
        this.members = members;
    }

    public Team(JSONObject jTeam) {
        super(jTeam);
        //TODO: TO LOAD CORRECTLY
        members = List.of();
    }

    public List<RefyUser> getMembers() {
        return members;
    }

}
