package com.subhamgupta.bookkeeper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;


public class MyAdapter extends FirebaseRecyclerAdapter<Model, MyAdapter.MyViewHolder>  {




    public MyAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);

    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model) {
        Glide.with(holder.imageView.getContext())
                .load(model.getIMAGELINK())

                .error(R.drawable.ic_error)
                .thumbnail(0.005f)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .into(holder.imageView);

        holder.author.setText(model.getAUTHOR());
        holder.title.setText(model.getTITLE());
        holder.share.setOnClickListener(view -> share(holder.share.getContext(), model.getFILELINK()));

        holder.materialCardView.setOnClickListener(view -> nextPage(holder.imageView.getContext(), model.getTITLE(), model.getAUTHOR(), model.getIMAGELINK(), model.getFILELINK(), model.getKEY()));


    }
    String key ="";



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView title, description;
        ImageView imageView;
        View dislike, like, share;
        RelativeLayout relativeLayout;

        MaterialCardView materialCardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.bimage);

            description = itemView.findViewById(R.id.bitext);
            materialCardView = itemView.findViewById(R.id.bookcard);
            share = itemView.findViewById(R.id.exshare);
            description.setActivated(false);



        }
    }
    public void nextPage(Context context, String title, String author, String url, String fileurl, String key){
        Intent intent = new Intent(context,ExploreBooks.class);
        intent.putExtra("title",title);
        intent.putExtra("author",author);
        intent.putExtra("url",url);
        intent.putExtra("fileurl",fileurl);
        intent.putExtra("key",key);
        context.startActivity(intent);
    }
    private void share(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Share Book");
        intent.putExtra(Intent.EXTRA_TEXT,"Check out this book which i am reading now\nClick here "+link);
        context.startActivity(Intent.createChooser(intent, "Share via"));

    }

}
