package com.postpc.dish;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class DishItem {
    public String name;
    public String description;
    public String photo;
    public String restaurant_name;
    public String category;


    public DishItem(String name, String description, String photo, String restaurant_name, String category){
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.restaurant_name = restaurant_name;
        this.category = category;
    }

    DishItem() {}

    public String getName() {
        return this.name;
    }

    public String getDescription() { return this.description; }

    public String getPhoto() { return this.photo; }

    public String getRestaurantName() { return this.restaurant_name; }

    public String getCategory() { return this.category; }

}
