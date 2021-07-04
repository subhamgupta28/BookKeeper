package com.subhamgupta.bookkeeper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FavouritesBookFragment extends Fragment {
    /*TODO: 1.Make this fragment as favourites book saver
    *       2.create recycler adapter
    *       2.extra view for image editor*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites_book, container, false);


        return view;
    }
}