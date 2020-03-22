package com.sd.a3kleingroup.classes.db;


import java.util.HashMap;
import java.util.Map;

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

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    @Override
    public Map<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("name", name);
            put("email", email);
//            put("notificationToken", notificationToken);
        }};
    }
}
