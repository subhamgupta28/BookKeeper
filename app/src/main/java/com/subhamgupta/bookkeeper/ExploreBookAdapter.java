package com.subhamgupta.bookkeeper;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ExploreBookAdapter extends RecyclerView.Adapter<ExploreBookAdapter.ExploreBookHolder> {
    List<ReadBookModel> bookchap;

    public ExploreBookAdapter(List<ReadBookModel> bookchap) {
        this.bookchap = bookchap;
    }

    @NonNull
    @Override
    public ExploreBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_book_item, parent, false);
        return new ExploreBookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ExploreBookAdapter.ExploreBookHolder holder, int position) {
        holder.description.setText(Html.fromHtml(bookchap.get(position).getDescription()));
        holder.chapter.setText(bookchap.get(position).getChapter());
        holder.page.setText(String.valueOf(bookchap.get(position).getPageNo()));
    }

    @Override
    public int getItemCount() {
        return bookchap.size();
    }

    public static class ExploreBookHolder extends RecyclerView.ViewHolder{
        TextView chapter, description, page;
        MaterialCardView materialCardView;
        public ExploreBookHolder(@NonNull View itemView) {
            super(itemView);
            chapter = itemView.findViewById(R.id.exchapter);
            description = itemView.findViewById(R.id.exdescribe);
            page = itemView.findViewById(R.id.pgid);
            materialCardView = itemView.findViewById(R.id.exreadcard);
            description.setMovementMethod(new ScrollingMovementMethod());
        }
    }
}
