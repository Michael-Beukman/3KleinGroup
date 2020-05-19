package com.sd.a3kleingroup.classes;

import com.sd.a3kleingroup.classes.db.dbUser;

public class FriendRequest {
    private dbUser potentialFriend;
    private String requestID;

    public dbUser getPotentialFriend() {
        return potentialFriend;
    }

    public void setPotentialFriend(dbUser potentialFriend) {
        this.potentialFriend = potentialFriend;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
}
