package com.aaish.tushare;

/**
 * Created by aaishsindwani on 14/09/16.
 */
public class Uploaddbreference {
    private String filename;
    private String subject;
    private String type;
    private String userId;
    private String username;
    private String link;


    public Uploaddbreference() {


    }

    public Uploaddbreference(String f, String s, String t, String usid, String uname, String l) {

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
