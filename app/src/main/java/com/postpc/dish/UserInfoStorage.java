package com.postpc.dish;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfoStorage {
    private String restuarant;
    private String user_Email;
    public String myID = "E2JKt2QRRP8HnWkTAZMe";
    public FirebaseFirestore database = FirebaseFirestore.getInstance();
    public List<DishRatings> ratings = new ArrayList<>();
    public List<String> indicesInRatings = new ArrayList<>();
    public List<OtherUser> otherUsers = new ArrayList<>();
    public List<String> otherUsersEmails = new ArrayList<>();
    public HashMap<String, Float> DishReccomendationScores = new HashMap<>();


    SharedPreferences sp;


    public UserInfoStorage(Context context){
        sp = PreferenceManager.getDefaultSharedPreferences(context);

    }



    public void setUser_Email(String user_Email) {
        this.user_Email = user_Email;
    }

    public String getUser_Email() {
        return user_Email;
    }

    public void setRestuarant(String restuarant) {
        this.restuarant = restuarant;
    }

    public String getRestuarant() {
        return restuarant;
    }
}
