package com.example.amynguyen.foodlover.Models;

public class Business {
    String businessId;
    String name;
    String address;
    String category;
    Double rating;
    String imgURL;
    String distanceFromCurrentLocation = null;
    Integer reviewCount;



    public Business(String  businessId, String name, String address, String category, Double rating, String imgURL)   {
        this.businessId = businessId;
        this.name = name;
        this.address = address;
        this.category = category;
        this.rating = rating;
        this.imgURL = imgURL;
        //this.reviewCount = reviewCount;
    }


    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
    public String getDistanceFromCurrentLocation() {
        return distanceFromCurrentLocation;
    }

    public void setDistanceFromCurrentLocation(String distanceFromCurrentLocation) {
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
    }
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }


}
