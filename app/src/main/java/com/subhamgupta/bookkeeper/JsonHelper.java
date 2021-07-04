package com.subhamgupta.bookkeeper;


import android.content.Context;
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
    private String title, author,imurl, key = "0";
    private Map pages;

    private String font, color;
    private long  fontsize, currentpage;
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

    public JsonHelper(String title, String author, String key, Map<String, String> pages, String font, String color, long fontsize, long currentpage) {
        this.title = title;
        this.author = author;
        this.key = key;
        this.pages = pages;
        this.font = font;
        this.color = color;
        this.fontsize = fontsize;
        this.currentpage = currentpage;
    }

    public void JsonCreateFile(String title, String author,String imurl, String key, Map<Long, Map<String, String>> pagecontents, String font, String color, long fontsize, long currentpage){

         root = new HashMap<>();
         details = new HashMap<>();
         pages = new HashMap<>();
        //contents.put("chapter1", "desc1");
        //pagecontents={
        //              1:{
        //                  chap:ch, desc:de
        //              },
        //              2:{
        //                  chap:ch, desc:de
        //              }
        //              3:{
            //              chap:ch, desc:de
            //             },
            //          4:{
            //              chap:ch, desc:de
            //             }
        //              }
        pages.put("PAGES", pagecontents );//pages={pages:{1:{}, 2:{},....}}  pagecontent

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
            String jsonStr = null;
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






    public void upload(String key){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/BookKeeper/");

        if (file.isDirectory())
        {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++)
            {
                Log.e("files",children[i]);
                String filename = children[i];
                //storageRef = storageRef.child(key+".json");
                InputStream stream;
                try {
                    //stream = new FileInputStream(new File("/sdcard/BookKeeper/"+key+".json"));
                    stream = new FileInputStream(new File(Environment.DIRECTORY_DOWNLOADS, "/BookKeeper/"+children[i]));
                    UploadTask uploadTask = storageRef.child(key+".json").putStream(stream);
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        Log.d("error",exception.getMessage());
                    }).addOnSuccessListener(taskSnapshot -> {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...

                        /*boolean deleted = file.delete();
                        Log.d("filedeleted", String.valueOf(deleted));*/
                        Log.e("onSuccess",taskSnapshot.getMetadata().getName());

                    }).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.e(filename, String.valueOf(progress));
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}


