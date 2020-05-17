package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;
import java.util.Map;

public class dbFile implements dbObject{
    private String filepath;
    private String name;
    private String userID;
    private String storageURL;

    private String encryptionKey;
    private String fileType;

    /**
     *
     * @param filepath The path that the file is stored on, in firebase
     * @param name The 'pretty' name of the file
     * @param userID UserID of the file owner
     * @param storageURL The url that the file can be downloaded from.
     * @param encryptionKey The key used for encryption
     */
    public dbFile(String filepath, String name, String userID, String storageURL, String encryptionKey, String fileType) {
        this.filepath = filepath;
        this.name = name;
        this.userID = userID;
        this.storageURL = storageURL;
        this.encryptionKey = encryptionKey;
        this.fileType = fileType;
    }

    /**
     * Same as the above, with filetype="application/pdf"
     */
    public dbFile(String filepath, String name, String userID, String storageURL, String encryptionKey) {
        this(filepath, name, userID, storageURL, encryptionKey, "application/pdf");
    }

    @Override
    public Map<String, Object> getHashmap() {
        return new HashMap<String, Object>(){{
            put("filepath", filepath);
            put("filename", name);
            put("userID", userID);
            put("storageURL", storageURL);
            put("encryptionKey", encryptionKey);
            put("fileType", fileType);
        }};
    }

    public String getName() {
        return name;
    }
}
