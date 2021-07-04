package com.subhamgupta.bookkeeper;

import android.text.Spannable;

public class ReadBookModel {
    private String chapter;
    private String description;
    private long pageNo;

    public ReadBookModel(String chapter, String description, long pageNo) {
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
