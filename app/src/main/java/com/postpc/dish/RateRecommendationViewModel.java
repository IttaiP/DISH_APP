package com.postpc.dish;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class RateRecommendationViewModel extends SharedViewModel {
    DishApplication app = (DishApplication)getApplication().getApplicationContext();

    public RateRecommendationViewModel(@NonNull Application application) {
        super(application);
    }

    private void addToUser(String dish, String dish_id, float rating){
        Map<String, Object> DishRating = new HashMap<>();
        DishRating.put("Dish_Name", dish);
        DishRating.put("Rating", rating);

        app.info.database.collection("users").document(app.info.myID).collection("Ratings")
                .document(dish_id)
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

    private void addToRatings(String Dish_Name, String Dish_Id, float rating, String User_email){
        Map<String, Object> DishRating = new HashMap<>();
        DishRating.put("Dish_Name", Dish_Name);
        DishRating.put("Dish_Id", Dish_Id);
        DishRating.put("Rating", rating);
        DishRating.put("email", User_email);


        app.info.database.collection("ittai-ratings").document(app.info.myID+Dish_Name+" rating")
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

    public void rateDish(float rating, String dish, String dish_id){
//        String Dish_Restaurant = dish+"_"+app.info.getRestuarant();
        addToUser(dish, dish_id, rating);
        addToRatings(dish, dish_id, rating, app.info.getUserEmail());

        DishRatings newRating = new DishRatings(dish_id, dish, rating);
        app.info.ratings.add(newRating);
        app.info.indicesInRatings.add(dish_id);
        Gson gson = new Gson();
        String ratingsAsJson = gson.toJson(app.info.ratings);
        String iRatingsAsJson = gson.toJson(app.info.indicesInRatings);
        SharedPreferences sp = ((DishApplication)getApplication()).info.sp;
        sp.edit().putString("ratings", ratingsAsJson).apply();
        sp.edit().putString("iRatings", iRatingsAsJson).apply();

    }
}