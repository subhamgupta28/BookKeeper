package com.subhamgupta.bookkeeper;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class SplashScreen extends AppCompatActivity {
    ImageView imageView;
    TextView splashtext;
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


        new Handler().postDelayed(() -> {
            if (currentUser!=null && currentUser.isEmailVerified()){
                HomeActivitty();
            }else {
                LoginAvtivity();
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