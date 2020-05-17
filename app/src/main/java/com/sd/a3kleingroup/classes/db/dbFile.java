package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;
import java.util.Map;

public class dbFile implements dbObject{
    protected String filepath;
    protected String name;
    protected String userID;
    protected String storageURL;

    protected String encryptionKey;
    protected String fileType;

    public static final String PDF_TYPE="application/pdf";

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
        this(filepath, name, userID, storageURL, encryptionKey, PDF_TYPE);
    }

    /**
     * This makes the file from the hashmap from firebase.
     * @param data
     */
    public dbFile(Map<String, Object> data){
        try{
            filepath = (String)data.getOrDefault("filepath", "");
            name = (String)data.getOrDefault("filename", "");
            userID = (String)data.getOrDefault("userID", "");
            storageURL = (String)data.getOrDefault("storageURL", "");
            encryptionKey = (String)data.getOrDefault("encryptionKey", "");
            // if we don't have a filetype, make it application/pdf.
            fileType = (String)data.getOrDefault("fileType", PDF_TYPE);
        }catch (Exception e){
            this.filepath = "";
            this.name = "";
            this.userID = "";
            this.storageURL = "";
            this.encryptionKey = "";
            this.fileType = PDF_TYPE;
        }
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

    public String getFileName() {
        return name;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getUserID() {
        return userID;
    }

    public String getStorageURL() {
        return storageURL;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public String getFileType() {
        return fileType;
    }
}
