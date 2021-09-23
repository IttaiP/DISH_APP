package com.postpc.dish;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.security.AlgorithmParameterGenerator;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class DishApplication extends Application {
    public UserInfoStorage info;
    public WifiScanner wifiScanner;
    boolean calcWasRun;

    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(this);
        Paper.book().delete("otherUsersEmails");
        calcWasRun = false;


        info = new UserInfoStorage(this);
        wifiScanner = new WifiScanner(this);

        //todo: should be
        load_rated_dishes_from_sp();

        Log.e("Users", "Info " + info.getUser_Email());
        runWork();
//        todo: this is background work to update similar users. is supposed to run once a week
//         todo continuation: but need to check where the first run should be written

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
        Log.e("Started", "work1");
        if(!info.otherUsersEmails.contains("shmu@gmail.com")) {// todo change to bottom
            info.otherUsersEmails.add("shmu@gmail.com");
        }
//        if(!info.otherUsersEmails.contains(info.getUser_Email())) {
//            info.otherUsersEmails.add(info.getUser_Email());
//        }

        // todo: can add constraints here. probably not needed
        Constraints constraints = new Constraints.Builder() // todo: decide if we want constraints
//                .setRequiredNetworkType(NetworkType.UNMETERED)
//                .setRequiresCharging(true)
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

    public void runWork2(){
        if(calcWasRun){
            return;
        }
        else{
            calcWasRun = true;
        }
        Log.e("Started", "work2");
        WorkRequest secondWorkRequest =
                new OneTimeWorkRequest.Builder(CalcSimilaritiesWorker.class)
                        .build();
        WorkManager.getInstance(getApplicationContext())
                .enqueue(secondWorkRequest);
    }

    public void load_rated_dishes_from_sp(){
        Gson gson = new Gson();
        DishRatings[] ratingsArray = gson.fromJson(info.sp.getString("ratings", null), DishRatings[].class);
        if(ratingsArray!=null){
            info.ratings = Arrays.asList(ratingsArray);
        }
        String[] iRatingsArray = gson.fromJson(info.sp.getString("iRatings", null), String[].class);
        if(iRatingsArray!=null){
            info.indicesInRatings = Arrays.asList(iRatingsArray);

        }
        if(info.ratings==null || info.ratings.isEmpty()){
            load_rated_dishes();
        }
    }

    public void load_rated_dishes(){
        Log.e("Started", "load_rated_dishes");
        info.database.collection("users").document(info.myID).collection("Ratings").get()
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
                        String iRatingsAsJson = gson.toJson(info.indicesInRatings);
                        info.sp.edit().putString("iRatings", iRatingsAsJson).apply();
                    } else {
                        Log.e("ERRRORRR", "Error getting documents: ", task.getException());
                    }
                });

    }
}
