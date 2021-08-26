package com.postpc.dish;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class DishItem {
    private String name;
    private String resturant;
    private int image;

    public DishItem(String name, String resturant, int image){
        this.name = name;
        this.resturant = resturant;
        this.image = image;
    }

    public int getImage() {
        return this.image;
    }

    public String getName() {
        return this.name;
    }

    public String getResturant() {
        return this.resturant;
    }
}
