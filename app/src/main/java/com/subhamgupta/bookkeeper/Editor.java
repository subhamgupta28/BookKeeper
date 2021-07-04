package com.subhamgupta.bookkeeper;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Editor extends AppCompatActivity {
    String jsonstr;
    List<String> keys, titles, authors, imgurls, filelink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        readSavedFile();
        parseString();
    }

    private void parseString() {
        JSONObject obj = null;
        keys = new ArrayList<>();
        titles = new ArrayList<>();
        authors = new ArrayList<>();
        imgurls = new ArrayList<>();
        filelink = new ArrayList<>();
        try {
            obj = new JSONObject(jsonstr);
            Log.d("My App", String.valueOf(obj.getJSONObject("1622807534")));
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(obj.toString());
            JsonObject object = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for(Map.Entry<String, JsonElement> entry: entries) {
                Log.e("dd", entry.getKey());
                keys.add(entry.getKey());
            }
            for (int i = 0; i <keys.size() ; i++) {
                JSONObject jb = obj.getJSONObject(keys.get(i));
                titles.add(jb.getString("TITLE"));
                authors.add(jb.getString("AUTHOR"));
                imgurls.add(jb.getString("IMAGELINK"));
                //filelink.add(jb.getString("FILELINK"));
                //Log.e("js",jb.getString("TITLE"));

            }
            System.out.println(keys);
            System.out.println(titles);
            System.out.println(authors);
            System.out.println(imgurls);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void readSavedFile(){
        try {
            File yourFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SavedFile"+".txt");
            FileInputStream stream = new FileInputStream(yourFile);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
                Log.e("json",jsonStr);
                jsonstr = jsonStr;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}