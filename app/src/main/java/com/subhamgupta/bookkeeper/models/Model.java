package com.subhamgupta.bookkeeper.models;


import java.io.Serializable;
import java.util.Date;

public class Model implements Serializable {

    String TITLE;
    String AUTHOR;
    String IMAGELINK;
    String BOOKREF;
    String UPLOAD_TIME;
    String IMAGENAME;
    String DESCRIPTION;
    String KEY;
    String FILELINK;
    long RATINGS;
    int RATING_COUNT;
    boolean PUBLISHED;
    private Date time;


    public Model(String TITLE,
                 String AUTHOR,
                 String IMAGELINK,
                 String BOOKREF,
                 String UPLOAD_TIME,
                 String IMAGENAME,
                 String DESCRIPTION,
                 String KEY,
                 String FILELINK,
                 long RATINGS,
                 int RATING_COUNT,
                 boolean PUBLISHED

    ) {
        this.TITLE = TITLE;
        this.AUTHOR = AUTHOR;
        this.IMAGELINK = IMAGELINK;
        this.BOOKREF = BOOKREF;
        this.UPLOAD_TIME = UPLOAD_TIME;
        this.IMAGENAME = IMAGENAME;
        this.DESCRIPTION = DESCRIPTION;
        this.KEY = KEY;
        this.FILELINK = FILELINK;
        this.RATINGS = RATINGS;
        this.RATING_COUNT = RATING_COUNT;
        this.PUBLISHED = PUBLISHED;

    }

    Model(){

    }

    public String getUPLOAD_TIME() {
        return UPLOAD_TIME;
    }

    public void setUPLOAD_TIME(String UPLOAD_TIME) {
        this.UPLOAD_TIME = UPLOAD_TIME;
    }

    public boolean isPUBLISHED() {
        return PUBLISHED;
    }

    public void setPUBLISHED(boolean PUBLISHED) {
        this.PUBLISHED = PUBLISHED;
    }

    public String getFILELINK() {
        return FILELINK;
    }
    public void setFILELINK(String FILELINK) {
        this.FILELINK = FILELINK;
    }
    public String getKEY() {
        return KEY;
    }
    public void setKEY(String KEY) {
        this.KEY = KEY;
    }
    public int getRATING_COUNT() {
        return RATING_COUNT;
    }
    public void setRATING_COUNT(int RATING_COUNT) {
        this.RATING_COUNT = RATING_COUNT;
    }
    public String getDESCRIPTION() {
        return DESCRIPTION;
    }
    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }
    public long getRATINGS() {
        return RATINGS;
    }
    public void setRATINGS(long RATINGS) {
        this.RATINGS = RATINGS;
    }
    public String getIMAGENAME() {
        return IMAGENAME;
    }
    public void setIMAGENAME(String IMAGENAME) {
        this.IMAGENAME = IMAGENAME;
    }
    public String getBOOKREF() {
        return BOOKREF;
    }
    public void setBOOKREF(String BOOKREF) {
        this.BOOKREF = BOOKREF;
    }
    public String getTITLE() {
        return TITLE;
    }
    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }
    public String getAUTHOR() {
        return AUTHOR;
    }
    public void setAUTHOR(String AUTHOR) {
        this.AUTHOR = AUTHOR;
    }
    public String getIMAGELINK() {
        return IMAGELINK;
    }
    public void setIMAGELINK(String IMAGELINK) {
        this.IMAGELINK = IMAGELINK;
    }
}
