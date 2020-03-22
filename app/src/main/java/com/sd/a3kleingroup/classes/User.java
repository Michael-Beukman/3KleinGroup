package com.sd.a3kleingroup.classes;

import com.sd.a3kleingroup.classes.db.dbUser;

public class User extends dbUser {
    private String id;

    public User(String email, String name, String notificationToken, String id) {
        super(email, name, notificationToken);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
