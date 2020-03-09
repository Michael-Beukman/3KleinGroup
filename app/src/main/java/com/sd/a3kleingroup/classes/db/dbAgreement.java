package com.sd.a3kleingroup.classes.db;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class dbAgreement implements dbObject{
    private String fileID;
    private String userID;
    private Date ValidUntil;

    /**
     *
     * @param fileID The ID of the File in the DB
     * @param userID The user that should receive the File
     * @param validUntil The Date until the agreement is valid
     */
    public dbAgreement(String fileID, String userID, Date validUntil) {
        this.fileID = fileID;
        this.userID = userID;
        ValidUntil = validUntil;
    }
    /**
     * Creates a valid until for 20 years in the future.
     * @param fileID The ID of the File in the DB
     * @param userID The user that should receive the File
     */
    public dbAgreement(String fileID, String userID) {
        this.fileID = fileID;
        this.userID = userID;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, 20);
        ValidUntil = c.getTime();
    }


    /**
     * Returns a hashmap, that can easily be used to upload the object.
     * @return
     */
    public HashMap<String, Object> getHashmap(){
        return new HashMap<String, Object>(){{
            put("userID", userID);
            put("fileID", fileID);
            put("validUntil", ValidUntil);
        }};
    }
}
