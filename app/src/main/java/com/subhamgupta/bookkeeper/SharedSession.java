package com.subhamgupta.bookkeeper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedSession {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private boolean darkTheme, lock, demoMain, demoAddBook, emailVerified;
    private String username;
    private String email;
    private String accountType;
    private String uuid;

    public SharedSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void setData(String username, String email, String accountType ){
        this.email = email;
        this.username = username;
        this.accountType = accountType;
        editor.putString("username",username);
        editor.putString("email",email);
        editor.putString("acctype",accountType);
        editor.commit();
    }

    public boolean isEmailVerified() {
        emailVerified = sharedPreferences.getBoolean("emailVerified", false);
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
        editor.putBoolean("emailVerified", emailVerified);
        editor.commit();
    }

    public String getEmail() {
        email = sharedPreferences.getString("email","");
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getData(){
        Map<String, String> data = new HashMap<>();
        data.put("username",sharedPreferences.getString("username","null"));
        data.put("email",sharedPreferences.getString("email","null"));
        data.put("acctype",sharedPreferences.getString("acctype","null"));
        return data;
    }
    public void logOut(){
        editor.clear();
        editor.apply();
    }

    public boolean isDemoMain() {
        return sharedPreferences.getBoolean("demoMain",true);
    }

    public void setDemoMain(boolean demoMain) {
        this.demoMain = demoMain;
        editor.putBoolean("demoMain",demoMain);
        editor.commit();
    }

    public boolean isDemoAddBook() {
        return sharedPreferences.getBoolean("demoAddbook",true);
    }

    public void setDemoAddBook(boolean demoAddBook) {
        this.demoAddBook = demoAddBook;
        editor.putBoolean("demoAddbook",demoAddBook);
        editor.commit();
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
        editor.putBoolean("theme",darkTheme);
        editor.commit();
    }

    public String getUuid() {
        uuid = sharedPreferences.getString("uuid","null");
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
        editor.putString("uuid",uuid);
        editor.commit();
    }

    public boolean isDarkTheme() {
        darkTheme = sharedPreferences.getBoolean("theme",true);
        return darkTheme;
    }

    public boolean isLock() {
        lock = sharedPreferences.getBoolean("theme",true);
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
        editor.putBoolean("lock",lock);
        editor.commit();
    }
}
