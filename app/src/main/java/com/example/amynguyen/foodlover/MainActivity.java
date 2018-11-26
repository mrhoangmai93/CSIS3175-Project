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
import android.widget.SearchView;
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

    private LocationManager mLocationManager;
    private static final String[] LOCATION_PERMS={
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };

    private FragNavController fragNavController;
    ;

    // Indices to fragments
    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;
    List<Fragment> fragments = new ArrayList<>(3);
    private static final int LOCATION_REQUEST=1340;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //list of fragments

        //add fragments to list
        fragments.add(SearchFragment.newInstance(0));
        fragments.add(FavoriteFragment.newInstance(0));
        fragments.add(RecentFragment.newInstance(0));

        //link fragments to container
        fragNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.fragment_container)
                .rootFragments(fragments)
                .defaultTransactionOptions(FragNavTransactionOptions.newBuilder().transition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).build())
                .build();

        // Init location manager
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!canAccessLocation()) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, LOCATION_REQUEST);
        }

        // Init image loader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this.getBaseContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            mLocationManager.removeUpdates(MainActivity.this);
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
        }
    }
    private boolean canAccessLocation() {
        return(hasPermission(ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

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
        // Switch between fragments
        switch (item.getItemId()) {
            case R.id.navigation_search:
                fragNavController.switchTab(TAB_FIRST);

                // Reset SearchView text
                SearchView foodSearch = fragments.get(0).getView().findViewById(R.id.searchViewRestaurant);
                SearchView locationSearch = fragments.get(0).getView().findViewById(R.id.searchViewLocation);
                foodSearch.onActionViewCollapsed();
                locationSearch.onActionViewCollapsed();
                break;

            case R.id.navigation_favorite:
                fragNavController.switchTab(TAB_SECOND);
                break;

            case R.id.navigation_recent:
                fragNavController.switchTab(TAB_THIRD);

                break;
        }
        return true;
    }
}
