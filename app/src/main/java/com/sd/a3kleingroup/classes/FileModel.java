package com.sd.a3kleingroup.classes;

import android.net.Uri;

public class FileModel {

    String fileName;
    String format;
    String url;

    public FileModel(String fileName, String format, String url) {
        this.fileName = fileName;
        this.format=format;
        this.url = url;
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

}
