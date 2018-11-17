package com.example.amynguyen.foodlover.Fragments;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

public class SearchFragment extends android.support.v4.app.Fragment {
    View mainView;
    EditText distanceInput;
    ImageView favorite;
    BusinessLineItemAdapter myAdapter;
    NoScrollListView myList;
    int totalItemCount = 30;
    ArrayList<Business> businessInfo = new ArrayList<Business>();
    private TextView mTextMessage;
    private LocationManager mLocationManager;
    private static final String[] LOCATION_PERMS={
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };

    public Handler mHandler;
    public View footView;
    public boolean isLoading = false;
    public int currentId = 20;


    private static final int LOCATION_REQUEST=1340;
    YelpHelper yelpHelper = new YelpHelper();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mainView = view;

        footView = inflater.inflate(R.layout.footer_view, null);
        mHandler = new MyHandler();

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

    public void search(final View view)    {
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

                    favorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            favorite.setImageResource(R.drawable.ic_favorite); }

                    });
            }
        });

        myList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(absListView.getLastVisiblePosition() == totalItemCount - 1
                        && myList.getCount() >= 20 && isLoading == false)   {
                    isLoading = true;

                    Thread thread = new ThreadGetMoreData();
                    thread.start();
                }
            }
        });

    }
    public void getBusinessList() {
        try {
            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            // Get last known location
            Criteria c=new Criteria();
            //if we pass false than
            //it will check first satellite location than Internet and than Sim Network
            String provider=mLocationManager.getBestProvider(c, false);
            Location location = mLocationManager.getLastKnownLocation(provider);
            //Location location = getLastKnownLocation();
            // Execute if its the first location
            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            // System.out.println(location);
            if(location != null) {
                String coordinate = location.getLatitude() + "," + location.getLongitude();
                // yelpHelper.setCoordinate(coordinate);
                yelpHelper.setCoordinate("49.20390,-122.91308");
            }
            // Print// create Yelp Helper instance
            final Handler handler = new Handler(Looper.getMainLooper());
           Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // execute command
                    ArrayList<Business> testList = new ArrayList<>();
                    JsonObject result = yelpHelper.getBusinessQuery();
                    // System.out.println(yelpHelper.getCoordinate());
                    if(result != null) {
                        JsonArray arr = result.getAsJsonArray("businesses");
                        for (JsonElement pa : arr) {
                            testList.add(getBusinessFromJson(pa));
                            // System.out.println("Name:" + name + ", " + "rating:" + rating);
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

    public Business getBusinessFromJson(JsonElement pa) {
        String category = "";
        String address = "";
        JsonObject business = pa.getAsJsonObject();
        String name = business.get("name").getAsString();
        JsonObject location = business.get("location").getAsJsonObject();
        address = location.get("address1").getAsString()
                + ", " + location.get("city").getAsString()
                + ", " + location.get("state").getAsString();
                //+ ", " + location.get("zip_code").getAsString()
                //+ ", " + location.get("country").getAsString();
        JsonArray categories = business.get("categories").getAsJsonArray();
        for (JsonElement ca : categories) {
            JsonObject caObj = ca.getAsJsonObject();
            category += caObj.get("title").getAsString() + ", ";
        }
        category = category.substring(0, category.length() - 1);
        Double rating = business.get("rating").getAsDouble();
        String imageURL = business.get("image_url").getAsString();
        Business businessObj = new Business(name, address, category, rating, imageURL);
        System.out.println(business.get("distance").getAsDouble() * 0.001);
        businessObj.setDistanceFromCurrentLocation(String.valueOf(Math.round((business.get("distance").getAsDouble() * 0.001) * 100.0) / 100.0) + " km");
        return businessObj;
    }
    private Location getLastKnownLocation() {
        Location bestLocation = null;
        try {
            mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        } catch (SecurityException ex) {
        System.out.println(ex);
    }
        return bestLocation;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)   {
                case 0:
                    myList.addFooterView(footView);
                    break;
                case 1:
                    myAdapter.addListItemToAdapter((List<Business>)msg.obj);
                    myList.removeFooterView(footView);
                    isLoading = false;
                    break;

                default:
                    break;


            }
        }
    }

    private List<Business> getMoreData()   {
        List<Business> lst = new ArrayList<>();
        lst.add(new Business("a", "b", "c", 3.0,
                "https://upload.wikimedia.org/wikipedia/en/a/ae/Love_TV_Logo.png"));
        lst.add(new Business("d", "b", "c", 3.0,
                "https://upload.wikimedia.org/wikipedia/en/a/ae/Love_TV_Logo.png"));
        lst.add(new Business("e", "b", "c", 3.0,
                "https://upload.wikimedia.org/wikipedia/en/a/ae/Love_TV_Logo.png"));
        return lst;
    }

    public class ThreadGetMoreData extends Thread   {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);

            List<Business> lstResult = getMoreData();
            try
            {
                Thread.sleep(3000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);

        }
    }
}



//ActivityCompat.checkSelfPermission(getActivity(),perm));