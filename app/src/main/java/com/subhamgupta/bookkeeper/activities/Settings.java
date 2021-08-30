package com.subhamgupta.bookkeeper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
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
import com.subhamgupta.bookkeeper.BuildConfig;
import com.subhamgupta.bookkeeper.R;
import com.subhamgupta.bookkeeper.dataclasses.JsonHelper;
import com.subhamgupta.bookkeeper.dataclasses.SharedSession;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Settings extends AppCompatActivity  {

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
        jsonHelper = new JsonHelper(this);
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
            try{
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
            //backup.setVisibility(View.VISIBLE);
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
        restore.setVisibility(View.GONE);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    childs.add(dataSnapshot.getKey()+".json");
                    Log.e("Setting Restore", dataSnapshot.getKey());
                }
                System.out.println("childs"+childs);
                if (!childs.isEmpty()){
                    restore.setVisibility(View.GONE);
                    restoretext.setText("Found "+ childs.size()+" Book on cloud");
                }
                else {
                    restore.setVisibility(View.GONE);
                    restoretext.setText("");
                }
                children = childs;
                restore();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public List<String> prepareBackup(){
        List<String> children = new ArrayList<>();
        File file = new File(this.getFilesDir(),
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
    public void restore(){
        progressBar.setVisibility(View.GONE);
        restore.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        List<String> downloadList = new ArrayList<>();
        for( String fname : children){
            Log.e("restore: ",fname );
            storageRef.child(mAuth.getUid()+"/"+fname).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Uri url = Uri.parse(uri.toString());
                        Log.e( "restore: ",url.toString() );
                        downloadList.add(url.toString());

                    })
                    .addOnFailureListener(e -> {
                        downloadList.add("null");
                        Log.e( "restore: ",e.getMessage() );
                    });
        }

        restore.setEnabled(true);
        restore.setOnClickListener(view -> {

            if (!downloadList.isEmpty()){
                for (int i = 0; i < downloadList.size() ; i++) {
                    if (downloadList.get(i).equals("null"))
                        downloadList.remove(i);
                }
                for (int i = 0; i < downloadList.size() ; i++) {
                    downloadFile( children.get(i), downloadList.get(i), downloadList.size());
                }

            }

        });


    }
    int fst =0;
    public void downloadFile( String filename, String url1, int last){

        progressBar.setVisibility(View.VISIBLE);
        ExecutorService service = Executors.newSingleThreadExecutor();
        Log.e( "downloadFile: ", ""+service.isTerminated());
        service.execute(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.e( "downloadFile: Thread Name ", Thread.currentThread().getName());

            URL url = null;
            HttpURLConnection connection;
            try{ url = new URL(url1);
                connection = (HttpURLConnection) url.openConnection();

                float totalDataRead=0;
                File file = new File(this.getFilesDir()+"/BookKeeper/", filename);

                FileOutputStream fileOutputStream = new FileOutputStream(file);

                int filesize = connection.getContentLength();
                Log.e("FILE_SIZE", (double)filesize/1024+" Kb");
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

                byte[] dataBuffer = new byte[81920];
                int bytesRead;
                float Percent = 0f;
                while ((bytesRead = in.read(dataBuffer, 0, 81920)) != -1) {
                    totalDataRead=totalDataRead+bytesRead;
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                     Percent=(totalDataRead*100)/filesize;

                    Log.e("Percent ", String.valueOf(Percent));


                }
                runOnUiThread(()->{
                    restoretext.setText(fst+ " Books Restored");
                    progressBar.setProgress((int) fst);
                });
                Log.e( "downloadFile: lst, fst ",last+","+fst );
                fst++;
                if (fst == last)
                    runOnUiThread(()->{
                        progressBar.setVisibility(View.GONE);
                    });
            } catch (Exception er) {
                // handle exception

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content),"Something went wrong try again.",Snackbar.LENGTH_LONG).show();
                });
                Log.e("error", er.getMessage());
            }

        });




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
                stream = new FileInputStream(this.getFilesDir()+
                        File.separator + "/BookKeeper/"+filename+".json");
                UploadTask uploadTask = storageRef.child(filename).putStream(stream);
                uploadTask.addOnFailureListener(exception -> {
                  backuptext.setText("Something Went Wrong");
                    Log.d("error",exception.getMessage());
                }).addOnSuccessListener(taskSnapshot -> {

                    storageRef.child(auth.getUid()+"/"+filename)
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