package com.example.amynguyen.foodlover.Fragments;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amynguyen.foodlover.R;
import com.example.amynguyen.foodlover.yelpAPI.YelpHelper;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SearchFragment extends android.support.v4.app.Fragment implements LocationListener {
    View mainView;
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
                yelpHelper.setCoordinate(coordinate);
            }
            // Print// create Yelp Helper instance
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // execute command
                    JsonObject result = yelpHelper.getBusinessQuery();
                    System.out.println(result);
                }
            };
            new Thread(runnable).start();
        } catch (SecurityException ex) {
            System.out.println(ex);
        }
    }

    //ActivityCompat.checkSelfPermission(getActivity(),perm));
}
