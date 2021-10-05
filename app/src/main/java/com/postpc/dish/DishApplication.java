package com.postpc.dish;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class DishApplication extends Application implements LifecycleOwner {
    public UserInfoStorage info;
    public WifiScanner wifiScanner;
    public GPSScanner gpsScanner;
    boolean calcWasRun;
    private Activity mCurrentActivity = null;
    ListenableFuture<WorkInfo> workInfo;

    public boolean loggedIn = false;



    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(this);
//        Paper.book().delete("otherUsersEmails");
        calcWasRun = false;


        info = new UserInfoStorage(this);
        wifiScanner = new WifiScanner(this);
        gpsScanner = new GPSScanner(this);


//        info.myID = info.sp.getString("id", null);
        if(info.userEmail!=null){
            info.myID = Paper.book(info.userEmail).read("id", null);
        }


    }



    public void findUserID(){
        info.database.collection("users").whereEqualTo("email", info.getUserEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        info.myID =task.getResult().getDocuments().get(0).getId();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void runWork(){
        Log.e("Started", "work1");
//        if(!info.otherUsersEmails.contains("shmu@gmail.com")) {// todo change to bottom
//            info.otherUsersEmails.add("shmu@gmail.com");
//        }
        if(!info.otherUsersEmails.contains(info.getUserEmail())) {
            info.otherUsersEmails.add(info.getUserEmail());
        }

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

        Log.e("Unique work name", info.userEmail);
        WorkManager workManager = WorkManager.getInstance(this);
                workManager.enqueueUniquePeriodicWork(info.userEmail,
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicWorkRequestRequest);
        workInfo = workManager.getWorkInfoById(periodicWorkRequestRequest.getId());
        MainActivity temp = (MainActivity) mCurrentActivity;
//        temp.initWorkListener();
        Futures.addCallback(workInfo,
                new FutureCallback<WorkInfo>() {
                    public void onSuccess(WorkInfo result) {
                        Log.e("HERE!!", info.otherUsers.toString());

                        Paper.book(info.getUserEmail()).write("otherUsers", info.otherUsers);
                        Paper.book(info.getUserEmail()).write("otherUsersEmails", info.otherUsersEmails);
                        Log.e("Wrote", "otherUsers:" + info.otherUsers.toString());
                        Log.e("Wrote", "otherUsersEmails:" + info.otherUsersEmails.toString());
                    }

                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                }, getMainExecutor());


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
//        DishRatings[] ratingsArray = gson.fromJson(info.sp.getString("ratings", null), DishRatings[].class);
        info.ratings = Paper.book(info.getUserEmail()).read("ratings", new ArrayList<>());
//        if(ratingsArray!=null){
//            info.ratings = Arrays.asList(ratingsArray);
//        }
//        String[] iRatingsArray = gson.fromJson(info.sp.getString("iRatings", null), String[].class);
        info.indicesInRatings = Paper.book(info.getUserEmail()).read("iRatings", new ArrayList<>());
//        if(iRatingsArray!=null){
//            info.indicesInRatings = Arrays.asList(iRatingsArray);
//
//        }
        if(info.ratings.isEmpty()){
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
                            DishRatings newRating = new DishRatings(document.getId(), document.getString("Dish_Name"), document.getDouble("Rating").floatValue());
                            info.ratings.add(newRating);
                            Log.d("ID IS ", newRating.Dish_Id);
                            Log.d("NAME IS ", newRating.Dish_Name);
                            info.indicesInRatings.add(newRating.Dish_Id);

                        }
                        Gson gson = new Gson();
                        String ratingsAsJson = gson.toJson(info.ratings);
                        Paper.book(info.getUserEmail()).write("ratings", info.ratings);
                        info.sp.edit().putString("ratings", ratingsAsJson).apply();
                        String iRatingsAsJson = gson.toJson(info.indicesInRatings);
                        Paper.book(info.getUserEmail()).write("iRatings", info.indicesInRatings);
                        info.sp.edit().putString("iRatings", iRatingsAsJson).apply();
                    } else {
                        Log.e("ERRRORRR", "Error getting documents: ", task.getException());
                    }
                });

    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

}
