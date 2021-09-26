package com.postpc.dish;

import java.io.Serializable;

public class DishRatings implements Serializable {
    public String Dish_Id = "";
    public String Dish_Name;
    public float Rating;

    DishRatings() {}
    DishRatings(String dish_Id, String dish_Name, float rating) {
        this.Dish_Id = dish_Id;
        this.Dish_Name = dish_Name;
        this.Rating = rating;
    }

    public void setId(String id) {
        this.Dish_Id = id;
    }

    public void setName(String name) {
        this.Dish_Name = name;
    }

    public void setRating(Float rating) {
        this.Rating = rating;
    }



}
