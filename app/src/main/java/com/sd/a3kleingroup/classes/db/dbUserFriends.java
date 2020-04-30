package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class dbUserFriends implements dbObject {
    private String userID;
    private boolean accepted;
    private String friendID;

    //Maybe possible later when you click on add a friend we can take some of that info from your fields for
    //add friend Guy ?

    /**
     *
     * @param userID= this user's ID
     * @param accepted =  status of friends
     * @param friendID = the friend's ID
     */
    public dbUserFriends(String userID, boolean accepted, String friendID){
        this.userID = userID;
        this.accepted = accepted;
        this.friendID = friendID;

    }

    public String getUserID(){ return userID; }

    public boolean getAccepted(){ return accepted; }

    public String getFriendID(){ return friendID; }

    @Override
    public Map<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("accepted", accepted);
            put("temp name2", userID);
            put("temp name3", friendID);
        }};
    }
}