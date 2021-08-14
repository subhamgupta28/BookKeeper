package com.subhamgupta.bookkeeper.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.subhamgupta.bookkeeper.activities.AllBooks;
import com.subhamgupta.bookkeeper.R;
import com.subhamgupta.bookkeeper.models.MyBookModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyBookAdapter extends FirebaseRecyclerAdapter<MyBookModel, MyBookAdapter.MyBookHolder> {
    StorageReference photoRef;
    FirebaseStorage firebaseStorage;
    StorageReference storageRef;
    FirebaseUser user;
    DatabaseReference ref, ref2;
    MaterialAlertDialogBuilder alert;
    Context context;



    public MyBookAdapter(@NonNull  FirebaseRecyclerOptions<MyBookModel> options) {
        super(options);
        storageRef = FirebaseStorage.getInstance().getReference();
        ref = FirebaseDatabase.getInstance().getReference().child("BOOKDATA");
        ref2 = ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        ref2.keepSynced(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull  MyBookAdapter.MyBookHolder holder, int position, @NonNull  MyBookModel myBookModel) {
        Glide.with(holder.imageView.getContext())
                .load(myBookModel.getIMAGELINK())
                .error(R.drawable.ic_error)
                .thumbnail(0.005f)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .into(holder.imageView);
        context = holder.materialCardView.getContext();
       /* Drawable unwrappedDrawable = holder.imageView.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#121212"));*/

        holder.author.setText(myBookModel.getAUTHOR());
        holder.title.setText(myBookModel.getTITLE());
        holder.materialCardView.setOnClickListener(view -> nextPage(holder.imageView.getContext(), myBookModel.getTITLE(), myBookModel.getAUTHOR(), myBookModel.getIMAGELINK(), myBookModel.getFILELINK(), myBookModel.getKEY(),myBookModel.getSYNC(), myBookModel.getUPLOAD_TIME()));
        holder.menu.setOnClickListener(view -> showMenu(holder, myBookModel));

    }public void unpublish(MyBookAdapter.MyBookHolder holder, String key){
        ref.child("PUBLISHED_BOOKS").child(key).removeValue()
                .addOnFailureListener(e -> Toast.makeText(holder.imageView.getContext(), "Something went wrong", Toast.LENGTH_LONG).show())
                .addOnSuccessListener(unused -> ref2.child(key).child("PUBLISHED").setValue(false)
                        .addOnSuccessListener(unused1 -> Toast.makeText(holder.imageView.getContext(), "Book is removed from explore section it is un published now", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e -> Toast.makeText(holder.imageView.getContext(), "Something went wrong", Toast.LENGTH_LONG).show()));
    }
    public void showMenu(MyBookAdapter.MyBookHolder holder, MyBookModel myBookModel){
        ListPopupWindow listPopupWindow = new ListPopupWindow(holder.materialCardView.getContext(), null, R.attr.listPopupWindowStyle);
        listPopupWindow.setAnchorView(holder.anchor);
        List<String> list = new ArrayList<>();
        list.add("SHARE");
        list.add("EDIT");
        list.add("BACK UP");
        if (!myBookModel.isPUBLISHED())
            list.add("PUBLISH");
        else
            list.add("UN PUBLISH");
        list.add("DELETE");
        ArrayAdapter adapter = new ArrayAdapter(holder.materialCardView.getContext(), R.layout.list_popup_item, list);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setBackgroundDrawable(holder.menu.getContext().getDrawable(
                R.drawable.custom_popup_menu));

        listPopupWindow.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i){
                case 0:
                    share(holder.menu.getContext(), myBookModel.getFILELINK());
                    break;
                case 1:
                    nextPage(holder.imageView.getContext(), myBookModel.getTITLE(), myBookModel.getAUTHOR(), myBookModel.getIMAGELINK(), myBookModel.getFILELINK(), myBookModel.getKEY(), myBookModel.getSYNC(), myBookModel.getUPLOAD_TIME());
                    break;
                case 2:

                    upload(holder, myBookModel.getKEY());
                    break;
                case 3:
                    if (myBookModel.isPUBLISHED())
                        unpublish(holder, myBookModel.getKEY());
                    else
                        showPopup(holder, myBookModel);
                    break;
                case 4:
                    popupdel(holder, myBookModel);
                    break;

            }
            listPopupWindow.dismiss();
        });

        listPopupWindow.show();

    }
    public void showPopup(MyBookAdapter.MyBookHolder holder, MyBookModel myBookModel){

        alert = new MaterialAlertDialogBuilder(holder.imageView.getContext());
        if (myBookModel.getFILELINK()==null){
            alert.setMessage("YOUR BOOK NEEDS TO BE BACKED UP BEFORE PUBLISHING\n" +
                    "Your book may be empty or not backed up.");
            alert.setPositiveButton("Backup", (dialogInterface, i) -> {
                    upload(holder, myBookModel.getKEY());
            });

        }
        else {
            alert.setMessage("All the contents of this book belongs to you, and only you" +
                    " and if the book contains any offensive, abusive, discriminating content is forbidden, if" +
                    " you publish this book then you are responsible for it." +
                    "We don,t encourage you to publish the book which contains these things.");
            alert.setNegativeButton("Accept & post", (dialogInterface, i) -> {
                if (myBookModel.getFILELINK()!=null){
                    publishBook(holder.imageView.getContext(), myBookModel.getTITLE(), myBookModel.getIMAGELINK(), myBookModel.getAUTHOR(), myBookModel.getDESCRIPTION(), myBookModel.getKEY(), myBookModel.getFILELINK());
                }
            });
        }


        alert.show();
    }
    public void popupdel(MyBookAdapter.MyBookHolder holder, MyBookModel myBookModel){
        alert = new MaterialAlertDialogBuilder(holder.imageView.getContext());

        alert.setMessage("All your book data will be deleted,\n" +
                "Once deleted cannot be retrieved");
        alert.setNegativeButton("delete", (dialogInterface, i) -> deleteBook(myBookModel.getIMAGELINK(), myBookModel.getTITLE(), myBookModel.getKEY()));
        alert.setPositiveButton("cancel", (dialogInterface, i) -> {

        });
        alert.show();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void publishBook(Context context, String title, String link, String author, String imname, String key, String filelink) {
        Query applesQuery = ref2.orderByChild("TITLE").equalTo(title);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    //appleSnapshot.getChildren();
                    //String key = String.valueOf((System.currentTimeMillis()/1000));
                    Log.e("data",appleSnapshot.getKey());
                    ref.child("PUBLISHED_BOOKS").child(key).child("TITLE").setValue(title);
                    ref.child("PUBLISHED_BOOKS").child(key).child("KEY").setValue(key);
                    ref.child("PUBLISHED_BOOKS").child(key).child("AUTHOR").setValue(author);
                    ref.child("PUBLISHED_BOOKS").child(key).child("IMAGELINK").setValue(link);
                    ref.child("PUBLISHED_BOOKS").child(key).child("RATINGS").setValue(0);
                    ref.child("PUBLISHED_BOOKS").child(key).child("IMAGENAME").setValue(imname);
                    ref.child("PUBLISHED_BOOKS").child(key).child("PUBLISHED").setValue(true);
                    ref.child("PUBLISHED_BOOKS").child(key).child("FILELINK").setValue(filelink);
                    ref2.child(Objects.requireNonNull(appleSnapshot.getKey())).child("PUBLISHED").setValue(true)
                            .addOnSuccessListener(unused -> {
                                Log.e("published",ref.getKey());
                                Toast.makeText(context, "Book Published",Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> Log.e("failed", e.getMessage()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DEL", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public MyBookHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_book_item, parent, false);
        return new MyBookHolder(view);
    }
    public void deleteBook(String link, String title, String key){
        Log.e("link",link+" kk");
        //firebaseStorage = FirebaseStorage.getInstance();
        photoRef = storageRef.child(FirebaseAuth.getInstance().getUid()+"/"+key+".json");
        photoRef.delete().addOnSuccessListener(aVoid -> {

            Log.d("DEL", "onSuccess: deleted file from firebase");
        }).addOnFailureListener(exception -> {
            // Uh-oh, an error occurred!
            Log.e("ERROR", exception.getMessage());
            //Toast.makeText(context,"Book Not Deleted", Toast.LENGTH_LONG).show();
        });
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("BOOKDATA")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        DatabaseReference ref2 = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("BOOKDATA")
                .child("PUBLISHED_BOOKS");
        Query applesQuery = ref.orderByChild("KEY").equalTo(key);
        Query ref2query = ref2.orderByChild("KEY").equalTo(key);
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();

                    Log.d("DEL", "onSuccess: deleted file");
                }
                deleteFromStorage(key);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DEL", "onCancelled", databaseError.toException());
            }
        });
        ref2query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    Log.d("DEL", "onSuccess: deleted file");
                }
                deleteFromStorage(key);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DEL", "onCancelled", databaseError.toException());
            }
        });
    }
    public void deleteFromStorage(String key){
        File file = new File(context.getFilesDir(),"/BookKeeper/"+key+".json");
        if (file.exists()) {
            Log.e("File "+key+" deleted", String.valueOf(file.delete()));
        }
    }

    public static class MyBookHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView title;
        ImageView imageView;

        View menu, anchor;


        MaterialCardView materialCardView;
        ProgressBar backupprogress;


        public MyBookHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.bimage);

            menu = itemView.findViewById(R.id.moremenu);
            anchor = itemView.findViewById(R.id.anchor);


            materialCardView = itemView.findViewById(R.id.mybookcard);

            backupprogress = itemView.findViewById(R.id.backupprogress);

            backupprogress.setVisibility(View.GONE);


        }
    }
    public void nextPage(Context context, String title, String author, String url, String fileurl, String key, String sync, String date){
        Intent intent = new Intent(context, AllBooks.class);
        intent.putExtra("title",title);
        intent.putExtra("author",author);
        intent.putExtra("fileurl",fileurl);
        intent.putExtra("url",url);
        intent.putExtra("key",key);
        intent.putExtra("sync",sync);
        intent.putExtra("date",date);
        context.startActivity(intent);
    }
    private void share(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Share Book");
        intent.putExtra(Intent.EXTRA_TEXT,"Check out this book which i am reading now\nClick here "+link);
        context.startActivity(Intent.createChooser(intent, "Share via"));

    }
    public void upload(MyBookAdapter.MyBookHolder holder, String key){
        holder.backupprogress.setIndeterminate(true);
        holder.backupprogress.setVisibility(View.VISIBLE);
        File file = new File(holder.materialCardView.getContext().getFilesDir(),
                File.separator + "BookKeeper/"+key+".json");


        if (file.isFile())
        {

                InputStream stream;
                try {
                    //stream = new FileInputStream(new File("/sdcard/BookKeeper/"+key+".json"));
                    stream = new FileInputStream(new File(holder.materialCardView.getContext().getFilesDir(),
                            File.separator + "/BookKeeper/"+key+".json"));
                    UploadTask uploadTask = storageRef.child(user.getUid()+"/"+key+".json").putStream(stream);
                    uploadTask.addOnFailureListener(exception -> {
                        Log.d("error",exception.getMessage());
                    }).addOnSuccessListener(taskSnapshot -> {
                        storageRef.child(user.getUid()+"/"+key+".json").getDownloadUrl().addOnSuccessListener(uri -> {
                            ref2.child(key).child("SYNC").setValue("Not");
                            ref2.child(key).child("FILELINK").setValue(uri.toString()).addOnFailureListener(e -> {
                                Log.e("Error",e.getMessage());
                                holder.backupprogress.setVisibility(View.GONE);
                            }).addOnSuccessListener(unused -> {
                                Log.e("File","Uploaded");
                                holder.backupprogress.setVisibility(View.GONE);
                                Toast.makeText(holder.menu.getContext(), "Backup Successful",Toast.LENGTH_LONG).show();
                            });
                        }).addOnFailureListener(e -> {
                            Log.e("Error",e.getMessage());
                            holder.backupprogress.setVisibility(View.GONE);
                        });

                        Log.e("onSuccess",taskSnapshot.getMetadata().getName());

                    }).addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        if (progress>1){
                            holder.backupprogress.setIndeterminate(false);
                            holder.backupprogress.setProgress((int)progress);
                        }

                    });

                } catch (FileNotFoundException e) {
                    Log.e("Error",e.getMessage());
                    Toast.makeText(holder.materialCardView.getContext(), "No Book file is available or Book is empty", Toast.LENGTH_LONG).show();
                }


        }
        else {

            Log.e("file","not present");
            Toast.makeText(holder.materialCardView.getContext(), "No Book file is available or Book is empty", Toast.LENGTH_LONG).show();
            holder.backupprogress.setVisibility(View.GONE);
        }


    }
}
