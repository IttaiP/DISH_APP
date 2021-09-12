package com.postpc.dish;

import android.graphics.drawable.Drawable;
import android.media.Image;

public class DishItem {
    public String name;
    public String description;
    public String photo;


    public DishItem(String name, String description, String photo){
        this.name = name;
        this.description = description;
        this.photo = photo;
    }

    DishItem() {}

    public String getName() {
        return this.name;
    }

    public String getDescription() { return this.description; }

    public String getPhoto() { return this.photo; }

}
