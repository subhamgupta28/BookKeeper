package com.subhamgupta.bookkeeper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExploreFrangments extends Fragment {


    DatabaseReference databaseReference, databaseReference1;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    MyAdapter myAdapter;
    int bookCount;
    TextView tvcheck, exnetStatus;
    ProgressBar progressBar;
    ConnectivityManager connectivityManager;
    LottieAnimationView loadingview;


    public ExploreFrangments() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_explore_frangments, container, false);
        recyclerView = view.findViewById(R.id.explorerecyler);
        progressBar = view.findViewById(R.id.exprogress);
        tvcheck = view.findViewById(R.id.extvcheck);
        exnetStatus = view.findViewById(R.id.netstatus);
        swipeRefreshLayout = view.findViewById(R.id.swipeexplorebooks);
        loadingview = view.findViewById(R.id.exloadingview);
        exnetStatus.setVisibility(View.GONE);
        //progressBar.setVisibility(View.VISIBLE);
        netStat();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));

        swipeRefreshLayout.setOnRefreshListener(this::getBooks);
        getBooks();
        return view;

    }
    public void getBooks(){
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("BOOKDATA").child("PUBLISHED_BOOKS");
        FirebaseRecyclerOptions<Model> options
                = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(databaseReference.orderByChild("PUBLISHED").equalTo(true), Model.class)
                .build();


        myAdapter = new MyAdapter(options);
        recyclerView.setAdapter(myAdapter);
        myAdapter.startListening();
        swipeRefreshLayout.setRefreshing(false);
        checkBookAvailable();
    }
    long excount = 0;

    public void checkBookAvailable(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                excount = snapshot.getChildrenCount();
                if (!snapshot.exists()){
                    tvcheck.setVisibility(View.VISIBLE);
                    tvcheck.setText("No Book Available.");
                    progressBar.setVisibility(View.GONE);
                    loadingview.setVisibility(View.GONE);

                }
                else {
                    bookCount = (int) snapshot.getChildrenCount();
                    //Log.e("count1",bookCount+ " counted");
                    tvcheck.setVisibility(View.INVISIBLE);
                    loadingview.setVisibility(View.GONE);
                    exnetStatus.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void search(String s){
        //Log.e("searchitem",s);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("BOOKDATA").child("PUBLISHED_BOOKS");
        FirebaseRecyclerOptions<Model> option
                = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(databaseReference1.orderByChild("TITLE").startAt(s).endAt(s+"\u8fff"), Model.class)
                .build();
        myAdapter = new MyAdapter(option);
        recyclerView.setAdapter(myAdapter);
        myAdapter.startListening();
    }
    public void netStat(){
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        //tvcheck.setVisibility(View.INVISIBLE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            exnetStatus.setVisibility(View.GONE);
        }
        else {

            exnetStatus.setVisibility(View.VISIBLE);
            exnetStatus.setText("You are Ofline");
        }
    }



}