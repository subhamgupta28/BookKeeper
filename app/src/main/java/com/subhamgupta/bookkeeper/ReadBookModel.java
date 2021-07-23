package com.subhamgupta.bookkeeper;


import java.io.Serializable;
import java.util.Date;

public class ReadBookModel implements Serializable {
     String chapter;
     String description;
     long pageNo;
     private Date date;

     ReadBookModel() {
    }

    ReadBookModel(String chapter, String description, long pageNo) {
        this.chapter = chapter;
        this.description = description;
        this.pageNo = pageNo;
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
