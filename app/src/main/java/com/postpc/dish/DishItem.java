package com.postpc.dish;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class DishItem {
    public String name;
    public String description;


    public DishItem(String name, String description){
        this.name = name;
        this.description = description;
    }

    DishItem() {}


    public String getName() {
        return this.name;
    }

    public String getDescription() { return this.description; }

}
