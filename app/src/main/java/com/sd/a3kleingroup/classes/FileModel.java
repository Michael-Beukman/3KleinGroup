package com.sd.a3kleingroup.classes;

import android.net.Uri;

public class FileModel {

    String fileName;
    String format;
    Uri uri;

    public FileModel(String fileName, String format, Uri uri) {
        this.fileName = fileName;
        this.format=format;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri url) {
        this.uri = url;
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
