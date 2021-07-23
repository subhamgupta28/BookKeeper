package com.subhamgupta.bookkeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Settings extends AppCompatActivity {

    TextView email, name, accinfo, backuptext, restoretext;
    ImageView profileimg;
    GoogleSignInAccount mGoogleSignInAccount;

    JsonHelper jsonHelper;
    MaterialCardView materialCardView;
    SwitchMaterial theme, lock;
    Button backup, restore;
    MaterialButton shareapp;
    SharedSession sharedSession;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Map<String, String> data;
    FirebaseAuth auth;
    ProgressBar progressBar;
    boolean isdark = false;
    StorageReference storageRef;
    DatabaseReference ref, ref2;
    List<String> children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        theme = findViewById(R.id.theme);
        lock = findViewById(R.id.lock);
        backup = findViewById(R.id.allbackup);
        backuptext = findViewById(R.id.backuptext);
        progressBar = findViewById(R.id.progressBar);
        restore = findViewById(R.id.allrestore);
        restoretext = findViewById(R.id.restoretext);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        materialCardView = findViewById(R.id.setingcard);
        accinfo = findViewById(R.id.accountinfo);
        profileimg = findViewById(R.id.profileimg);
        shareapp = findViewById(R.id.shareapp);
        storageRef = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference().child("BOOKDATA");
        ref2 = ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        sharedSession = new SharedSession(getApplicationContext());
        jsonHelper = new JsonHelper();
        data = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (true) {
            email.setText(auth.getCurrentUser().getEmail());
            name.setText(auth.getCurrentUser().getDisplayName());
            Glide.with(getApplicationContext())
                    .load(auth.getCurrentUser().getPhotoUrl())
                   .thumbnail(0.005f)
                    .placeholder(R.drawable.ic_author)
                    .centerCrop()
                    .into(profileimg);
        }
        else {
            //accinfo.setText("Sign in with google to see account details");
            //materialCardView.setVisibility(View.GONE);
            data = sharedSession.getData();
            System.out.println(data);
            email.setText(data.get("username"));
            //name.setText(data.get("email"));
        }
        isdark = sharedSession.isDarkTheme();//false
        Log.e("theme", String.valueOf(isdark));
        theme.setChecked(isdark);
        theme.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedSession.setDarkTheme(true);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedSession.setDarkTheme(false);
            }
        });
        shareapp.setOnClickListener(view -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Keeper");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                startActivity(Intent.createChooser(shareIntent, "Share to..."));
            } catch(Exception e) {
                //e.toString();
            }
        });


        List<String> child = prepareBackup();
        System.out.println("child"+child);
        if (!child.isEmpty()){
            backuptext.setText("Found " + child.size() + " On device books");
            backup.setEnabled(true);
            backup.setVisibility(View.VISIBLE);
            backup.setOnClickListener(view -> {
                upload(child);
            });
        }
        else{
            backuptext.setText("");
            backup.setVisibility(View.GONE);
        }
         prepareRestore();

    }
    public void prepareRestore(){
        List<String> childs = new ArrayList<>();

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    childs.add(dataSnapshot.getKey()+".json");
                    Log.e("Setting Restore", dataSnapshot.getKey());
                }
                System.out.println("childs"+childs);
                if (!childs.isEmpty()){
                    restore.setVisibility(View.VISIBLE);
                    restoretext.setText("Found "+ childs.size()+" Book on cloud");
                }
                else {
                    restore.setVisibility(View.VISIBLE);
                    restoretext.setText("");
                }
                children = childs;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public List<String> prepareBackup(){
        List<String> children = new ArrayList<>();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                File.separator + "BookKeeper/");
        if (file.isDirectory()) {
            children = Arrays.asList(file.list());

        }
        else {
            backup.setEnabled(false);
            backup.setVisibility(View.GONE);

        }
        return children;
    }
    public void saveForOfflineReading(String json){
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    File.separator + "BookKeeper/");
            if(!dir.exists())
                dir.mkdir();
            else{
                File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        File.separator + "SavedFile"+".txt");

                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(json);
                myOutWriter.close();
                fOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("err",e.getMessage());
        }

    }
    public void upload(List<String> filesList){
        backup.setEnabled(false);
       for (int i=0;i<filesList.size();i++)
        {
            String filename = filesList.get(i).substring(0, 10);
            int count =i;
            InputStream stream;
            try {
                //stream = new FileInputStream(new File("/sdcard/BookKeeper/"+key+".json"));
                stream = new FileInputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        File.separator + "/BookKeeper/"+filename+".json"));
                UploadTask uploadTask = storageRef.child(filename).putStream(stream);
                uploadTask.addOnFailureListener(exception -> {
                  backuptext.setText("Something Went Wrong");
                    Log.d("error",exception.getMessage());
                }).addOnSuccessListener(taskSnapshot -> {

                    storageRef.child(filename)
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> ref2.child(filename)
                                    .child("FILELINK").setValue(uri.toString())
                                    .addOnFailureListener(e -> Log.e("Error",e.getMessage()))
                                    .addOnSuccessListener(unused -> {
                                        Log.e("File","Uploaded");
                                        backuptext.setText((count+1)+" Book BackedUp");
                                        progressBar.setVisibility(View.GONE);
                                    }))
                            .addOnFailureListener(e -> Log.e("Error",e.getMessage()));

                    Log.e("onSuccess",taskSnapshot.getMetadata().getName());

                }).addOnProgressListener(snapshot -> {

                    progressBar.setIndeterminate(true);
                    progressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    if (progress>1){
                        progressBar.setProgress((int)progress, true);
                    }

                });

            } catch (FileNotFoundException e) {
                Log.e("Error",e.getMessage());
            }


        }

        backup.setEnabled(true);

    }

}