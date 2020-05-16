package com.sd.a3kleingroup.classes.db;

import java.util.ArrayList;

public class dbPublicFileManager extends dbPublicFiles{
    private ArrayList<String> viewers = new ArrayList<>();

    public dbPublicFileManager(String encryptionKey, String fileName, String filePath, String fileStorage, String userID, ArrayList<String> viewers){
        super(encryptionKey,fileName,filePath,fileStorage,userID);
        this.viewers = viewers;
    }

    public void setViewers(ArrayList<String> theViewers){
        this.viewers = theViewers;
    }

    public ArrayList<String> getViewers() {
        return viewers;
    }
}
