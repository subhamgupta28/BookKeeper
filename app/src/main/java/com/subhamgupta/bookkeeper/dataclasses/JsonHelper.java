package com.subhamgupta.bookkeeper.dataclasses;


import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Keep
public class JsonHelper {

    Map<String, Map<Long, Map<String, String>>> pages;


    private String json;
    Map<String, Map<String, Object>> root;
    Map<String, Object> details;
    JSONObject jsonObject;

    Context context;
    protected int secretKey = 666;
    protected boolean encrypted = false;
    public JsonHelper(Context context) {

        this.context = context;
    }


    public void setEncrypted(boolean encrypted, int secretKey) {
        this.encrypted = encrypted;
        this.secretKey = secretKey;
    }

    public void JsonCreateFile(String title, String author, String imurl, String key, Map<Long, Map<String, String>> pagecontents, String font, String color, long fontsize, long currentpage) {

        root = new HashMap<>();
        details = new HashMap<>();
        pages = new HashMap<>();
        pages.put("PAGES", pagecontents);

        details.put("TITLE", title);
        details.put("AUTHOR", author);
        details.put("IMGURL", imurl);
        details.put("FONT", font);
        details.put("COLOR", color);
        details.put("FONTSIZE", fontsize);
        details.put("CURRENTPAGE", currentpage);
        details.put("CONTENTS", pages);

        root.put(key, details);


        ObjectMapper mapper = new ObjectMapper();
        try {

            json = mapper.writeValueAsString(root);
            //Log.e("json",json);
            jsonObject = new JSONObject(json);
            //Log.e("obj",jsonObject.getString("KEY"));
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
        try {
            File dir = new File(context.getFilesDir(),
                    File.separator + "BookKeeper/");
            if (!dir.exists())
                dir.mkdir();

            File myFile = new File(dir, key + ".json");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            String encrypt;
            if (encrypted){
                encrypt = encrypt(json, secretKey);
            }else
                encrypt = json;
            myOutWriter.write(encrypt);
            myOutWriter.close();
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("err", e.getMessage());
        }

    }

    String jsonString = null;

    public String readFile(String key) {


            File yourFile = new File(context.getFilesDir(), "/BookKeeper/" + key + ".json");
            try (FileInputStream stream = new FileInputStream(yourFile)) {
                String jsonStr;
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
                Log.e("json", jsonStr);
                String decry;
                if (encrypted)
                    decry = decrypt(jsonStr, secretKey);
                else
                    decry = jsonStr;

                jsonString = decry;
                //jsonString = jsonStr;
            } catch (Exception e) {
                e.printStackTrace();
            }

        return jsonString;
    }

    public String readSharedFile(String key) {

        File yourFile = new File(context.getFilesDir(), "/Shared Books/" + key + ".json");

        try (FileInputStream stream = new FileInputStream(yourFile)) {
            String jsonStr;
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            jsonStr = Charset.defaultCharset().decode(bb).toString();
            Log.e("json", jsonStr);
            String decry;
            if (encrypted)
                decry = decrypt(jsonStr, secretKey);
            else
                decry = jsonStr;

            jsonString = decry;
            //jsonString = jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public String encrypt(String text, int key) {

        String encry = "";
        char[] chars = text.toCharArray();
        for (char c : chars) {
            c += key;
            encry += c + "";
        }
        return encry;
    }

    public String decrypt(String text, int key) {

        String decry = "";
        char[] chars = text.toCharArray();
        for (char c : chars) {
            c -= key;
            decry += c + "";
        }
        return decry;
    }
}


