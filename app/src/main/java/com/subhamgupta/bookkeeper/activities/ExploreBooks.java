package com.subhamgupta.bookkeeper.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
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

import androidx.annotation.Keep;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Keep
public class ExploreBooks extends AppCompatActivity {
    private static Byte ISSUE_DOWNLOAD_STATUS  ;
    TextView extitle, exauthor, extext;

    ViewPager2 exviewPager2;
    ImageView exboimg;
    List<ReadBookModel> chap;
    Button  exexpand, tryagain;
    FloatingActionButton excollapse;
    MaterialCardView excard;
    JsonHelper jsonHelper;
    ExploreBookAdapter exploreBookAdapter;
    ProgressBar progressBar;
    String title, author, url, key = "0";
    Map<String, String> contents;
    String  fileurl, jsonStr;

    int bgcolor = 0;

    JSONObject jsonObject;
    List<String> chapterss, desc;
    List<Long> pg;

    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_books);

        jsonHelper = new JsonHelper(this);
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
        tryagain = findViewById(R.id.tryagainex);
        init();
        exploreBookAdapter = new ExploreBookAdapter(chap);
        exviewPager2.setAdapter(exploreBookAdapter);
        exviewPager2.setClipToPadding(false);
        exviewPager2.setClipChildren(false);
        exviewPager2.setOffscreenPageLimit(3);
        File fl = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/Shared Books/");

        if(!fl.exists())
            fl.mkdir();
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
        tryagain.setOnClickListener(view -> {

            downloadFile(key+".json",fileurl);
        });
        if(fileurl!=null){

            //fastDownload(fileurl);
            downloadFile(/*getApplicationContext(),*/ key+".json",fileurl);

            Log.e("file",fileurl);
        }
        else{
            setEmptyCard();
        }




        //badgeDrawable.setNumber(4);
    }




    public void downloadFile( String filename,String url1){
        progressBar.setVisibility(View.VISIBLE);
        tryagain.setVisibility(View.GONE);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            URL url = null;

            try{ url = new URL(url1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                float totalDataRead=0;
                File file = new File(this.getFilesDir()+"/Shared Books/", filename);

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int filesize = connection.getContentLength();
                //Log.e("FILE_SIZE", (double)filesize/1024+" Kb");
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
                    jsonStr = jsonHelper.readSharedFile(key);
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
            //exviewPager2.setCurrentItem((int) pgn-1, true);
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
            for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
                Log.e("files", children[i]);
                new File(file, children[i]).delete();
            }
        }
    }

}