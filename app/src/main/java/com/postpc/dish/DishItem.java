package com.postpc.dish;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class DishItem {
    private String name;
    private String description;


    public DishItem(String name, String description, String restaurant){
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return this.name;
    }

    public String getDescription() { return this.description; }

}
