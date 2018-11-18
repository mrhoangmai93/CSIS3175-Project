package com.example.amynguyen.foodlover.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.CustomView.NoScrollListView;
import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends android.support.v4.app.Fragment {
    List<Business> businessInfo = new ArrayList<>();
    public void addResult() {
/*        businessInfo.add(new Business("a", "b", "c", 3.0,
                "https://upload.wikimedia.org/wikipedia/en/a/ae/Love_TV_Logo.png"));
        businessInfo.add(new Business("d", "b", "c", 3.0,
                "https://upload.wikimedia.org/wikipedia/en/a/ae/Love_TV_Logo.png"));
        businessInfo.add(new Business("e", "b", "c", 3.0,
                "https://upload.wikimedia.org/wikipedia/en/a/ae/Love_TV_Logo.png"));*/
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        NoScrollListView myList = (NoScrollListView) view.findViewById(R.id.listViewFavorite);
        addResult();
        BusinessLineItemAdapter myAdapter = new BusinessLineItemAdapter(businessInfo, getContext());
        myList.setAdapter(myAdapter);

        return view;
    }
}
