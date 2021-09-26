package com.postpc.dish;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class UserInfoStorage {
    private String restuarant;
    public String user_Email;
    public String myID = "";
    public FirebaseFirestore database = FirebaseFirestore.getInstance();
    public List<DishRatings> ratings;
    public List<String> indicesInRatings;
    public List<OtherUser> otherUsers;
    public List<String> otherUsersEmails;
    public HashMap<String, Float> DishReccomendationScores;

    SharedPreferences sp;

    public UserInfoStorage(Context context){
        FirebaseAuth auth = FirebaseAuth.getInstance();
//        user_Email = auth.getCurrentUser().getEmail();
//        myID = auth.getCurrentUser().getUid();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit(); // TODO: delete later
        editor.clear().commit(); // TODO: delete later
        Paper.book().destroy();
        ratings = Paper.book().read("ratings", new ArrayList<>());
        indicesInRatings = Paper.book().read("indicesInRatings", new ArrayList<>());
        otherUsers = Paper.book().read("otherUsers", new ArrayList<>());
        otherUsersEmails = Paper.book().read("otherUsersEmails", new ArrayList<>());
        DishReccomendationScores = Paper.book().read("DishReccomendationScores", new HashMap<>());
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
