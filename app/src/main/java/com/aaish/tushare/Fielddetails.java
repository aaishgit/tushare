package com.aaish.tushare;

/**
 * Created by aaishsindwani on 09/09/16.
 */
public class Fielddetails {
    private String name;
    private String type;
    private String dept;
    private String year;


    //Compulsory for firebase
    public Fielddetails() {

    }


    public Fielddetails(String b, String a, String p, String pr) {

    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDept() {
        return dept;
    }

    public String getYear() {
        return year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
