package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;
import java.util.Map;

public class dbFriends implements dbObject {
    private String recipientID;
    private String senderID;
    private boolean accepted;

    public dbFriends(String recipientID, String senderID, boolean accepted) {
        this.recipientID = recipientID;
        this.senderID = senderID;
        this.accepted = accepted;
    }

    @Override
    public Map<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("recipientID", recipientID);
            put("senderID", senderID);
            put("accepted", accepted);
        }};
    }
}
