package com.postpc.dish;

import java.io.Serializable;

public class DishRatings implements Serializable {
    public String Dish_Restaurant;
    public float Rating;

    DishRatings() {}
    DishRatings(String dish_Restaurant, float rating) {
        this.Dish_Restaurant = dish_Restaurant;
        this.Rating = rating;
    }

}
