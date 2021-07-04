package com.subhamgupta.bookkeeper;

public interface OnItemClick {
     void onClickItem(String title, String author, String link);
     void onLongClickItem(String title, String author, String link);
}
