package com.subhamgupta.bookkeeper.fragments;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subhamgupta.bookkeeper.R;
import com.subhamgupta.bookkeeper.adapters.MyBookAdapter;
import com.subhamgupta.bookkeeper.models.MyBookModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class MyBooksFragment extends Fragment {



    private View view;
    DatabaseReference databaseReference;
    SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    MyBookAdapter myBookAdapter;
    int bookCount;
    TextView tvcheck, netStatus;
    ProgressBar progressBar;
    ConnectivityManager connectivityManager;
    NetworkStatsManager networkStatsManager;
    LottieAnimationView lottieAnimationView;

    public MyBooksFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_books, container, false);
        recyclerView = view.findViewById(R.id.mybookrecycler);
        progressBar = view.findViewById(R.id.progress);
        tvcheck = view.findViewById(R.id.tvcheck);
        swipeRefreshLayout = view.findViewById(R.id.swipemybooks);
        netStatus = view.findViewById(R.id.mynetstatus);
        lottieAnimationView = view.findViewById(R.id.loadingview);
        //progressBar.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        netStatus.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.setAnimation(R.raw.loadinganime);
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
        networkStatsManager = (NetworkStatsManager) getActivity().getSystemService(Context.NETWORK_STATS_SERVICE);
        netStats();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                getBook();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {

            getBook();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            Log.e( "onCreateView: ",dtf.format(now) );
        }
        return view;

    }



    public void getBook() throws Exception{

        mAuth = FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("BOOKDATA").child(Objects.requireNonNull(mAuth.getUid()));
        Log.e("snap", String.valueOf(databaseReference.get()));
        FirebaseRecyclerOptions<MyBookModel> options
                = new FirebaseRecyclerOptions.Builder<MyBookModel>()
                .setQuery(databaseReference, MyBookModel.class)
                .build();
        myBookAdapter = new MyBookAdapter(options);
        recyclerView.setAdapter(myBookAdapter);
        Log.e("ccc", String.valueOf(myBookAdapter.getItemCount()));
        myBookAdapter.startListening();
        swipeRefreshLayout.setRefreshing(false);
        checkBookAvailable();
    }
    long count = 0;

    public void checkBookAvailable() throws Exception{

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                Log.e("count", String.valueOf(count));
//                Object object = snapshot.getValue(Object.class);
                /*String json = new Gson().toJson(object);
                //saveForOfflineReading(json);
                Log.e("json",json);*/
                if (!snapshot.exists()){
                    tvcheck.setText("No book available.\nCreate new book by clicking plus icon.");
                    progressBar.setVisibility(View.INVISIBLE);
                    lottianim("");
                    //lottieAnimationView.setVisibility(View.GONE);

                }
                else {
                    bookCount = (int) snapshot.getChildrenCount();
                    //Log.e("count1",bookCount+ " counted");
                    tvcheck.setVisibility(View.INVISIBLE);
                    lottianim("");
                    //lottieAnimationView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    netStatus.setVisibility(View.GONE);


                }
                if (count==0)
                    netStatus.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.e("Error",error.getDetails());
            }
        });
    }

    public void search(String s) throws Exception{
        Log.e("searchmybook",s);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("BOOKDATA").child(Objects.requireNonNull(mAuth.getUid()));
        FirebaseRecyclerOptions<MyBookModel> options
                = new FirebaseRecyclerOptions.Builder<MyBookModel>()
                .setQuery(databaseReference.orderByChild("TITLE").startAt(s).endAt(s+"\u8fff"), MyBookModel.class)
                .build();

        myBookAdapter = new MyBookAdapter(options);
        recyclerView.setAdapter(myBookAdapter);
        myBookAdapter.startListening();





    }
    public void netStats(){
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        //tvcheck.setVisibility(View.INVISIBLE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            netStatus.setVisibility(View.GONE);
        }
        else {

            //netStatus.setVisibility(View.VISIBLE);
            netStatus.setText("You are Ofline");
           lottianim("y");

        }
    }
    public void lottianim(String string){
        if (string.equals("y")){
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.setAnimation(R.raw.nonet);
            lottieAnimationView.loop(true);
            lottieAnimationView.playAnimation();
        }
        else {
            lottieAnimationView.setVisibility(View.GONE);
        }

    }




}