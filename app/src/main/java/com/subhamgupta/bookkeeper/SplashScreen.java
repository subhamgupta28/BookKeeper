package com.subhamgupta.bookkeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreen extends AppCompatActivity {
    ImageView imageView;
    TextView splashtext;
    private FirebaseAuth mAuth;
    SharedSession sharedSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        imageView = findViewById(R.id.splashimage);
        splashtext = findViewById(R.id.splashtextview);

        //splashtext.setText("Book Keeper");
        /*Glide.with(imageView.getContext())
                .load(R.drawable.icon)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);*/
        sharedSession = new SharedSession(getApplicationContext());
        if (!sharedSession.getData().isEmpty()){
            if (sharedSession.isDarkTheme()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        }
//        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
//            sharedSession.setDarkTheme(true);
//        }
//        else sharedSession.setDarkTheme(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser!=null){
                    HomeActivitty();
                }else {
                    LoginAvtivity();
                }

            }
        }, 3000);

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