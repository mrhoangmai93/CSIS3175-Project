package com.example.amynguyen.foodlover;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amynguyen.foodlover.yelpAPI.YelpHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity  implements LocationListener {

    private TextView mTextMessage;
    private LocationManager mLocationManager;
    private static final String[] LOCATION_PERMS={
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };
    private static final int LOCATION_REQUEST=1340;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!canAccessLocation()) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMS, LOCATION_REQUEST);
        }
        this.getBusinessList();
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
            this.getBusinessList();
        }
    }
    public void getBusinessList() {
        try {
            // Get last known location
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Execute if its the first location
                // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
                String coordinate = location.getLatitude() + "," + location.getLongitude();
                System.out.println(coordinate);
                // Print// create Yelp Helper instance
                final YelpHelper yelpHelper = new YelpHelper();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // execute command
                        JsonObject result = yelpHelper.getBusinessQuery();
                    }
                };
                new Thread(runnable).start();
        } catch (SecurityException ex) {
            System.out.println(ex);
        }
    }
    private boolean canAccessLocation() {
        return(hasPermission(ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
}
