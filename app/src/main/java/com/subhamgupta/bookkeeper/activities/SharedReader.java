package com.subhamgupta.bookkeeper.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.subhamgupta.bookkeeper.dataclasses.JsonHelper;
import com.subhamgupta.bookkeeper.R;
import com.subhamgupta.bookkeeper.adapters.ExploreBookAdapter;
import com.subhamgupta.bookkeeper.models.ReadBookModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedReader extends AppCompatActivity {
    FloatingActionButton collapse;
    Button expand, tryagain;
    TextView tvtitle, tvauthor;
    ImageView imageView;
    ViewPager2 pager2;
    JsonHelper jsonHelper;
    List<ReadBookModel> chap;
    String jsonStr;
    ProgressBar progressBar;
    String key;
    JSONObject jsonObject;
    String fileurl, author;
   
    List<String> chapterss, desc;
    ExploreBookAdapter exploreBookAdapter;
    RelativeLayout relativeLayout;
    List<Long> pg;
    MaterialCardView materialCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_reader);
        expand = findViewById(R.id.srexpand);
        tvauthor = findViewById(R.id.srabauthor);
        tvtitle = findViewById(R.id.srabtitle);
        imageView = findViewById(R.id.srboimg);
        collapse = findViewById(R.id.srcollapse);
        pager2 = findViewById(R.id.srrecycler);
        relativeLayout = findViewById(R.id.srrelative);
        materialCardView = findViewById(R.id.li3);
        progressBar = findViewById(R.id.loading);
        tryagain = findViewById(R.id.tryagainsr);
        jsonHelper = new JsonHelper(this);

        File fl = new File(this.getFilesDir(),"/Shared Books/");
        if(!fl.exists())
            fl.mkdir();

        Uri uri = getIntent().getData();
        Log.e("uri",uri.toString());
        if (uri != null){
            List<String> path = uri.getPathSegments();
            System.out.println(path);
            key = path.get(path.size()-1);
            key = key.substring(key.lastIndexOf("/")+1).substring(0, 10);
            Log.e("path",key);

        }
        chap = new ArrayList<>();
        exploreBookAdapter = new ExploreBookAdapter(chap);
        pager2.setAdapter(exploreBookAdapter);
        pager2.setClipToPadding(false);
        pager2.setClipChildren(false);
        pager2.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer((page, position) -> {
            float a = 1 - Math.abs(position);
            page.setScaleY(0.85f + a*0.15f);
        });
        pager2.setPageTransformer(transformer);
        Snackbar.make(findViewById(android.R.id.content),"Wait getting book. ",Snackbar.LENGTH_SHORT).show();
        downloadFile( uri.toString(),key);
        fileurl = uri.toString();
        expand.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(pager2, new ChangeBounds());
            TransitionManager.beginDelayedTransition(materialCardView, new Slide(Gravity.TOP));
            materialCardView.setVisibility(View.GONE);
            collapse.setVisibility(View.VISIBLE);
        });

        collapse.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(pager2, new ChangeBounds());
            TransitionManager.beginDelayedTransition(materialCardView, new Fade());
            materialCardView.setVisibility(View.VISIBLE);
            collapse.setVisibility(View.GONE);
            //showSystemUI();
        });
        tryagain.setOnClickListener(view -> {
            downloadFile(fileurl, key);
        });
    }

    public void downloadFile( String url1, String filename){
        progressBar.setVisibility(View.VISIBLE);
        tryagain.setVisibility(View.GONE);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            URL url = null;

            try{ url = new URL(url1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                float totalDataRead=0;
                File file = new File(this.getFilesDir()+"/Shared Books/",filename+".json");

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
                    jsonStr = jsonHelper.readSharedFile(filename);
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



    public void setData(String jsonStr){
        chapterss = new ArrayList<>();
        desc = new ArrayList<>();
        pg = new ArrayList<>();
        try {
            jsonObject = new JSONObject(jsonStr);
            JsonParser parser = new JsonParser();
            Log.e("error",key);
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
            JSONObject jb = jsonObject.getJSONObject(key);
            tvtitle.setText(jb.getString("TITLE"));
            tvauthor.setText(jb.getString("AUTHOR"));
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(jb.getString("IMGURL"))
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
                                int bgcolor = p.getDominantColor(Color.parseColor("#121212"));

                                relativeLayout.setBackgroundColor(bgcolor);
                                getWindow().setStatusBarColor(bgcolor);
                                //materialCardView.setCardBackgroundColor(bgcolor);
                                //navigationView.setBackgroundColor(bgcolor);

                            }
                            return false;
                        }
                    })
                    .into(imageView);
            Log.e("url",jb.getString("IMGURL"));
            for (int i =0;i<pg.size();i++){
                chap.add(new ReadBookModel(chapterss.get(i), desc.get(i), pg.get(i)));
            }
            progressBar.setVisibility(View.GONE);
            exploreBookAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(this.getFilesDir(),"/Shared Books/");

        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                Log.e("files", children[i]);
                new File(file, children[i]).delete();
            }
        }
    }
}