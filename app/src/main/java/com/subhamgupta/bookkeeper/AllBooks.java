package com.subhamgupta.bookkeeper;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AllBooks extends AppCompatActivity  {
    private TextView abtitle, abauthor, text;
    private BottomAppBar bottomAppBar;
    private ViewPager2 viewPager2;
    private ImageView boimg;
    private List<ReadBookModel> chap;
    private ReadBookAdapter readBookAdapter;

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

    private EditText gotopage;
    private Button btngo, syncnow, synccancel;
    private long pgn;
    private String sync;
    MaterialCardView synclayout;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        bottomAppBar = findViewById(R.id.abbottombar);
        setSupportActionBar(bottomAppBar);
        abauthor = findViewById(R.id.abauthor);
        abtitle = findViewById(R.id.abtitle);
        additem = findViewById(R.id.abfloatingbtn);
        text = findViewById(R.id.textv);
        boimg = findViewById(R.id.boimg);
        viewPager2 = findViewById(R.id.readbookrecycler);
        materialCardView = findViewById(R.id.li2);
        relativeLayout = findViewById(R.id.relativelayoutallbooks);
        //backup = findViewById(R.id.backup);


        progressBar =findViewById(R.id.abloading);
        slider =findViewById(R.id.pageslider);
        bottomlayout =findViewById(R.id.bottomsheetlayout);


        gotopage =findViewById(R.id.gotopage);
        btngo =findViewById(R.id.btngo);
        synclayout =findViewById(R.id.synclayout);
        syncnow =findViewById(R.id.syncnow);
        synccancel =findViewById(R.id.synccancel);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> _init(),300);

        
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

        readBookAdapter = new ReadBookAdapter(chap);
        viewPager2.setAdapter(readBookAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        map = new HashMap<>();
        jsonHelper = new JsonHelper();
        sheetBehavior = BottomSheetBehavior.from(bottomlayout);


        bottomAppBar.setNavigationOnClickListener(view -> {

            sheetBehavior.setState((sheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)?BottomSheetBehavior.STATE_COLLAPSED:BottomSheetBehavior.STATE_EXPANDED);
        });


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
            progressBar.setVisibility(View.VISIBLE);
            downloadFile(/*getApplicationContext(),*/ key+".json", fileurl);
        }
        else{
            setEmptyCard();
        }
        syncnow.setOnClickListener(view -> {sync();});
        /*IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast

                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (downloadID == id) {
                    Log.e("File", "Downloaded");
                    jsonStr = jsonHelper.readFile(key);
                    setData(jsonStr);
                }
            }
        };
        registerReceiver(onDownloadComplete, filter);*/
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
        Log.e("menu_selected",menu.toString());
        return super.onCreateOptionsMenu(menu);
    }
    /*public void downloadFile(Context context, String fileName, String url) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, "/BookKeeper/"+fileName);
        downloadID = downloadmanager.enqueue(request);
    }*/
    public void downloadFile( String filename,String url1){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
            try (BufferedInputStream in = new BufferedInputStream(new URL(url1).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/BookKeeper/"+filename))
            )
            {
                byte[] dataBuffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 2048)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    Log.e("data", String.valueOf(bytesRead));
                }
                runOnUiThread(() -> {
                    jsonStr = jsonHelper.readFile(key);
                    setData(jsonStr);
                });
            } catch (IOException er) {
                // handle exception
                Log.e("error", er.getMessage());
            }

        });
        Log.e("error", "er.getMessage()");



    }

    public void setEmptyCard(){
        chap.add(new ReadBookModel("Add Text","", 1));

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
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/BookKeeper/");
            if (file.isDirectory()) {
                new File(file, key+".json").delete();
            }
            downloadFile( key+".json", fileurl);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("BOOKDATA").child(FirebaseAuth.getInstance().getUid());
            ref.child(key).child("SYNC").setValue("synced").addOnSuccessListener(unused -> synclayout.setVisibility(View.GONE)).addOnFailureListener(e -> {

            });
        }
        else
            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();

    }
    List<String> chapt, descri;
    public void setBackup(){
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

        Snackbar.make(additem, "Saved", Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
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
        if (sync!=null){
            if (sync.equals("Not"))
                synclayout.setVisibility(View.VISIBLE);
            else
                synclayout.setVisibility(View.GONE);
        }

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
    public void lock(){

    }
    public void setData(String jsonStr) throws IndexOutOfBoundsException {
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
            if (pg.get(0)==1){
                slider.setValueFrom(0);
            }
            else{
                slider.setValueFrom(1);

            }
            //slider.setValueFrom(pg.get(0));
            slider.setValueTo(chap.size());
            slider.setValue(pg.get(0));
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

        alert = new MaterialAlertDialogBuilder(this);

        alert.setMessage("Save all tabs before exiting.");

        alert.setNegativeButton("Exit", (dialogInterface, i) ->{
            setBackup();
            finish();
        });
        alert.setPositiveButton("Cancel", (dialogInterface, i) -> {
            Toast.makeText(getApplicationContext(),"Save Before Exiting",Toast.LENGTH_LONG).show();
        });
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












}