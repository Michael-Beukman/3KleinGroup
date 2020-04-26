package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;
import java.util.Map;

public class dbPublicFiles implements dbObject{
    /**
     * Need the following info:
     * userID - to see which user it is w.r.t to the public file. Used to also see if a user has set it up
     * file name - to see which files there are.
     * file storage - URL to retrieve it from on firebase
     * file path - path that it is stored on in firebase
     *
     * TO DO: encryption keys
     *
     */

    // two options
    // 1) reuse the dbFile code and implement it as an extension of the file class.
    // 2) implement separately.
    // will do 2) for now but consider integrate the two later or extending
    //partially due to the fact that originally envisioned friend id being a part of it but probably a better idea to use friend bool to request files and just transfer database info

    private String userID;
    private String fileName;
    private String fileStorage;
    private String filePath;
    private String encryptionKey = ""; //Check later for now just leave as "" to indicate no key just yet

    public dbPublicFiles(String userID, String fileName, String fileStorage, String filePath, String encryptionKey ){
        this.userID = userID;
        this.fileName = fileName;
        this.fileStorage = fileStorage;
        this.filePath = filePath;
        this.encryptionKey = encryptionKey;
    }

    @Override
    public Map<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("file_path", filePath);
            put("file_name", fileName);
            put("user_id", userID);
            put("file_storage", fileStorage);
            put("encryptionKey", encryptionKey);
        }};
    }

    public String getFileName() { return fileName; }

    public String getFileStorage() { return fileStorage; }

    public String getFilePath() { return filePath; }

    public String getUserID() { return userID; }

    private String getEncryptionKey () { return encryptionKey; }


}
