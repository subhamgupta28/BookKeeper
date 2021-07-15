package com.subhamgupta.bookkeeper;


import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class JsonHelper {
    String title, author, key = "0";
    private Map pages;

    String font, color;
     long  fontsize, currentpage;
    private FileWriter fileWriter;
    private String m = "{\" KEY \":{\"  TITLE   \":\"   fsttitle    \",\"   AUTHOR  \":\"   fstauthor   \",\"" +
            "   FONT    \":\"   fontname    \",\"   COLOR   \":\"   colorcode   \",\"   FONTSIZE    \":\20  ,\"" +
            " CURRENTPAGE \":\5,\"    PAGES    \":{\"  1:{\"CHAPTER\"    \":\"   chaptername, DESC : descrption}, 2:{chapter2    \":\"   desc2}  \"}}}";
    /*KEY (DETAILS, PAGES(PAGECONTENT()))*/
    private String json;
    private Map<String, Map<String, Object>> root;
    private Map<String, Object> details;
    private JSONObject jsonObject;
    private StorageReference storageRef;
    private long downloadID;

    public JsonHelper() {
        storageRef = FirebaseStorage.getInstance().getReference();

    }



    public void JsonCreateFile(String title, String author,String imurl, String key, Map<Long, Map<String, String>> pagecontents, String font, String color, long fontsize, long currentpage){

         root = new HashMap<>();
         details = new HashMap<>();
         pages = new HashMap<>();
         pages.put("PAGES", pagecontents );

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

            json  = mapper.writeValueAsString(root);
            //Log.e("json",json);
             jsonObject = new JSONObject(json);
            //Log.e("obj",jsonObject.getString("KEY"));
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    File.separator + "BookKeeper/");
            if(!dir.exists())
                dir.mkdir();
            else{
                File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        File.separator + "BookKeeper/"+key+".json");
                myFile.createNewFile();
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
    String jsonString = null;
    public String readFile(String key) {

        try {
            File yourFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/BookKeeper/" + key + ".json");
            FileInputStream stream = new FileInputStream(yourFile);
            String jsonStr;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
                Log.e("json",jsonStr);
                jsonString = jsonStr;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return jsonString;
    }
    public String readSharedFile(String key) {

        try {
            File yourFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Shared Books/" + key + ".json");
            FileInputStream stream = new FileInputStream(yourFile);
            String jsonStr;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
                Log.e("json",jsonStr);
                jsonString = jsonStr;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return jsonString;
    }
}


