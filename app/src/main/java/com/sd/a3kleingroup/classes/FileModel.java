package com.sd.a3kleingroup.classes;

import android.net.Uri;

import com.sd.a3kleingroup.classes.db.*;

public class FileModel {

    String fileName;
    String format;
    String path;
    String url;
    String encryptionKey;
    dbAgreement agreement;
    dbUser owner;

    public FileModel(){
        fileName=null;
        format=null;
        path=null;
        url=null;
        agreement=null;
        owner=null;
    }

    public FileModel(String fileName, String format,  String path, String url) {
        this.fileName = fileName;
        this.format=format;
        this.path = path;
        this.url = url;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
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

    public boolean isAllDataRetrieved(){
        return !(fileName==null||url==null||path==null||agreement==null||owner==null);
    }
}
