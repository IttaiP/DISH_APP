package com.postpc.dish;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SharedViewModel extends AndroidViewModel {
    public SharedViewModel(@NonNull Application application) {
        super(application);
    }
//    private final DishApplication app = (DishApplication) getApplicationContext();
//
//
//
//    SharedPreferences sp = getApplication().getSharedPreferences("sp", Context.MODE_PRIVATE);
//
//
//    public SharedViewModel(@NonNull Application application) {
//        super(application);
//    }
//
//
//
//
//
//
//
//
//
//    public void addNewSimilarUser(DocumentSnapshot document) {
//        otherUsersEmails.add(document.get("User_email").toString());
//        String otherMail = document.get("User_email", String.class);
//        OtherUser newOtherUser = new OtherUser(otherMail);
//        Log.e("other user", document.getData().toString());
//        database.collection("ittai-users-test")
//                .whereEqualTo("User_email", document.get("User_email"))
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot document1 : task.getResult().getDocuments()) {
//
//                            document1.getReference().collection("Ratings").get()
//                                    .addOnCompleteListener(task1 -> {
//                                        if(task1.isSuccessful()){
//                                            for (DocumentSnapshot document11 : task1.getResult().getDocuments()) {
//                                                DishRatings newRating = document11.toObject(DishRatings.class);
//                                                newOtherUser.addRating(newRating);
//                                            }
//
//                                        }else {
//                                            Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 2: ", task.getException());
//                                        }
//                                    });
//
//                        }
//                    } else {
//                        Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 1: ", task.getException());
//                    }
//                });
//        otherUsers.add(newOtherUser);
//    }
//
//
//    public void load_similar_users() {
//        Log.e("Started", "load_similar_users");
//        otherUsersEmails.add("shmu@gmail.com");
////        otherUsersEmails.add(sharedViewModel.getUser_Email());
//
//        for (DishRatings rating : ratings) {
//            Log.e("Started", "DishRatings rating : ratings");
//
//            database.collection("ittai-ratings")
//                    .whereEqualTo("Dish_Restaurant", rating.Dish_Restaurant)
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Log.d("NEW RATING", task.getResult().getDocuments().toString());
//                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
//                                if (!otherUsersEmails.contains(document.get("User_email"))) {
//                                    addNewSimilarUser(document);
//                                }
//                            }
//                        } else {
//                            Log.d("ERRRORRR", "Error getting documents in load_similar_users: ", task.getException());
//                        }
//                    });
//        }
//    }
//
//    public void calculateSimilarities(){
//        Log.e(" OTHER_USERS", otherUsers.toString());
//        for(OtherUser otherUser: otherUsers){
//            int both_rated = 0;
//            float differece_sum = 0;
//            for(DishRatings rating: otherUser.getRatings()){
//                int i = indicesInRatings.indexOf(rating.Dish_Restaurant);
//                if(i != -1){
//                    differece_sum+=(Math.abs(ratings.get(i).Rating - rating.Rating));
//                    both_rated++;
//                }
//            }
//            otherUser.setSimilarity((float) (2.5 - differece_sum/both_rated));
//            otherUser.setBoth_rated(both_rated);
//            Log.e(" SIMILARITY",otherUser.getUser_email() +  otherUser.getSimilarity().toString());
//
//        }
//
//    }


}
