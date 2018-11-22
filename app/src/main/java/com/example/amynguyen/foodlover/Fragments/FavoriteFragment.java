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

import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.CustomView.NoScrollListView;
import com.example.amynguyen.foodlover.Database.MyDBHandler;
import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteFragment extends android.support.v4.app.Fragment {
    List<Business> businessInfo = new ArrayList<>();
    Dialog myDialog;
    Button btnView;
    Button btnFavorite;
    MyDBHandler db;

    BusinessLineItemAdapter myAdapter;
    public static FavoriteFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        favoriteFragment.setArguments(args);
        return favoriteFragment;
    }



    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        NoScrollListView myList = (NoScrollListView) view.findViewById(R.id.listViewFavorite);

        List<Business> businessInfoTest = new ArrayList<>();
        db = MyDBHandler.getInstance(getContext());
        final BusinessLineItemAdapter myAdapter;
        businessInfoTest = db.loadFavorite();

        myAdapter = new BusinessLineItemAdapter(businessInfoTest, getContext(), db);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                myDialog = new Dialog(getContext());
                myDialog.setContentView(R.layout.popup_view);
                myDialog.show();
                btnView = myDialog.findViewById(R.id.buttonView);
                final Business currentItem = (Business) myAdapter.getItem(i);
                btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", currentItem.getAddress());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        getContext().startActivity(intent);
                        if(!db.isBusinessexistFromRecent(((Business) myAdapter.getItem(i)).getBusinessId())) {
                            db.addToRecent(myAdapter, i);
                        }
                        else {
                            db.deleteFromRecent(myAdapter, i);
                            db.addToRecent(myAdapter, i);
                        }
                        if(db.loadRecent().size() > 10) {
                            db.deleteFirstRecordFromRecent();
                        }
                    }
                });
                btnFavorite = myDialog.findViewById(R.id.buttonFavorite);
                btnFavorite.setText("Remove From Favorite");
                btnFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.deleteFromFavorite(myAdapter, i);
                        myAdapter.deleteItem(i);
                        myDialog.dismiss();
                    }
                });
            }
        });

        return view;
    }
}
