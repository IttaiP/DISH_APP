package com.postpc.dish;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class UserInfoStorage {
    private String restaurant;
    public ArrayList<String> dishesToRate;
    public FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public String userEmail;
    public String myID = null;
    public FirebaseFirestore database = FirebaseFirestore.getInstance();
    public List<DishRatings> ratings;
    public List<String> indicesInRatings;
    public List<OtherUser> otherUsers;
    public List<Uri> toUpload;
    public List<String> otherUsersEmails;
    public HashMap<String, Float> DishRecommendationScores;
    SharedPreferences sp;
    DishApplication app;

    public UserInfoStorage(Context context){
        FirebaseAuth auth = FirebaseAuth.getInstance();
//        user_Email = auth.getCurrentUser().getEmail();
//        myID = auth.getCurrentUser().getUid();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
//        Paper.book().destroy();

        app = (DishApplication) context.getApplicationContext();
    }

    public void initDB(){
        ratings = Paper.book(getUserEmail()).read("ratings", new ArrayList<>());
        indicesInRatings = Paper.book(getUserEmail()).read("indicesInRatings", new ArrayList<>());
        otherUsers = Paper.book(getUserEmail()).read("otherUsers", new ArrayList<>());
        otherUsersEmails = Paper.book(getUserEmail()).read("otherUsersEmails", new ArrayList<>());
        DishRecommendationScores = Paper.book(getUserEmail()).read("DishRecommendationScores", new HashMap<>());
        dishesToRate = Paper.book(getUserEmail()).read("dishesToRate", new ArrayList<>());
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        initDB();
        sp.edit().putString("email", userEmail).apply();
        if(myID!=null) {
            app.load_rated_dishes_from_sp();
        }
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setMyID(String myID) {
        this.myID = myID;
        sp.edit().putString("id", myID).apply();
    }

    public void addDishToRate(String dishToRate) {
        this.dishesToRate.add(dishToRate);
        Paper.book(getUserEmail()).write("dishesToRate", dishesToRate);
        Gson gson = new Gson();
        String dishesAsJson = gson.toJson(app.info.ratings);
        sp.edit().putString("dishesToRate", dishesAsJson).apply();
    }

    public ArrayList<String> getDishesToRate() {
        return dishesToRate;
    }

    public void removeDishFromToRate(String dishToRemove) {
        dishesToRate.remove(dishToRemove);
    }
}
