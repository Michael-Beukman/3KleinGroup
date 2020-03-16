package com.sd.a3kleingroup.classes;

import java.util.Date;

public class SingleSentFile {
    private String docID;
    private String fileID;
    private String userID;
    private Date validUntil;
    private String ownerID;

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public SingleSentFile(){}

    public SingleSentFile(String fileID, String userID, Date validUntil, String ownerID, String docID) {
        this.fileID = fileID;
        this.userID = userID;
        this.validUntil = validUntil;
        this.ownerID = ownerID;
        this.docID = docID;
    }

    public String getDocID() {
        return docID;
    }
}
