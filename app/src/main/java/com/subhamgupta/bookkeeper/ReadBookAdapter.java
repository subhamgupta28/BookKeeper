package com.subhamgupta.bookkeeper;

import android.graphics.Typeface;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadBookAdapter extends RecyclerView.Adapter<ReadBookAdapter.ReadBookHolder> {
    List<ReadBookModel> bookchap;
    String title, author, key = "0";
    Map<Long, Map<String, String>> pagecontents = new HashMap<>();
    String font, color;
    ReadBookHolder hold;
    Spannable str, ch;
    int fontsize = 10, currentpage;

    public ReadBookAdapter(List<ReadBookModel> bookchap) {
        this.bookchap = bookchap;

    }

    @NonNull
    @Override
    public ReadBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.read_book_item, parent, false);
        return new ReadBookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadBookAdapter.ReadBookHolder holder, int position) {
        holder.description.setText(Html.fromHtml(bookchap.get(position).getDescription()));
        holder.chapter.setText(Html.fromHtml(bookchap.get(position).getChapter()));
        holder.pageno.setText(String.valueOf(bookchap.get(position).getPageNo()));

        save(holder);
        holder.btnsave.setOnClickListener(view -> {
            str = new SpannableString(holder.description.getText());
            ch = new SpannableString(holder.chapter.getText());
            String st = Html.toHtml(str);
            Map map = new HashMap();
            map.put("CHAP", ch);
            map.put("DESC", st);
            long pg = Integer.parseInt(holder.pageno.getText().toString());
            pagecontents.put(pg, map);//1 2 3
        });
        holder.btndelete.setOnClickListener(view -> {
            bookchap.remove(position);
            //pagecontents.remove(bookchap.get(position).getPageNo());
            notifyDataSetChanged();
        });
        hold=holder;
        holder.btnbold.setOnClickListener(view -> {
            str = new SpannableString(holder.description.getText());
            str.setSpan(new StyleSpan(Typeface.BOLD), holder.description.getSelectionStart(), holder.description.getSelectionEnd(), 0);
            holder.description.setText(str);
        });
        holder.btnitalic.setOnClickListener(view -> {
            str = new SpannableString(holder.description.getText());
            str.setSpan(new StyleSpan(Typeface.ITALIC), holder.description.getSelectionStart(), holder.description.getSelectionEnd(), 0);
            holder.description.setText(str);
        });
        holder.btnunderlined.setOnClickListener(view -> {
            str = new SpannableString(holder.description.getText());
            str.setSpan(new UnderlineSpan(), holder.description.getSelectionStart(), holder.description.getSelectionEnd(), 0);
            holder.description.setText(str);

        });
        holder.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                str = new SpannableString(editable);
                String st = Html.toHtml(str);
                Map<String, String> map = new HashMap();
                map.put("CHAP", holder.chapter.getText().toString());
                map.put("DESC", st);
                long pg = Integer.parseInt(holder.pageno.getText().toString());
                pagecontents.put(pg, map);//1 2 3
                //saveToBuffer(holder);

            }
        });
        holder.chapter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                str = new SpannableString(editable);
                String st = Html.toHtml(str);
                Map<String, String> map = new HashMap();
                map.put("CHAP", st);
                map.put("DESC", holder.description.getText().toString());
                long pg = Integer.parseInt(holder.pageno.getText().toString());
                pagecontents.put(pg, map);//1 2 3
                //saveToBuffer(holder);


            }
        });

    }


    public void setFromBuffer() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/Buffer/");
            String[] filenames = file.list();
            for (int i = 0; i < filenames.length; i++) {
                File myFile = new File(file+filenames[i]);
                FileInputStream stream = new FileInputStream(myFile);
                String jsonStr;
                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    jsonStr = Charset.defaultCharset().decode(bb).toString();
                    Log.e("json", jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    //holder.pageno.setText((int) jsonObject.getLong("PG"));
                    hold.description.setText(Html.fromHtml(jsonObject.getString("DE")));
                    hold.chapter.setText(Html.fromHtml(jsonObject.getString("CH")));
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

    private void saveToBuffer(ReadBookHolder holder) {
        try {
            File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    File.separator + "Buffer/"+holder.pageno.getText().toString() + ".json");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            Map<String, String> root = new HashMap();
            Spannable cha = new SpannableString(holder.chapter.getText());
            Spannable desc = new SpannableString(holder.description.getText());
            root.put("PG", holder.pageno.getText().toString());
            root.put("CH",  Html.toHtml(cha));
            root.put("DE",  Html.toHtml(desc));
            ObjectMapper mapper = new ObjectMapper();
            String json="";
            json  = mapper.writeValueAsString(root);
            //Log.e("BUFFER",json);
            myOutWriter.write(json);
            myOutWriter.close();
            fOut.close();

        } catch (Exception e) {
           Log.e("ERROR BUFFER", e.getMessage());
        }
    }


    public void setFontsize(int size) {
        fontsize = size;
    }

    public int getFontsize() {
        return fontsize;
    }

    private void save(ReadBookHolder holder) {
        for (int i = 0; i < bookchap.size(); i++) {
            String st = bookchap.get(i).getDescription();
            String cha = bookchap.get(i).getChapter();
            Map<String, String> map = new HashMap();
            map.put("CHAP", cha);
            map.put("DESC", st);
            long pg = Integer.parseInt(String.valueOf(bookchap.get(i).getPageNo()));
            pagecontents.put(pg, map);
        }


    }


    public Map<Long, Map<String, String>> getContents() {

        return pagecontents;
    }


    @Override
    public int getItemCount() {
        return bookchap.size();
    }


    public class ReadBookHolder extends RecyclerView.ViewHolder {
        EditText chapter, description;
        TextView pageno;
        MaterialCardView materialCardView;
        Button btndelete, btnbold, btnitalic, btnunderlined, btnsave;

        public ReadBookHolder(@NonNull View itemView) {
            super(itemView);
            chapter = itemView.findViewById(R.id.chapter);
            description = itemView.findViewById(R.id.descriptmulti);
            btndelete = itemView.findViewById(R.id.btndel);
            btnbold = itemView.findViewById(R.id.btnbold);
            btnitalic = itemView.findViewById(R.id.btnitalic);
            btnunderlined = itemView.findViewById(R.id.btnunderlined);
            btnsave = itemView.findViewById(R.id.btnsave);
            pageno = itemView.findViewById(R.id.tvpageno);
            materialCardView = itemView.findViewById(R.id.readcard);

        }
    }
}
