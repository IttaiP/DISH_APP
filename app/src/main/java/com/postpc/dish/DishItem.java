package com.postpc.dish;

import android.graphics.drawable.Drawable;
import android.media.Image;

import java.io.Serializable;
import java.util.Comparator;

public class DishItem implements Serializable {
    public String name;
    public String description;
    public String photo;
    public String restaurant_name;
    public String category;
    public float match;


    public DishItem(String name, String description, String photo, String restaurant_name, String category){
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.restaurant_name = restaurant_name;
        this.category = category;
        this.match = 0;
    }

    DishItem() {}

    public String getName() {
        return this.name;
    }

    public String getDescription() { return this.description; }

    public String getPhoto() { return this.photo; }

    public String getRestaurantName() { return this.restaurant_name; }

    public String getCategory() { return this.category; }

    public float getMatch() { return this.match; }

}

class SortByMatch implements Comparator<DishItem>
{
    @Override
    public int compare(DishItem dishItem_1, DishItem dishItem_2) {
        return (int) -(dishItem_1.match - dishItem_2.match);
    }
}
