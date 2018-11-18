package com.example.amynguyen.foodlover.yelpAPI;

import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.rapidapi.rapidconnect.Argument;
import com.rapidapi.rapidconnect.RapidApiConnect;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class YelpHelper {
    public final static String APP_NAME = "default-application_5be34bdde4b08725af2ad174";
    public final static String APP_ID = "9ba414c5-557c-45e5-a1b6-0218c9aa2edf";
    public final static String YIELP_API_KEY = "MsBMFjvBxbbjORnbOCk0hBaFONlDNDt0C1uUGik3WC3N_r0IfLGoDqhdmNrUSUgrfYXVd-nNTodyE4E3rTdOgt5G58rFlCYEMpMPGyHT7pvCA2-nW_UkzazS_8bjW3Yx";

    RapidApiConnect connect;
    Map<String, Argument> body;
    Gson gson;

    String term = "";
    Boolean openNow = true;
    int radius = 0;
    String location = "";
    String sortBy = "best_match";
    String coordinate = "";
    int offset = 0;
    int limit = 20;

    public YelpHelper() {
        this.connect = new RapidApiConnect(APP_NAME, APP_ID);
        this.body = new HashMap<String, Argument>();
        body.put("accessToken", new Argument("data", YIELP_API_KEY));

        gson = new Gson();
    }
    public JsonObject getBusinessQuery() {
        if(location == null && coordinate == null) return null;
        if(!location.equals("")) body.put("location", new Argument("data", location));
        else body.put("coordinate", new Argument("data", coordinate));
        // body.put("coordinate", new Argument("data", coordinate));
        // System.out.println(location);
        body.put("term", new Argument("data", term));
        body.put("offset", new Argument("data", String.valueOf(offset)));
        body.put("openNow", new Argument("data", String.valueOf(openNow)));
        body.put("sortBy", new Argument("data", sortBy));
        // System.out.println("Opennow:" + String.valueOf(openNow));
        body.put("limit", new Argument("data", String.valueOf(limit)));
        if(radius > 0 ) body.put("radius", new Argument("data", String.valueOf(radius)));
        try {
            Map<String, Object> response = connect.call("YelpAPI", "getBusinesses", body);
            if(response.get("success") != null) {
                LinkedTreeMap<?,?> result = (LinkedTreeMap) response.get("success");
                // JsonObject jsonObject = gson.toJsonTree(yourMap).getAsJsonObject();
                JsonObject data = gson.toJsonTree(result).getAsJsonObject();
                // JsonArray array = data.getAsJsonObject().getAsJsonArray("businesses");
                System.out.println("success: " + data.getAsJsonObject().get("total"));
                return data;
                // return null;
            } else{
                System.out.println("error: " + response);
                return null;
            }
        } catch(Exception e){
            System.out.println("Error: " + e);
            return null;
        }
    }
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
