package com.sd.a3kleingroup.classes.db;


import java.util.HashMap;

public class dbUser implements dbObject{
    private String email, name, notificationToken;

    public dbUser(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public dbUser(String email, String name, String notificationToken){
        this.email = email;
        this.name = name;
        this.notificationToken = notificationToken;
    }

    @Override
    public HashMap<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("name", name);
            put("email", email);
//            put("notificationToken", notificationToken);
        }};
    }
}
