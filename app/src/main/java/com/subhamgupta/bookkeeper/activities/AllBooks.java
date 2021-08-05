package com.subhamgupta.bookkeeper.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.subhamgupta.bookkeeper.dataclasses.JsonHelper;
import com.subhamgupta.bookkeeper.R;
import com.subhamgupta.bookkeeper.dataclasses.SharedSession;
import com.subhamgupta.bookkeeper.adapters.ReadBookAdapter;
import com.subhamgupta.bookkeeper.models.ReadBookModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Keep
public class AllBooks extends AppCompatActivity  {
    private TextView abtitle, abauthor, text;
    private BottomAppBar bottomAppBar;
    private ViewPager2 viewPager2;
    private ImageView boimg;
    boolean isBookEmpty  = false;
    ProgressBar progressup;
    StorageReference storageRef;
    DatabaseReference ref, ref2;
    private List<ReadBookModel> chap;
    private ReadBookAdapter readBookAdapter;
    boolean writePermissionGranted = false;
    boolean readPermissionGranted = false;
    private FloatingActionButton  additem;
    private MaterialCardView materialCardView;
    private String title, author, url, fileurl, key = "0";
    private Map<String, String> contents;
    private String font, color;
    private long  fontsize, currentpage;
    private CoordinatorLayout relativeLayout;
    private SharedSession ss;
    private int bgcolor = 0;
    private Map<Long, Map<String, String>> map;
    private Slider slider;
    private long downloadID;
    private List<Long> keyset;
    private JSONObject jsonObject;
    private MaterialAlertDialogBuilder alert;
    private JsonHelper jsonHelper;
    private String jsonStr;
    private ProgressBar progressBar;
    private MaterialCardView bottomlayout;
    private BottomSheetBehavior sheetBehavior;
    int READ_REQUEST_CODE = 202;
    private EditText gotopage;
    private Button btngo, syncnow, synccancel, tryagain;
    private long pgn;
    private String sync;
    MaterialCardView synclayout;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        View decorView = getWindow().getDecorView(); //set status background black
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        bottomAppBar = findViewById(R.id.abbottombar);
        setSupportActionBar(bottomAppBar);
        abauthor = findViewById(R.id.abauthor);
        abtitle = findViewById(R.id.abtitle);
        additem = findViewById(R.id.abfloatingbtn);
        text = findViewById(R.id.textv);
        progressup = findViewById(R.id.progressup);
        boimg = findViewById(R.id.boimg);
        viewPager2 = findViewById(R.id.readbookrecycler);
        materialCardView = findViewById(R.id.li2);
        relativeLayout = findViewById(R.id.relativelayoutallbooks);
        tryagain = findViewById(R.id.tryagainab);
        //backup = findViewById(R.id.backup);
        user = FirebaseAuth.getInstance().getCurrentUser();
        MaterialShapeDrawable bottomBarBackground = (MaterialShapeDrawable) bottomAppBar.getBackground();
        bottomBarBackground.setShapeAppearanceModel(
                bottomBarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(CornerFamily.ROUNDED,30)
                        .setTopLeftCorner(CornerFamily.ROUNDED,30)
                        .build());
        progressBar =findViewById(R.id.abloading);
        slider =findViewById(R.id.pageslider);
        bottomlayout =findViewById(R.id.bottomsheetlayout);
        File fl = new File(this.getFilesDir(),"/Buffer/");
        if(!fl.exists())
            fl.mkdir();
        fl = new File(this.getFilesDir(),"/BookKeeper/");
        if(!fl.exists())
            fl.mkdir();

        gotopage =findViewById(R.id.gotopage);
        btngo =findViewById(R.id.btngo);
        synclayout =findViewById(R.id.synclayout);
        syncnow =findViewById(R.id.syncnow);
        synccancel =findViewById(R.id.synccancel);
        progressBar.setVisibility(View.VISIBLE);
        requestForPermission();
        tryagain.setOnClickListener(view -> downloadFile(key+".json", fileurl));
        new Handler().postDelayed(this::_init,300);

        
    }
    public void showPermissionDialog(){
        MaterialAlertDialogBuilder madb = new MaterialAlertDialogBuilder(this);
        madb.setTitle("Allow Permission");
        madb.setMessage("Storage permission is needed for the better functionality of the app. " +
                "Go to App info to allow permission.");
        madb.setNegativeButton("Go to App info", (dialogInterface, i) -> {

            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });
        madb.show();

    }
    public void requestForPermission(){
        Log.e("Permission","requesting");
        readPermissionGranted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;


        if (!readPermissionGranted)
            ActivityCompat.requestPermissions(this,new String[]{"Manifest.permission.READ_EXTERNAL_STORAGE"}, READ_REQUEST_CODE);

        if (!readPermissionGranted)
            showPermissionDialog();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("permission "+permissions+"\ngrant result "+ grantResults+"\nrequest code"+requestCode);
        if (requestCode == READ_REQUEST_CODE) {
            //requestPermission();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission was granted. Now you can call your method to open camera, fetch contact or whatever
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

        }



    }

    public void showDemo(){
        TapTargetSequence tp = new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(additem, "Add New Item")
                                .dimColor(R.color.colorPrimary)
                                .titleTextSize(25)
                                .outerCircleColor(R.color.colorRed)

                                .targetCircleColor(R.color.colorSecondary)
                                .tintTarget(false)
                                .textColor(R.color.colorPrimary),

                        TapTarget.forView(findViewById(R.id.collapse), "Collapse Item")
                                .dimColor(R.color.colorPrimary)
                                .titleTextSize(25)
                                .outerCircleColor(R.color.colorRed)
                                .targetCircleColor(R.color.colorSecondary)
                                .textColor(R.color.colorPrimary),
                        TapTarget.forView(findViewById(R.id.expand), "Expand Item")
                                .dimColor(R.color.colorPrimary)
                                .titleTextSize(25)
                                .outerCircleColor(R.color.colorRed)
                                .targetCircleColor(R.color.colorSecondary)
                                .textColor(R.color.colorPrimary),
                        TapTarget.forView(findViewById(R.id.savebook), "Save Book Locally")
                                .dimColor(R.color.colorPrimary)
                                .titleTextSize(25)
                                .outerCircleColor(R.color.colorRed)
                                .targetCircleColor(R.color.colorSecondary)
                                .textColor(R.color.colorPrimary),
                        TapTarget.forView(findViewById(R.id.booksearch), "Search for any part in the book")
                                .dimColor(R.color.colorPrimary)
                                .titleTextSize(25)
                                .outerCircleColor(R.color.colorRed)
                                .targetCircleColor(R.color.colorSecondary)
                                .textColor(R.color.colorPrimary))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay

                        ss.setDemoAddBook(false);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Perform action for the current target
                        Log.e("target",lastTarget.toString());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                });
        tp.start();
    }
    public void _init(){
        init();

        readBookAdapter = new ReadBookAdapter(chap, key);
        viewPager2.setAdapter(readBookAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        map = new HashMap<>();
        jsonHelper = new JsonHelper(this);
        sheetBehavior = BottomSheetBehavior.from(bottomlayout);


        bottomAppBar.setNavigationOnClickListener(view -> sheetBehavior.setState((sheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)?BottomSheetBehavior.STATE_COLLAPSED:BottomSheetBehavior.STATE_EXPANDED));


        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer((page, position) -> {
            float a = 1 - Math.abs(position);
            page.setScaleY(0.85f + a*0.15f);
        });
        viewPager2.setPageTransformer(transformer);





        newCards();

        if (jsonHelper.readFile(key)!=null){
            setData(jsonHelper.readFile(key));
        }
        else if(fileurl!=null){

            downloadFile(/*getApplicationContext(),*/ key+".json", fileurl);
        }
        else{
            setEmptyCard();
        }
        syncnow.setOnClickListener(view -> sync());

        btngo.setOnClickListener(view -> {
            int pg = Integer.parseInt(gotopage.getText().toString());
            if(pg<=chap.size())
                viewPager2.setCurrentItem(pg-1, true);
            Log.e("pg", String.valueOf(pg));
        });

         if (ss.isDemoAddBook())
            showDemo();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        
        MenuItem savebook = menu.findItem(R.id.savebook);
        savebook.setOnMenuItemClickListener(menuItem1 -> {
            setBackup();
            return false;
        });

        MenuItem collapses = menu.findItem(R.id.collapse);
        MenuItem expand = menu.findItem(R.id.expand);

        collapses.setOnMenuItemClickListener(menuItem1 -> {
            TransitionManager.beginDelayedTransition(viewPager2, new ChangeBounds());
            TransitionManager.beginDelayedTransition(materialCardView, new Fade());
            materialCardView.setVisibility(View.VISIBLE);
            //navigationView.setVisibility(View.VISIBLE);

            collapses.setEnabled(false);
            expand.setEnabled(true);
            collapses.setChecked(false);
            expand.setChecked(true);
            return false;
        });

        expand.setOnMenuItemClickListener(menuItem -> {
            TransitionManager.beginDelayedTransition(viewPager2, new ChangeBounds());
            TransitionManager.beginDelayedTransition(materialCardView, new Slide(Gravity.TOP));
            materialCardView.setVisibility(View.GONE);
            collapses.setEnabled(true);
            expand.setEnabled(false);
            collapses.setChecked(true);
            expand.setChecked(false);


            return false;
        });

        MenuItem search = menu.findItem(R.id.booksearch);
        android.widget.SearchView searchView = (android.widget.SearchView) search.getActionView();
        searchView.setMaxWidth(600);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });
        MenuItem share = menu.findItem(R.id.share);
        share.setOnMenuItemClickListener(menuItem -> {
            share(fileurl);
            return false;
        });
        Log.e("menu_selected",menu.toString());
        return super.onCreateOptionsMenu(menu);
    }
    private void share( String link) {
        if(link==null){
            progressup.setIndeterminate(true);
            progressup.setVisibility(View.VISIBLE);
            setBackup();
            upload(key);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Share Book");
            intent.putExtra(Intent.EXTRA_TEXT,"Check out this book which i am reading now\nClick here "+link);
            startActivity(Intent.createChooser(intent, "Share via"));
        }


    }

    public void upload( String key){
        File file = new File(this.getFilesDir(),
                File.separator + "BookKeeper/"+key+".json");


        if (file.isFile())
        {
            storageRef = FirebaseStorage.getInstance().getReference();
            ref = FirebaseDatabase.getInstance().getReference().child("BOOKDATA");
            ref2 = ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            InputStream stream;
            try {
                //stream = new FileInputStream(new File("/sdcard/BookKeeper/"+key+".json"));
                stream = new FileInputStream(new File(this.getFilesDir(),
                        File.separator + "/BookKeeper/"+key+".json"));
                UploadTask uploadTask = storageRef.child(user.getUid()+"/"+key+".json").putStream(stream);
                uploadTask.addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Log.d("error",exception.getMessage());
                }).addOnSuccessListener(taskSnapshot -> {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    progressup.setVisibility(View.GONE);
                    storageRef.child(user.getUid()+"/"+key+".json").getDownloadUrl().addOnSuccessListener(uri -> {
                        ref2.child(key).child("SYNC").setValue("Not");
                        ref2.child(key).child("FILELINK").setValue(uri.toString()).addOnFailureListener(e -> {
                            Log.e("Error",e.getMessage());

                        }).addOnSuccessListener(unused -> {
                            Log.e("File","Uploaded");

                            Toast.makeText(this, "Sharing...",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT,"Share Book");
                            intent.putExtra(Intent.EXTRA_TEXT,"Check out this book which i am reading now\nClick here "+uri.toString());
                            startActivity(Intent.createChooser(intent, "Share via"));
                        });
                    }).addOnFailureListener(e -> {
                        Log.e("Error",e.getMessage());
                        progressup.setVisibility(View.GONE);
                    });

                    Log.e("onSuccess",taskSnapshot.getMetadata().getName());

                }).addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    if (progress>1){
                        progressup.setIndeterminate(false);

                        progressup.setProgress((int)progress, true);
                    }

                });

            } catch (FileNotFoundException e) {
                Log.e("Error",e.getMessage());
            }


        }
        else {

            Log.e("file","not present");

        }


    }


    public void downloadFile( String filename,String url1){
        progressBar.setVisibility(View.VISIBLE);
        tryagain.setVisibility(View.GONE);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
                while ((bytesRead = in.read(dataBuffer, 0, 81920)) != -1) {
                    totalDataRead=totalDataRead+bytesRead;
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    float Percent=(totalDataRead*100)/filesize;

                    Log.e("Percent ", String.valueOf(Percent));
                }
                runOnUiThread(() -> {
                    jsonStr = jsonHelper.readFile(key);
                    setData(jsonStr);
                });
            } catch (Exception er) {
                // handle exception

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tryagain.setVisibility(View.VISIBLE);
                    Snackbar.make(findViewById(android.R.id.content),"Something went wrong try again.",Snackbar.LENGTH_LONG).show();
                });
                Log.e("error", er.getMessage());
            }

        });




    }


    public void setEmptyCard(){
        chap.add(new ReadBookModel("Add Text","", 1));
        isBookEmpty = true;
        Toast.makeText(getApplicationContext(), "Add More Cards By Clicking Plus Icon",Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    public void search(String s){
        Log.e("search",s);
        System.out.println(desc);
        if (desc != null){
            for (String l: desc){
                Log.e("search",l);
                if (l.contains(s)){
                    int i = desc.indexOf(l);
                    viewPager2.setCurrentItem(i, true);

                }
            }
        }

    }


    public void sync(){
        if (fileurl!=null){
            progressBar.setVisibility(View.VISIBLE);
            File file = new File(this.getFilesDir(),"/BookKeeper/");
            if (file.isDirectory()) {
                new File(file, key+".json").delete();
            }
            downloadFile( key+".json", fileurl);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("BOOKDATA").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            ref.child(key).child("SYNC").setValue("synced").addOnSuccessListener(unused -> synclayout.setVisibility(View.GONE)).addOnFailureListener(e -> {

            });
        }
        else
            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();

    }
    List<String> chapt, descri;
    public void setBackup(){
        isBookEmpty = false;
        map = readBookAdapter.getContents();
        System.out.println(map);
        keyset = new ArrayList<>(map.keySet());//page numbers
        chapt = new ArrayList<>();// chapters
        descri = new ArrayList<>();// descriptions
        Map mp;
        for (int i=0;i<map.size();i++){
            mp = map.get(keyset.get(i));
            chapt.add(String.valueOf(mp.get("CHAP")));
            descri.add(String.valueOf(mp.get("DESC")));
        }

        /*for (int i = 0; i<map.size();i++){
            chap.set(i, new ReadBookModel(chapt.get(i),descri.get(i), keyset.get(i)));
        }*/
        currentpage = viewPager2.getCurrentItem();
        jsonHelper.JsonCreateFile(title, author, url, key, map, font, color, fontsize, currentpage);

        Snackbar.make(additem, "Saved to local storage", Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setAnchorView(additem)
                .show();
    }
    public void newCards(){
        additem.setOnClickListener(view -> {
            int po = readBookAdapter.getItemCount();
            chap.add(new ReadBookModel("Add Text","Add text",po+1));
            readBookAdapter.notifyDataSetChanged();
            int pos = readBookAdapter.getItemCount();
            viewPager2.setCurrentItem(pos, true);
            text.setVisibility(View.GONE);
            slider.setValueFrom(1);
            slider.setValue(po+1);
            slider.setValueTo(chap.size());
            //setFromBuffer();
        });
        /*for (int childCount = viewPager2.getChildCount(), i = 0; i < childCount; ++i) {
            final ViewHolder holder = viewPager2.getChildViewHolder(recyclerView.getChildAt(i));
        }*/

    }

    public void init(){
        title = getIntent().getStringExtra("title");
        author = getIntent().getStringExtra("author");
        key = getIntent().getStringExtra("key");
        url = getIntent().getStringExtra("url");
        fileurl = getIntent().getStringExtra("fileurl");
        sync = getIntent().getStringExtra("sync");
        ss = new SharedSession(getApplicationContext());

        String uuid = new SharedSession(getApplicationContext()).getUuid();
        Log.e("uuid",uuid);
//        if (sync!=null){
//            if (sync.equals("Not"))
//                synclayout.setVisibility(View.VISIBLE);
//            else
//                synclayout.setVisibility(View.GONE);
//        }

        chap = new ArrayList<>();
        contents = new HashMap<>();
        abauthor.setText(author);
        abtitle.setText(title);
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.005f)
                .centerCrop()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        if (resource != null) {
                            Palette p = Palette.from(resource).generate();
                            // Use generated instance
                             bgcolor = p.getDominantColor(Color.parseColor("#121212"));

                                relativeLayout.setBackgroundColor(bgcolor);
                                getWindow().setStatusBarColor(bgcolor);



                            //materialCardView.setCardBackgroundColor(bgcolor);
                            //navigationView.setBackgroundColor(bgcolor);

                        }
                        return false;
                    }
                })
                .into(boimg);
        synccancel.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(synclayout, new Slide(Gravity.RIGHT));
            TransitionManager.beginDelayedTransition(viewPager2, new ChangeBounds());
            synclayout.setVisibility(View.GONE);
        });
    }

    List<String> chapterss, desc;
    List<Long> pg;

    public void setData(String jsonStr) throws IndexOutOfBoundsException {
        isBookEmpty = false;
        chapterss = new ArrayList<>();
        desc = new ArrayList<>();
        pg = new ArrayList<>();
        try {
            jsonObject = new JSONObject(jsonStr);
            JsonParser parser = new JsonParser();
            JSONObject keydata = jsonObject.getJSONObject(key).getJSONObject("CONTENTS");
            JsonElement element = parser.parse(keydata.getString("PAGES"));
            JsonObject object = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for(Map.Entry<String, JsonElement> entry: entries) {
               pg.add(Long.valueOf(entry.getKey()));
            }
            for (int i = 0;i<pg.size();i++){
                JSONObject j = keydata.getJSONObject("PAGES");
                JSONObject jb = j.getJSONObject(String.valueOf(pg.get(i)));
                String ch = jb.getString("CHAP");
                chapterss.add(ch);
                String des = jb.getString("DESC");
                desc.add(des);
            }
             pgn = jsonObject.getJSONObject(key).getLong("CURRENTPAGE");
            Log.e("pgn", String.valueOf(pgn));

            for (int i =0;i<pg.size();i++){
                chap.add(new ReadBookModel(chapterss.get(i), desc.get(i), pg.get(i)));
            }

            progressBar.setVisibility(View.GONE);
            readBookAdapter.notifyDataSetChanged();
            viewPager2.setCurrentItem((int) pgn, true);
            Log.e("pgn", String.valueOf(pgn));
            Log.e("chap", String.valueOf(chap.size()));
            Log.e("pg", String.valueOf(pg));
            try {
                if (pg.get(0)==1){
                    slider.setValueFrom(0);
                }
                else{
                    slider.setValueFrom(1);

                }
                //slider.setValueFrom(pg.get(0));
                slider.setValueTo(chap.size());
                slider.setValue(pg.get(0));
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }

            slider.addOnChangeListener((slider, value, fromUser) -> {
                Log.e("slider", String.valueOf(value));
                if (value<=chap.size())
                    viewPager2.setCurrentItem((int) value-1, true);
                Log.e("value", String.valueOf(value));

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        //setBackup();
        //new Handler().postDelayed(() -> AllBooks.super.onBackPressed(), 200);

            alert = new MaterialAlertDialogBuilder(this);

            alert.setMessage("Save all tabs before exiting.");

            alert.setNegativeButton("Save & Exit", (dialogInterface, i) ->{
                setBackup();
                finish();
            });
            alert.setPositiveButton("Exit", (dialogInterface, i) -> finish());
            alert.show();


    }


    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop()
    {
        super.onStop();

    }

    public void setFromBuffer() {
        chapterss = new ArrayList<>();
        desc = new ArrayList<>();
        pg = new ArrayList<>();
        try {
            File file = new File(this.getFilesDir(),
                    File.separator+"Buffer/");
            String[] filenames = file.list();
            for (int i = 0; i <filenames.length; i++) {
                File myFile = new File(file+File.separator+filenames[i]);
                FileInputStream stream = new FileInputStream(myFile);
                String jsonStr;

                try {

                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    jsonStr = Charset.defaultCharset().decode(bb).toString();
                    Log.e("json", jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    chapterss.add(jsonObject.getString("CH"));
                    desc.add(jsonObject.getString("DE"));
                    pg.add(Long.parseLong(jsonObject.getString("PG")));
                    chap.clear();
                    for (int j =0;j<pg.size();j++){
                        chap.add(new ReadBookModel(chapterss.get(j), desc.get(j), pg.get(j)));
                    }
                    readBookAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stream.close();
                }
            }

        }catch(Exception e){
            Log.e("ERROR BUFFER", e.getMessage());
        }

    }










}