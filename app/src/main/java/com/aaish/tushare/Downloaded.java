package com.aaish.tushare;

import android.net.Uri;

/**
 * Created by aaishsindwani on 30/09/16.
 */
public class Downloaded {
    private String filename;
    private Uri fileuri;
    private String subject;
    private String type;
    private String username;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Downloaded() {

    }

    public Downloaded(String filename, Uri fileuri, String subject, String type, String username) {

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Uri getFileuri() {
        return fileuri;
    }

    public void setFileuri(Uri fileuri) {
        this.fileuri = fileuri;
    }
}
