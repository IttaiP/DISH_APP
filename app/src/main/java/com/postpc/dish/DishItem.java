package com.postpc.dish;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class DishItem {
    private String name;
    private String description;
    private String restaurant;
    private String id;

    public DishItem(String name, String description, String restaurant){
        this.name = name;
        this.description = description;
        this.restaurant = restaurant;
    }

    public DishItem() { }

    public String getName() {
        return this.name;
    }

    public String getDescription() { return this.description; }

    public String getRestaurant() { return this.restaurant; }
}
