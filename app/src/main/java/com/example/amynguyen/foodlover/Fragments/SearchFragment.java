package com.example.amynguyen.foodlover.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.amynguyen.foodlover.R;

public class SearchFragment extends android.support.v4.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        search(view);
        return view;
    }

    public void search(View view)    {
        final SearchView locationSearch = (SearchView) view.findViewById(R.id.searchViewLocation);
        SearchView foodSearch = (SearchView) view.findViewById(R.id.searchViewRestaurant);
        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationSearch.setVisibility(view.VISIBLE);
            }
        });

        foodSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                locationSearch.setVisibility(view.VISIBLE);
            }
        });
    }










}
