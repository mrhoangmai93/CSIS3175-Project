package com.example.amynguyen.foodlover.Fragments;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.CustomView.NoScrollListView;
import com.example.amynguyen.foodlover.CustomView.ScrollViewExt;
import com.example.amynguyen.foodlover.Interfaces.ScrollViewListener;
import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;
import com.example.amynguyen.foodlover.yelpAPI.YelpHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

public class SearchFragment extends android.support.v4.app.Fragment implements ScrollViewListener {
    View mainView;
    EditText distanceInput;
    ImageView favorite;
    BusinessLineItemAdapter myAdapter;
    NoScrollListView myList;
    //int totalItemCount;
    ArrayList<Business> businessInfo = new ArrayList<Business>();
    private TextView mTextMessage;
    private LocationManager mLocationManager;
    SearchView locationSearch;
    SearchView foodSearch;
    private static final String[] LOCATION_PERMS = {
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };

    public Handler mHandler;
    public View footView;
    ScrollViewExt scroll;
    public boolean isLoading = false;
    public int offset = 2;

    private static final int LOCATION_REQUEST = 1340;
    YelpHelper yelpHelper = new YelpHelper();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mainView = view;

        footView = inflater.inflate(R.layout.footer_view, null);
        // mHandler = new MyHandler();

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

    public void search(View view) {
        locationSearch = (SearchView) view.findViewById(R.id.searchViewLocation);
        foodSearch = (SearchView) view.findViewById(R.id.searchViewRestaurant);
        final Button btnSearch = (Button) view.findViewById(R.id.btnSearch);

        // Set scroll view listener
        scroll = (ScrollViewExt) view.findViewById(R.id.scrollView);
        scroll.setScrollViewListener(this);

        // Set adapter for result
        myList = (NoScrollListView) mainView.findViewById(R.id.listViewResult);
        //myAdapter = new BusinessLineItemAdapter(businessInfo, getContext());
        // myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                favorite = (ImageView) view.findViewById(R.id.imageViewFavorite);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favorite.setImageResource(R.drawable.ic_favorite);
                    }

                });
            }
        });


        // Set listener
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("dkm");
                //System.out.println(foodSearch.getQuery().toString().equals(""));
                // if(foodSearch.getQuery().toString() == "") Toast.makeText(view.getContext(), "dfsdfsdf", Toast.LENGTH_SHORT).show();
                // else getBusinessList();
                getBusinessList();
            }
        });


        // Expand location search when foodSearch on focus
        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationSearch.setVisibility(view.VISIBLE);
                foodSearch.onActionViewExpanded();
                // getBusinessList();

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

        /*myList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isLoading) {
                    if (totalItemCount > previousTotal) {
                        // the loading has finished
                        isLoading = false;
                        previousTotal = totalItemCount;
                    }
                }
                System.out.println("visibleItemcount " + visibleItemCount + firstVisibleItem);
                System.out.println("totalItemCount " + totalItemCount);
                // check if the List needs more data
                if (!isLoading && ((firstVisibleItem + visibleItemCount) >= (totalItemCount + offset))) {
                    isLoading = true;
                    myList.addFooterView(footView);

// List needs more data. Go fetch !!
                    getMoreData();
                }

*//*                if(absListView.getLastVisiblePosition() == totalItemCount - 1
                        && myList.getCount() >= offset && isLoading == false)   {
                    isLoading = true;
                    // Add loading bar
                    myList.addFooterView(footView);

                    // Get more data from yelp

                    getMoreData();*//*
                // Thread thread = new ThreadGetMoreData();
                // thread.start();
*//*            }
        }
    });*//*

            }
        });*/
    }

    public void getBusinessList() {
        try {
            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            // Get last known location
            Criteria c = new Criteria();
            //if we pass false than
            //it will check first satellite location than Internet and than Sim Network
            String provider = mLocationManager.getBestProvider(c, false);
            Location location = mLocationManager.getLastKnownLocation(provider);
            //Location location = getLastKnownLocation();
            // Execute if its the first location
            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
            // System.out.println(location);

            // Reset offset
            yelpHelper.setOffset(0);
            // Set restaurant query
            yelpHelper.setTerm(foodSearch.getQuery().toString());
            // Set location
            if (location != null) {
                String coordinate = location.getLatitude() + ", " + location.getLongitude();
                yelpHelper.setCoordinate(coordinate);
                //yelpHelper.setCoordinate("49.20390,-122.91308");
                // System.out.println(coordinate);
            }
            if (!locationSearch.getQuery().toString().equals("")) yelpHelper.setLocation("");
            else yelpHelper.setLocation(locationSearch.getQuery().toString());

            // Print// create Yelp Helper instance
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // execute command
                    ArrayList<Business> testList = new ArrayList<>();
                    JsonObject result = yelpHelper.getBusinessQuery();
                    // System.out.println(yelpHelper.getCoordinate());
                    if (result != null) {
                        JsonArray arr = result.getAsJsonArray("businesses");
                        //totalItemCount = result.getAsJsonObject().get("total").getAsInt();
                        for (JsonElement pa : arr) {
                            testList.add(getBusinessFromJson(pa));
                            // System.out.println("Name:" + name + ", " + "rating:" + rating);
                        }
                        final ArrayList<Business> finishList = testList;
                        // myAdapter.refresAdapter(businessInfo);
                        handler.post(new Runnable() {
                            public void run() {
                                //myAdapter.refresAdapter(finishList);
                                // myAdapter.notifyDataSetChanged();
                                myAdapter = new BusinessLineItemAdapter(finishList, getContext());
                                myList.setAdapter(myAdapter);
                                System.out.println("Tao day");
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
        Business businessObj = null;
        if (pa != null) {
            String category = "";
            String address = "";
            JsonObject business = pa.getAsJsonObject();
            String name = business.get("name").getAsString();
            JsonObject location = business.get("location").getAsJsonObject();
            if (location.get("address1") != null) {
                address = location.get("address1").getAsString()
                        + ", " + location.get("city").getAsString()
                        + ", " + location.get("state").getAsString();
                //+ ", " + location.get("zip_code").getAsString()
                //+ ", " + location.get("country").getAsString();
            }
            JsonArray categories = business.get("categories").getAsJsonArray();
            for (JsonElement ca : categories) {
                JsonObject caObj = ca.getAsJsonObject();
                category += caObj.get("title").getAsString() + ", ";
            }
            category = category.substring(0, category.length() - 1);
            Double rating = business.get("rating").getAsDouble();
            String imageURL = business.get("image_url").getAsString();
            businessObj = new Business(name, address, category, rating, imageURL);
            System.out.println("name" + name);
            businessObj.setDistanceFromCurrentLocation(String.valueOf(Math.round((business.get("distance").getAsDouble() * 0.001) * 100.0) / 100.0) + " km");
        }
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

/*    public class MyHandler extends Handler {
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
    }*/

    private void getMoreData() {
        // Set offset
        // System.out.println("Offet: " +offset);
        yelpHelper.setOffset(offset);
        // Print// create Yelp Helper instance
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // execute command
                ArrayList<Business> testList = new ArrayList<>();
                JsonObject result = yelpHelper.getBusinessQuery();
                // System.out.println(yelpHelper.getCoordinate());
                if (result != null) {
                    JsonArray arr = result.getAsJsonArray("businesses");
                    //totalItemCount = result.getAsJsonObject().get("total").getAsInt();
                    for (JsonElement pa : arr) {
                        testList.add(getBusinessFromJson(pa));
                        // System.out.println("Name:" + name + ", " + "rating:" + rating);
                    }
                    final ArrayList<Business> finishList = testList;
                    // myAdapter.refresAdapter(businessInfo);
                    handler.post(new Runnable() {
                        public void run() {
                            myAdapter.addListItemToAdapter(finishList);
                            System.out.println("ListCount: " + finishList.size());
                            myList.removeFooterView(footView);
                            isLoading = false;
                            offset += 20;
                            myAdapter.notifyDataSetChanged();
                            // myAdapter =
                            //
                            // new BusinessLineItemAdapter(finishList, getContext());
                            // myList.setAdapter(myAdapter);
                            // System.out.println("Tao day");
                        }


                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // if diff is zero, then the bottom has been reached
        if (diff == 0 && !isLoading) {
            isLoading = true;
            myList.addFooterView(footView);
            getMoreData();
        }
    }

/*    public class ThreadGetMoreData extends Thread   {
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
    }*/
}


//ActivityCompat.checkSelfPermission(getActivity(),perm));