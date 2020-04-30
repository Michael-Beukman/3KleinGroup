package com.sd.a3kleingroup.classes;

import android.net.Uri;

import java.io.File;

import kotlin.NotImplementedError;

//Only added for a few things,
public class PublicFile {

    //order this comes across in firebase
    private String encryptionkey;
    private String filename;
    private String filepath;
    private Uri fileuri;
    private String userid;


    // actual File contents and everything
    private File file;

    //done in the same order as it is in firebase currently
    public PublicFile(String encryptionkey, String filename, String filepath, Uri fileuri, String userid) {
        this.encryptionkey = encryptionkey;
        this.filename = filename;
        this.filepath = filepath;
        this.fileuri = fileuri;
        this.userid = userid;
    }

    /**
     * Returns the file matching the filepath
     * @return
     */

    public String getEncryptionkey() {
        return encryptionkey;
    }

    public void setEncryptionkey(String encryptionkey) {
        this.encryptionkey = encryptionkey;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Uri getFileuri() {
        return fileuri;
    }

    public void setFileuri(Uri fileuri) {
        this.fileuri = fileuri;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

