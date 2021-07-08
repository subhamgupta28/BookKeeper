package com.subhamgupta.bookkeeper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExploreBooks extends AppCompatActivity {
    TextView extitle, exauthor, extext;

    ViewPager2 exviewPager2;
    ImageView exboimg;
    List<ReadBookModel> chap;
    Button  exexpand;
    FloatingActionButton excollapse;
    MaterialCardView excard;
    JsonHelper jsonHelper;
    ExploreBookAdapter exploreBookAdapter;
    ProgressBar progressBar;
    String title, author, url, key = "0";
    Map<String, String> contents;
    String font, color, fileurl, jsonStr;
    long  fontsize, currentpage, downloadID;
    int bgcolor = 0;
    List<Long> keyset;
    JSONObject jsonObject;
    List<String> chapterss, desc;
    List<Long> pg;
    Map<Long, Map<String, String>> map;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_books);

        jsonHelper = new JsonHelper();
        exauthor = findViewById(R.id.exabauthor);
        extitle = findViewById(R.id.exabtitle);
        exviewPager2 = findViewById(R.id.exreadbookrecycler);
        exexpand = findViewById(R.id.exexpand);
        excollapse = findViewById(R.id.exfabcollapse);
        exboimg = findViewById(R.id.exboimg);
        excard = findViewById(R.id.li3);
        extext = findViewById(R.id.extextv);
        relativeLayout = findViewById(R.id.explorerelative);
        progressBar = findViewById(R.id.exloading);
        init();
        exploreBookAdapter = new ExploreBookAdapter(chap);
        exviewPager2.setAdapter(exploreBookAdapter);
        exviewPager2.setClipToPadding(false);
        exviewPager2.setClipChildren(false);
        exviewPager2.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer((page, position) -> {
            float a = 1 - Math.abs(position);
            page.setScaleY(0.85f + a*0.15f);
        });
        exviewPager2.setPageTransformer(transformer);
        exexpand.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(exviewPager2, new ChangeBounds());
            TransitionManager.beginDelayedTransition(excard, new Slide(Gravity.TOP));
            excard.setVisibility(View.GONE);
            excollapse.setVisibility(View.VISIBLE);
        });

        excollapse.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(exviewPager2, new ChangeBounds());
            TransitionManager.beginDelayedTransition(excard, new Fade());
            excard.setVisibility(View.VISIBLE);
            excollapse.setVisibility(View.GONE);
            //showSystemUI();
        });

        if(fileurl!=null){
            progressBar.setVisibility(View.VISIBLE);
            downloadFile(/*getApplicationContext(),*/ key+".json",fileurl);
            Log.e("file",fileurl);
        }
        else{
            setEmptyCard();
        }




        //badgeDrawable.setNumber(4);
    }

    public void downloadFile( String filename,String url1){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
            try (BufferedInputStream in = new BufferedInputStream(new URL(url1).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Shared Books/"+filename))
            )
            {
                byte[] dataBuffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 2048)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    Log.e("data", String.valueOf(bytesRead));
                }
                runOnUiThread(() -> {
                    jsonStr = jsonHelper.readSharedFile(key);
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
        extext.setText("No Content Available");
    }
    public void setData(String jsonStr){
        chapterss = new ArrayList<>();
        desc = new ArrayList<>();
        pg = new ArrayList<>();
        try {
            jsonObject = new JSONObject(jsonStr);
            JsonParser parser = new JsonParser();
            JSONObject keydata = jsonObject.getJSONObject(key).getJSONObject("CONTENTS");
            //Log.e("keydata",keydata.toString());
            JsonElement element = parser.parse(keydata.getString("PAGES"));
            //Log.e("contents",keydata.getString("CONTENTS"));
            JsonObject object = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for(Map.Entry<String, JsonElement> entry: entries) {
                pg.add(Long.valueOf(entry.getKey()));
                //Log.e("keyss",entry.getKey());
            }
            for (int i = 0;i<pg.size();i++){
                JSONObject j = keydata.getJSONObject("PAGES");
                //Log.e("j",j.toString());
                //Log.e("chap", String.valueOf(pg.get(i)));
                JSONObject jb = j.getJSONObject(String.valueOf(pg.get(i)));
                //Log.e("val",jb.toString());
                String ch = jb.getString("CHAP");
                chapterss.add(ch);
                String des = jb.getString("DESC");
                desc.add(des);
            }
            long pgn = jsonObject.getJSONObject(key).getLong("CURRENTPAGE");
            Log.e("pgn", String.valueOf(pgn));

            for (int i =0;i<pg.size();i++){
                chap.add(new ReadBookModel(chapterss.get(i), desc.get(i), pg.get(i)));
            }
            progressBar.setVisibility(View.GONE);
            exploreBookAdapter.notifyDataSetChanged();
            exviewPager2.setCurrentItem((int) pgn-1, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void init(){
        title = getIntent().getStringExtra("title");
        author = getIntent().getStringExtra("author");
        key = getIntent().getStringExtra("key");
        url = getIntent().getStringExtra("url");
        fileurl = getIntent().getStringExtra("fileurl");
        chap = new ArrayList<>();
        contents = new HashMap<>();
        exauthor.setText(author);
        extitle.setText(title);
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.005f)
                .centerCrop()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        //AllBooks.startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        //AllBooks.startPostponedEnterTransition();
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
                .into(exboimg);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/Shared Books/");

        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                Log.e("files", children[i]);
                new File(file, children[i]).delete();
            }
        }
    }

}