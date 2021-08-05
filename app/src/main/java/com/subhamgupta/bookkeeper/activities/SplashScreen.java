package com.subhamgupta.bookkeeper.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.subhamgupta.bookkeeper.R;
import com.subhamgupta.bookkeeper.dataclasses.SharedSession;

import java.io.File;


public class SplashScreen extends AppCompatActivity {
    ImageView imageView;
    TextView splashtext;
    RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;
    int MY_REQUEST_CODE =200;
    SharedSession sharedSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        imageView = findViewById(R.id.splashimage);
        splashtext = findViewById(R.id.splashtextview);
        relativeLayout = findViewById(R.id.splashre);


        sharedSession = new SharedSession(getApplicationContext());
        if (!sharedSession.getData().isEmpty()){
            if (sharedSession.isDarkTheme()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                relativeLayout.setBackgroundColor(Color.BLACK);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        }
        try {
            createPublicFolders();
        }catch (Exception e){
            Log.e("ERROR",e.getMessage());
        }

        new Handler().postDelayed(() -> {
            if (currentUser!=null && currentUser.isEmailVerified()){
                HomeActivitty();
            }else {
                LoginAvtivity();
            }

        }, 3000);

    }
    public void createPublicFolders(){
        File fl = new File(this.getFilesDir(),"/BookKeeper/");
        if(!fl.exists())
            Log.e("FOLDER_BOOKKEEPER", String.valueOf(fl.mkdir()));
        fl = new File(this.getFilesDir(),"/Shared Books/");
        if(!fl.exists())
            Log.e("FOLDER_SHAREDBOOKS", String.valueOf(fl.mkdir()));
        fl = new File(this.getFilesDir(),"/Buffer/");
        if(!fl.exists())
            Log.e("FOLDER_BUFFER", String.valueOf(fl.mkdir()));
    }


    public void LoginAvtivity(){
        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
        startActivity(intent);
        finish();
    }
    public void HomeActivitty(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}