package com.example.amynguyen.foodlover.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.CustomView.NoScrollListView;
import com.example.amynguyen.foodlover.CustomView.ScrollViewExt;
import com.example.amynguyen.foodlover.Database.MyDBHandler;
import com.example.amynguyen.foodlover.Interfaces.ScrollViewListener;
import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;
import com.example.amynguyen.foodlover.yelpAPI.YelpHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.Locale;
import static android.content.Context.LOCATION_SERVICE;



public class SearchFragment extends android.support.v4.app.Fragment implements ScrollViewListener {

    Dialog myDialog;
    View mainView;
    EditText distanceInput;
    ImageView favorite;
    BusinessLineItemAdapter myAdapter;
    NoScrollListView myList;
    private LocationManager mLocationManager;
    SearchView locationSearch;
    SearchView foodSearch;
    Spinner spinnerSortBy;
    int preScrollY = 0;
    private static final int LOAD_PER_PAGE = 20;
    private static final int DEFAULT_RADIUS = 3000;
    public View footView;
    ScrollViewExt scroll;
    public boolean isLoading = false;
    public int offset = LOAD_PER_PAGE;
    Button btnView;
    Button btnFavorite;
    public MyDBHandler db;
    Business currentItem;
    YelpHelper yelpHelper = new YelpHelper();
    public static SearchFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt("argsInstance", instance);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(args);
        return searchFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mainView = view;

        footView = inflater.inflate(R.layout.footer_view, null);

        // create database
        db = MyDBHandler.getInstance(getContext());

        // Set up basic view
        initLoad(view);

        // Define location manager
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup listener for hide keyboard and loose focus
        setupUI(mainView.findViewById(R.id.scrollView));

    }
    public void initLoad(View view) {
        // Define views
        locationSearch = (SearchView) view.findViewById(R.id.searchViewLocation);
        foodSearch = (SearchView) view.findViewById(R.id.searchViewRestaurant);
        distanceInput = (EditText) view.findViewById(R.id.editTextDistance);
        spinnerSortBy = (Spinner) view.findViewById(R.id.spinnerSortBy);
        final Button btnSearch = (Button) view.findViewById(R.id.btnSearch);

        // Set scroll view listener
        scroll = (ScrollViewExt) view.findViewById(R.id.scrollView);
        scroll.setScrollViewListener(this);

        // Set adapter for result
        myList = (NoScrollListView) mainView.findViewById(R.id.listViewResult);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                // Define view and set action
                favorite = (ImageView) view.findViewById(R.id.imageViewFavorite);
                myDialog = new Dialog(getContext());
                myDialog.setContentView(R.layout.popup_view);
                myDialog.show();
                currentItem = (Business) myAdapter.getItem(position);
                btnView = myDialog.findViewById(R.id.buttonView);
                btnFavorite = myDialog.findViewById(R.id.buttonFavorite);

                // Perform database query
                if(!db.isBusinessexistFromRecent(((Business) myAdapter.getItem(position)).getBusinessId())) {
                    db.addToRecent(myAdapter, position);
                }
                else {
                    db.deleteFromRecent(myAdapter, position);
                    db.addToRecent(myAdapter, position);
                }
                if(db.loadRecent().size() > 10) {
                    db.deleteFirstRecordFromRecent();
                }

                // Redirect to Google Maps app if click on get direction
                btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", currentItem.getAddress());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        getContext().startActivity(intent);

                    }
                });

                if(db.isBusinessExistFromFavorite(((Business) myAdapter.getItem(position)).getBusinessId())) {
                    btnFavorite.setText("Remove From Favorite");
                }

                btnFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(db.isBusinessExistFromFavorite(((Business) myAdapter.getItem(position)).getBusinessId())) {
                            favorite.setImageResource(R.drawable.ic_favorite_border);
                            btnFavorite.setText("Add To Favorite");
                            db.deleteFromFavorite(myAdapter, position);
                        }
                        else {
                            if(!db.isBusinessExistFromFavorite(((Business) myAdapter.getItem(position)).getBusinessId())) {
                            favorite.setImageResource(R.drawable.ic_favorite);
                            db.addToFavorite(myAdapter, position);
                            btnFavorite.setText("Remove From Favorite");
                            }
                        }
                    }
                });
            }
        });

        // Set listener for search button, execute query to Yelp
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBusinessList();
            }
        });

        // Expand location search when foodSearch on focus
        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodSearch.onActionViewExpanded();
            }
        });
        locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationSearch.onActionViewExpanded();

            }
        });


    }



    public void getBusinessList() {
        try {
            // Request update to make sure we have latest location
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) getActivity());
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) getActivity());
            // Get last known location
            Criteria c = new Criteria();
            //if we pass false than
            //it will check first satellite location than Internet and than Sim Network
            String provider = mLocationManager.getBestProvider(c, false);
            Location location = mLocationManager.getLastKnownLocation(provider);
            // Set location
            if (location != null) {
                String coordinate = location.getLatitude() + ", " + location.getLongitude();
                yelpHelper.setCoordinate(coordinate);
            }
            if (locationSearch.getQuery().toString().equals("")) yelpHelper.setLocation("");
            else yelpHelper.setLocation(locationSearch.getQuery().toString());


            // Reset offset
            yelpHelper.setOffset(0);

            // Get optional parameters
            // Set radius distance
            if(distanceInput.getText().toString().equals("")) {
                yelpHelper.setRadius(DEFAULT_RADIUS);
            }else {
                int radius = (int) (Integer.parseInt(distanceInput.getText().toString()) * 1609.34);
                if(radius > 40000) yelpHelper.setRadius(40000);
                else yelpHelper.setRadius(radius);
            }

            // Set sortBy query
            yelpHelper.setSortBy(getSortByValue(spinnerSortBy));

            // Set restaurant query
            yelpHelper.setTerm(foodSearch.getQuery().toString());
            // Print// create Yelp Helper instance
            final Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // execute command
                    ArrayList<Business> testList = new ArrayList<>();
                    JsonObject result = yelpHelper.getBusinessQuery();
                    if (result != null) {
                        JsonArray arr = result.getAsJsonArray("businesses");
                        for (JsonElement pa : arr) {
                            testList.add(getBusinessFromJson(pa));
                        }
                        final ArrayList<Business> finishList = testList;

                        // Working on UI thread after fetch the result
                        handler.post(new Runnable() {
                            public void run() {
                                myAdapter = new BusinessLineItemAdapter(finishList, getContext(), db);
                                myList.setAdapter(myAdapter);
                                isLoading = false;
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

    public String getSortByValue(Spinner spinner) {
        switch (spinner.getSelectedItemPosition()) {
            case 1:
                return "distance";
            case 2:
                return "review_count";
            case 3:
                return "rating";
            default:
                return "best_match";
        }
    }

    public Business getBusinessFromJson(JsonElement pa) {
        Business businessObj = null;
        if (pa != null) {
            String category = "";
            String address = "";
            JsonObject business = pa.getAsJsonObject();
            String name = business.get("name").getAsString();
            String id = business.get("id").getAsString();
            Integer reviewCount = business.get("review_count").getAsInt();
            JsonObject location = business.get("location").getAsJsonObject();

            // Append fields to address string
            if (location.get("address1") != null) {
                address = location.get("address1").getAsString()
                        + ", " + location.get("city").getAsString()
                        + ", " + location.get("state").getAsString();
            }
            JsonArray categories = business.get("categories").getAsJsonArray();

            // Append fields to category string
            for (JsonElement ca : categories) {
                JsonObject caObj = ca.getAsJsonObject();
                category += caObj.get("title").getAsString() + ", ";
            }
            // Remove the last comma
            category = category.substring(0, category.length() - 2);
            Double rating = business.get("rating").getAsDouble();
            String imageURL = business.get("image_url").getAsString();
            // Init new business instance
            businessObj = new Business(id, name, address, category, rating, reviewCount, imageURL);
            // Set distance by km
            businessObj.setDistanceFromCurrentLocation(String.valueOf(Math.round((business.get("distance").getAsDouble() * 0.001) * 100.0) / 100.0) + " km");
        }
        return businessObj;

    }

    private void getMoreData() {
        // Set offset
        yelpHelper.setOffset(offset);

        // Print// create Yelp Helper instance
        final Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Established connection to Yelp
                // Execute query
                ArrayList<Business> testList = new ArrayList<>();
                JsonObject result = yelpHelper.getBusinessQuery();
                // Only perform when there is some results
                if (result != null) {
                    // Get an array of businesses
                    JsonArray arr = result.getAsJsonArray("businesses");

                    // Iterate though result, add business to temporary list
                    for (JsonElement pa : arr) {
                        testList.add(getBusinessFromJson(pa));
                    }
                    final ArrayList<Business> finishList = testList;
                    handler.post(new Runnable() {
                        public void run() {
                            // Change new adapter
                            myAdapter.addListItemToAdapter(finishList);
                            // System.out.println("ListCount: " + finishList.size());
                            // Remove loading icon
                            myList.removeFooterView(footView);
                            isLoading = false;
                            // Increase offset
                            offset += LOAD_PER_PAGE;
                            // Update listView
                            myAdapter.notifyDataSetChanged();
                            // Set to current scroll position
                            scroll.setScrollY(preScrollY);
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
        preScrollY = scrollView.getScrollY();

        // if diff is zero, then the bottom has been reached
        if (diff == 0 && !isLoading) {
            isLoading = true;
            myList.addFooterView(footView);
            getMoreData();

        }
    }

public static void hideSoftKeyboard(Activity activity) {
    InputMethodManager inputMethodManager =
            (InputMethodManager) activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);
    if(activity.getCurrentFocus() != null) {
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
        activity.getCurrentFocus().clearFocus();
    }
}
    public void setupUI(View view) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    // Clear all focus
                    v.clearFocus();

                    // Clear focus for distance input
                    if(!(v instanceof EditText)) {
                        distanceInput.setCursorVisible(false);
                        distanceInput.setFocusable(false);
                        distanceInput.setFocusableInTouchMode(false);
                    }else {
                        distanceInput.setCursorVisible(true);
                        distanceInput.setFocusable(true);
                        distanceInput.setFocusableInTouchMode(true);
                    }
                    // Hide the keyboard
                    hideSoftKeyboard(getActivity());
                    return false;
                }

            });

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

}