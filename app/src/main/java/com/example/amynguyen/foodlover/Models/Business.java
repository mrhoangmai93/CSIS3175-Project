package com.example.amynguyen.foodlover.Models;

public class Business {
    String name;
    String address;
    String category;
    Integer rating;
    String imgURL;

    public Business(String name, String address, String category, Integer rating, String imgURL)   {
        this.name = name;
        this.address = address;
        this.category = category;
        this.rating = rating;
        this.imgURL = imgURL;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
