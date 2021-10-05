package com.postpc.dish;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class UserInfoStorage {
    private boolean flag = false;
    private String restaurant;
    private ArrayList<String> dishToRate;
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
        this.dishToRate.add(dishToRate);
    }

    public ArrayList<String> getDishToRate() {
        if(!flag) {
            dishToRate = new ArrayList<>();
            dishToRate.add("0HEbQdcMUoqovPtCGnRr");
            dishToRate.add("DJBWkaGK26LrVC7ScC2i");
            dishToRate.add("LXtcb2qasdVveT2h0SYx");
            flag = true;
        }
        return dishToRate;
    }

    public void removeDishFromToRate(String dishToRemove) {
        dishToRate.remove(dishToRemove);
    }
}
