package com.postpc.dish;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DishApplication extends Application {
    public UserInfoStorage info;

    @Override
    public void onCreate() {
        super.onCreate();

        info = new UserInfoStorage(this);

//        runWork();
    }

    public void findUserID(){
        info.database.collection("users").whereEqualTo("email", info.getUser_Email()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        info.myID = task.getResult().getDocuments().get(0).getId();
                    }
                });
    }

    public void runWork(){
        if(!info.otherUsersEmails.contains("shmu@gmail.com")) {// todo change to bottom
            info.otherUsersEmails.add("shmu@gmail.com");
        }
//        if(!info.otherUsersEmails.contains(info.getUser_Email())) {
//            info.otherUsersEmails.add(info.getUser_Email());
//        }

        Constraints constraints = new Constraints.Builder() // todo: decide if we want constraints
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest periodicWorkRequestRequest =
                new PeriodicWorkRequest.Builder(OtherUsersWorker.class, 7, TimeUnit.DAYS) // todo: decide interval
                        .setConstraints(constraints)
                        .addTag("OtherUsersWorker")
                        .build();


        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("load similar users",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicWorkRequestRequest);


    }

    public void load_rated_dishes_from_sp(){
        Gson gson = new Gson();
        DishRatings[] ratingsArray = gson.fromJson(info.sp.getString("ratings", null), DishRatings[].class);
        info.ratings = Arrays.asList(ratingsArray);
    }

    public void load_rated_dishes() {
        Log.e("Started", "load_rated_dishes");
        info.database.collection("ittai-users-test").document(info.myID).collection("Ratings").get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Log.e("DOCUMENTTT", document.getId() + " => " + document.getData());
                            DishRatings newRating = document.toObject(DishRatings.class);
                            info.ratings.add(newRating);
                            info.indicesInRatings.add(newRating.Dish_Restaurant);

                        }
                        Gson gson = new Gson();
                        String ratingsAsJson = gson.toJson(info.ratings);
                        info.sp.edit().putString("ratings", ratingsAsJson).apply();
                    } else {
                        Log.e("ERRRORRR", "Error getting documents: ", task.getException());
                    }
                });

    }
}
