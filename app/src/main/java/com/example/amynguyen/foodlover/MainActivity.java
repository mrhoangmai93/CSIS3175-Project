package com.example.amynguyen.foodlover;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageView;

import com.example.amynguyen.foodlover.Database.MyDBHandler;
import com.example.amynguyen.foodlover.Fragments.FavoriteFragment;
import com.example.amynguyen.foodlover.Fragments.RecentFragment;
import com.example.amynguyen.foodlover.Fragments.SearchFragment;
import com.example.amynguyen.foodlover.yelpAPI.YelpHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity  implements LocationListener,BottomNavigationView.OnNavigationItemSelectedListener  {

    private TextView mTextMessage;
    private LocationManager mLocationManager;
    private static final String[] LOCATION_PERMS={
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };

    private FragNavController fragNavController;
    ;

    //indices to fragments
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;

    private static final int LOCATION_REQUEST=1340;
    Fragment fragment = new SearchFragment();
    YelpHelper yelpHelper = new YelpHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FragNav
        //list of fragments
        List<Fragment> fragments = new ArrayList<>(3);
        //add fragments to list
        fragments.add(SearchFragment.newInstance(0));
        fragments.add(FavoriteFragment.newInstance(0));
        fragments.add(RecentFragment.newInstance(0));

        //link fragments to container
        fragNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.fragment_container)
                .rootFragments(fragments)
                .defaultTransactionOptions(FragNavTransactionOptions.newBuilder().transition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).build())
                .build();

        // this.getBusinessList();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!canAccessLocation()) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, LOCATION_REQUEST);
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            //Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // System.out.println(location);
        }catch (SecurityException exception) {
            System.out.println(exception);
        }
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this.getBaseContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        // loadFragment(fragment);

        navigation.setOnNavigationItemSelectedListener(this);
        try {
            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            System.out.println(location);
        }catch (SecurityException exception) {
            System.out.println(exception);
        }
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
            Toast.makeText(this, "Location permission is a must", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            // this.getBusinessList();
        }
    }
    private boolean canAccessLocation() {
        return(hasPermission(ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

/*    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }*/

    @Override
    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() > 1) {
            fragNavController.popFragment();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        fragNavController.onSaveInstanceState(outState);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_search:
                //fragment = new SearchFragment();
                fragNavController.switchTab(TAB_FIRST);
                break;

            case R.id.navigation_favorite:
                //fragment = new FavoriteFragment();
                fragNavController.switchTab(TAB_SECOND);
                break;

            case R.id.navigation_recent:
                //fragment = new RecentFragment();
                fragNavController.switchTab(TAB_THIRD);
                break;



        }
        // return loadFragment(fragment);
        return true;
    }
}
