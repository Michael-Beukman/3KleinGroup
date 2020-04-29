package com.sd.a3kleingroup.classes;

import android.net.Uri;

import java.io.File;

import kotlin.NotImplementedError;

//Only added for a few things,
public class PublicFile {
    private String filepath = "";
    private String filename = "" ;
    private Uri fileUri;

    // actual File contents and everything
    private File file;

    public PublicFile(){ }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Uploads the file to firebase.
     * @param userIDSent
     * @param userIDReceive
     */
    public void uploadToFirebase(String userIDSent, String userIDReceive){
        throw new NotImplementedError();
    }

    /**
     * Returns the file matching the filepath
     * @return
     */
    public File getFile(){
        return new File(filepath);
    }

    public void setUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public Uri getUri() {
        return fileUri;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }
}

