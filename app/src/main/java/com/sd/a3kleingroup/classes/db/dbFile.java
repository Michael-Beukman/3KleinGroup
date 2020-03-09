package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;

public class dbFile implements dbObject{
    private String filepath;
    private String name;
    private String userID;
    private String storageURL;

    /**
     *
     * @param filepath The path that the file is stored on, in firebase
     * @param name The 'pretty' name of the file
     * @param userID UserID of the file owner
     * @param storageURL The url that the file can be downloaded from.
     */
    public dbFile(String filepath, String name, String userID, String storageURL) {
        this.filepath = filepath;
        this.name = name;
        this.userID = userID;
        this.storageURL = storageURL;
    }


    @Override
    public HashMap<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("filepath", filepath);
            put("filename", name);
            put("userID", userID);
            put("storageURL", storageURL);
        }};
    }
}
