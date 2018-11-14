package com.example.amynguyen.foodlover.Fragments;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.CustomListView.NoScrollListView;
import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;
import com.example.amynguyen.foodlover.yelpAPI.YelpHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

public class SearchFragment extends android.support.v4.app.Fragment implements LocationListener {
    View mainView;
    EditText distanceInput;
    ImageView favorite;
    BusinessLineItemAdapter myAdapter;
    NoScrollListView myList;
    ArrayList<Business> businessInfo = new ArrayList<Business>();
    private TextView mTextMessage;
    private LocationManager mLocationManager;
    private static final String[] LOCATION_PERMS={
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };


    private static final int LOCATION_REQUEST=1340;
    YelpHelper yelpHelper = new YelpHelper();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mainView = view;

        distanceInput = (EditText) view.findViewById(R.id.editTextDistance);
        distanceInput.setFocusable(false);
        distanceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distanceInput.setFocusableInTouchMode(true);
            }
        });

        search(view);
        //favorite = (ImageView) view.findViewById(R.id.imageViewFavorite);


        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return view;
    }

    public void search(View view)    {
        final SearchView locationSearch = (SearchView) view.findViewById(R.id.searchViewLocation);
        final SearchView foodSearch = (SearchView) view.findViewById(R.id.searchViewRestaurant);
        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationSearch.setVisibility(view.VISIBLE);
                foodSearch.onActionViewExpanded();
                getBusinessList();
                locationSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        locationSearch.onActionViewExpanded();
                    }
                });
            }
        });

        foodSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                locationSearch.setVisibility(view.VISIBLE);

            }
        });

        myList = (NoScrollListView) mainView.findViewById(R.id.listViewResult);
        myAdapter = new BusinessLineItemAdapter(businessInfo, getContext());


        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                favorite = (ImageView) view.findViewById(R.id.imageViewFavorite);

                    favorite.setImageResource(R.drawable.ic_favorite);
            }
        });


    }


    @Override
    public void onLocationChanged(Location location) {
        // this.getBusinessList();
        if (location != null) {
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int index = 0;
        Map<String, Integer> PermissionsMap = new HashMap<String, Integer>();
        for (String permission : permissions){
            PermissionsMap.put(permission, grantResults[index]);
            index++;
        }
        if((PermissionsMap.get(ACCESS_FINE_LOCATION) != 0)
                || PermissionsMap.get(ACCESS_COARSE_LOCATION) != 0){
            // Toast.makeText(this, "Location permission is a must", Toast.LENGTH_SHORT).show();
            // finish();
        }else {
            this.getBusinessList();
        }
    }
    public void getBusinessList() {
        try {
            // Get last known location
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Execute if its the first location
            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            if(location != null) {
                String coordinate = location.getLatitude() + "," + location.getLongitude();
                // yelpHelper.setCoordinate(coordinate);
                yelpHelper.setCoordinate(String.valueOf("49.20390,-122.91308"));
            }
            // Print// create Yelp Helper instance
            final Handler handler = new Handler(Looper.getMainLooper());
           Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // execute command
                    ArrayList<Business> testList = new ArrayList<>();
                    JsonObject result = yelpHelper.getBusinessQuery();
                    System.out.println(yelpHelper.getCoordinate());
                    if(result != null) {
                        JsonArray arr = result.getAsJsonArray("businesses");
                        for (JsonElement pa : arr) {
                            JsonObject business = pa.getAsJsonObject();
                            String name = business.get("name").getAsString();
                            JsonObject location = business.get("location").getAsJsonObject();
                            String address = location.get("address1").getAsString();
                            JsonArray categories = business.get("categories").getAsJsonArray();
                            JsonObject categoryObject = categories.get(0).getAsJsonObject();
                            String category = categoryObject.get("title").getAsString();
                            Double rating = business.get("rating").getAsDouble();
                            String imageURL = business.get("image_url").getAsString();
                            testList.add(new Business(name, address, category, rating, imageURL));
                            System.out.println("Name:" + name + "," + "rating:" + rating);
                        }
                        final ArrayList<Business> finishList = testList;
                        // myAdapter.refresAdapter(businessInfo);
                        handler.post(new Runnable() {
                            public void run() {
                                myAdapter.refresAdapter(finishList);
                                // System.out.println("Tao day");
                            }
                        });
                    }
                }
            };
            new Thread(runnable).start();
        } catch (SecurityException ex) {
            System.out.println(ex);
        }
    }
    //ActivityCompat.checkSelfPermission(getActivity(),perm));
}
