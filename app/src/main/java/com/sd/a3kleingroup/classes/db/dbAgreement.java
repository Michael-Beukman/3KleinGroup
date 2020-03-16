package com.sd.a3kleingroup.classes.db;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class dbAgreement implements dbObject{
    private String fileID;
    private String userID;
    private Date ValidUntil;
    private String userSentID;

    /**
     * Returns a date 20 years in future
     * @return
     */
    public static Date getDate20YearsInfuture(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, 20);
        return c.getTime();
    }

    /**
     * Returns a date 1 year in the past
     * @return
     */
    public static Date getDateInPast(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, -1);
        return c.getTime();
    }

    /**
     * Returns a date tomorrow
     * @return
     */
    public static Date getDateTomorrow(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.HOUR, 24);
        return c.getTime();
    }
    /**
     *
     * @param fileID The ID of the File in the DB
     * @param userID The user that should receive the File
     * @param validUntil The Date until the agreement is valid
     * @param userSentID The user that sent it
     */
    public dbAgreement(String fileID, String userID, Date validUntil, String userSentID) {
        this.fileID = fileID;
        this.userID = userID;
        ValidUntil = validUntil;
        this.userSentID = userSentID;
    }
    /**
     * Creates a valid until for 20 years in the future.
     * @param fileID The ID of the File in the DB
     * @param userID The user that should receive the File
     */
    public dbAgreement(String fileID, String userID, String userSentID) {
        this.fileID = fileID;
        this.userID = userID;
        this.userSentID = userSentID;


        ValidUntil = dbAgreement.getDate20YearsInfuture();
    }


    /**
     * Returns a hashmap, that can easily be used to upload the object.
     * @return
     */
    public Map<String, Object> getHashmap(){
        return new HashMap<String, Object>(){{
            put("userID", userID);
            put("fileID", fileID);
            put("validUntil", ValidUntil);
            put("ownerID", userSentID);
        }};
    }
}
