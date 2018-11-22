package com.example.amynguyen.foodlover.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.CustomView.NoScrollListView;
import com.example.amynguyen.foodlover.Database.MyDBHandler;
import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentFragment extends android.support.v4.app.Fragment {

    MyDBHandler db;


    public static RecentFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        RecentFragment recentFragment = new RecentFragment();
        recentFragment.setArguments(args);
        return recentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        NoScrollListView myList = (NoScrollListView) view.findViewById(R.id.listViewRecent);
        List<Business> businessInfoTest = new ArrayList<>();
        db = MyDBHandler.getInstance(getContext());
        final BusinessLineItemAdapter myAdapter;
        businessInfoTest = db.loadRecent();


        myAdapter = new BusinessLineItemAdapter(businessInfoTest, getContext(), db);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                final Business currentItem = (Business) myAdapter.getItem(i);
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", currentItem.getAddress());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
                db.deleteFromRecent(myAdapter, i);
                db.addToRecent(myAdapter, i);
            }
        });
        //BusinessLineItemAdapter myAdapter = new BusinessLineItemAdapter(businessInfo, getContext());
        // myList.setAdapter(myAdapter);

        return view;

    }
}
