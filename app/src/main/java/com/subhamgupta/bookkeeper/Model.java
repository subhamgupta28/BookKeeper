package com.subhamgupta.bookkeeper;

public class Model {
     private String TITLE;
     private String AUTHOR;
     private String IMAGELINK;
     private String BOOKREF;
     private String IMAGENAME;
     private String DESCRIPTION;
     private String KEY;
     private String FILELINK;
     private long RATINGS;
     private int RATING_COUNT;


    public Model(String TITLE, String AUTHOR, String IMAGELINK, String BOOKREF, String IMAGENAME, String DESCRIPTION, String KEY, long RATINGS, int RATING_COUNT) {
        this.TITLE = TITLE;
        this.AUTHOR = AUTHOR;
        this.IMAGELINK = IMAGELINK;
        this.BOOKREF = BOOKREF;
        this.IMAGENAME = IMAGENAME;
        this.DESCRIPTION = DESCRIPTION;
        this.KEY = KEY;
        this.RATINGS = RATINGS;
        this.RATING_COUNT = RATING_COUNT;
    }

    public Model(){

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
