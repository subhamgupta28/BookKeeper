package com.subhamgupta.bookkeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    TextView email, name, accinfo;
    ImageView profileimg;
    GoogleSignInAccount mGoogleSignInAccount;
    JsonHelper jsonHelper;
    MaterialCardView materialCardView;
    MaterialCardView theme, lock, backup, logoutu;
    SharedSession sharedSession;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Map<String, String> data;
    FirebaseAuth auth;
    boolean isdark = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        theme = findViewById(R.id.theme);
        lock = findViewById(R.id.lock);
        backup = findViewById(R.id.allbackup);
        logoutu = findViewById(R.id.logout);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        materialCardView = findViewById(R.id.setingcard);
        accinfo = findViewById(R.id.accountinfo);
        profileimg = findViewById(R.id.profileimg);

        sharedSession = new SharedSession(getApplicationContext());
        jsonHelper = new JsonHelper();
        data = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (mGoogleSignInAccount!=null) {
            email.setText(mGoogleSignInAccount.getEmail());
            name.setText(mGoogleSignInAccount.getDisplayName());
            Glide.with(getApplicationContext())
                    .load(mGoogleSignInAccount.getPhotoUrl())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .centerCrop()
                    .into(profileimg);
        }
        else {
            accinfo.setText("Sign in with google to see account details");
            //materialCardView.setVisibility(View.GONE);
            data = sharedSession.getData();
            System.out.println(data);
            email.setText(data.get("username"));
            //name.setText(data.get("email"));
        }
        isdark = sharedSession.isDarkTheme();//false
        Log.e("theme", String.valueOf(isdark));


        theme.setOnClickListener(view -> {
            Log.e("themeon", String.valueOf(isdark));
            isdark = !isdark;

            if (isdark){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedSession.setDarkTheme(true);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedSession.setDarkTheme(false);
            }

        });
        logoutu.setOnClickListener(view -> {
            startActivity(new Intent(this, Editor.class));
            /*mGoogleSignInAccount = null;
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginPage.class));
            editor.clear();
            data = sharedSession.getData();
            System.out.println(data);
            finish();*/
        });


    }
}