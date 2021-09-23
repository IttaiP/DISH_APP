package com.postpc.dish;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.postpc.dish.DishApplication;
import com.postpc.dish.DishRatings;
import com.postpc.dish.SharedViewModel;

import java.util.HashMap;
import java.util.Map;

public class RateRecommendationViewModel extends SharedViewModel {
    DishApplication app = (DishApplication)getApplication().getApplicationContext();

    public RateRecommendationViewModel(@NonNull Application application) {
        super(application);
    }

    private void addToUser(String Dish_Restaurant, float rating){
        Map<String, Object> DishRating = new HashMap<>();
        DishRating.put("Dish_Restaurant", Dish_Restaurant);
        DishRating.put("Rating", rating);

        app.info.database.collection("users").document(app.info.myID).collection("Ratings")
                .document(app.info.myID+Dish_Restaurant+" user")
                .set(DishRating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("WRITE SUCCESS", "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("WRITE FAILURE", "Error writing document", e);

                    }
                });

    }

    private void addToRatings(String Dish_Restaurant, float rating, String User_email){
        Map<String, Object> DishRating = new HashMap<>();
        DishRating.put("Dish_Restaurant", Dish_Restaurant);
        DishRating.put("Rating", rating);
        DishRating.put("email", User_email);


        app.info.database.collection("ittai-ratings").document(app.info.myID+Dish_Restaurant+" rating")
                .set(DishRating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("WRITE SUCCES", "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("WRITE FAILURE", "Error writing document", e);

                    }
                });
    }

    public void rateDish(float rating, String dish){
        String Dish_Restaurant = dish+"_"+app.info.getRestuarant();
        addToUser(Dish_Restaurant, rating);
        addToRatings(Dish_Restaurant, rating, app.info.getUser_Email());

        DishRatings newRating = new DishRatings();
        newRating.Dish_Restaurant = Dish_Restaurant;
        newRating.Rating = rating;
        app.info.ratings.add(newRating);
        Gson gson = new Gson();
        String ratingsAsJson = gson.toJson(app.info.ratings);
        SharedPreferences sp = ((DishApplication)getApplication()).info.sp;
        sp.edit().putString("ratings", ratingsAsJson).apply();
    }
}