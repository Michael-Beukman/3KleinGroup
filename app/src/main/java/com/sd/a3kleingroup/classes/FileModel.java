package com.sd.a3kleingroup.classes;

import android.net.Uri;

import com.sd.a3kleingroup.classes.db.*;

public class FileModel {

    String fileName;
    String format;
    String path;
    String url;
    dbAgreement agreement;
    dbUser owner;

    public FileModel(){ }

    public FileModel(String fileName, String format,  String path, String url) {
        this.fileName = fileName;
        this.format=format;
        this.path = path;
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getFormat() {
        return format;
    }

    public void setFormat(String format){
        this.format = format;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public dbAgreement getAgreement() {
        return agreement;
    }

    public void setAgreement(dbAgreement agreement) {
        this.agreement = agreement;
    }

    public dbUser getOwner() {
        return owner;
    }

    public void setOwner(dbUser owner) {
        this.owner = owner;
    }
}
