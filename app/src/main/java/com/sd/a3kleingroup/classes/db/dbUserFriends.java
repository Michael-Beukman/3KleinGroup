package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class dbUserFriends implements dbObject {
    private String userEmail;
    private String friendName;
    private String friendEmail;

    //Maybe possible later when you click on add a friend we can take some of that info from your fields for
    //add friend Guy ?

    /**
     *
     * @param userEmail = this user's email
     * @param friendName = the friend's name
     * @param friendEmail = the friend's email
     */
    public dbUserFriends(String userEmail, String friendName, String friendEmail, String userName){
        this.userEmail = userEmail;
        this.friendName = friendName;
        this.friendEmail = friendEmail;

    }

    public String getUserEmail(){ return userEmail; }

    public String getFriendName(){ return friendName; }

    public String getFriendEmail(){ return friendEmail; }

    @Override
    public Map<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("temp name1", userEmail);
            put("temp name2", friendName);
            put("temp name3", friendName);
        }};
    }
}