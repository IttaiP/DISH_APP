package com.postpc.dish;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class UserInfoStorage {
    private String restaurant;
    private String dishToRate;
    public String userEmail;
    public String myID = "";
    public FirebaseFirestore database = FirebaseFirestore.getInstance();
    public List<DishRatings> ratings;
    public List<String> indicesInRatings;
    public List<OtherUser> otherUsers;
    public List<String> otherUsersEmails;
    public HashMap<String, Float> DishRecommendationScores;
    SharedPreferences sp;

    public UserInfoStorage(Context context){
        FirebaseAuth auth = FirebaseAuth.getInstance();
//        user_Email = auth.getCurrentUser().getEmail();
//        myID = auth.getCurrentUser().getUid();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        Paper.book().destroy();
        ratings = Paper.book().read("ratings", new ArrayList<>());
        indicesInRatings = Paper.book().read("indicesInRatings", new ArrayList<>());
        otherUsers = Paper.book().read("otherUsers", new ArrayList<>());
        otherUsersEmails = Paper.book().read("otherUsersEmails", new ArrayList<>());
        DishRecommendationScores = Paper.book().read("DishRecommendationScores", new HashMap<>());
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        sp.edit().putString("email", userEmail).apply();
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

    public void setDishToRate(String dishToRate) {
        this.dishToRate = dishToRate;
    }

    public String getDishToRate() {
        return dishToRate;
    }
}
