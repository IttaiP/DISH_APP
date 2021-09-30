package com.postpc.dish;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

public class OtherUsersWorker extends ListenableWorker {
    private final DishApplication app = (DishApplication) getApplicationContext();
    private final FirebaseFirestore database = app.info.database;
    SettableFuture<Result> mFuture;
    int updatedUserCount = 0;



    public OtherUsersWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        Log.e("Started", "OtherUsersWorker doWork");
        mFuture = SettableFuture.create();


//        if(app.info.otherUsers.isEmpty() || app.info.otherUsersEmails.isEmpty() || app.info.ratings.isEmpty() ) { // not sure why had first 2 conds
        load_similar_users();


//        Paper.book().write("otherUsers", app.info.otherUsers);
//        Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//        Log.e("Wrote", "otherUsers:"+ app.info.otherUsers.toString());
//        Log.e("Wrote", "otherUsersEmails:"+ app.info.otherUsersEmails.toString());


        return mFuture;
    }

//    @NonNull
//    @Override
//    public Result doWork() {
//        Log.e("Started", "OtherUsersWorker doWork");
//
////        if(app.info.otherUsers.isEmpty() || app.info.otherUsersEmails.isEmpty() || app.info.ratings.isEmpty() ) { // not sure why had first 2 conds
//        if(app.info.ratings.isEmpty() ) {
//            return Result.failure(); // todo: check what i should do here
//        }
//        load_similar_users();
//
//
//        Paper.book().write("otherUsers", app.info.otherUsers);
//        Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//        Log.e("Wrote", "otherUsers:"+ app.info.otherUsers.toString());
//        Log.e("Wrote", "otherUsersEmails:"+ app.info.otherUsersEmails.toString());
//
//        return Result.success();
//    }




    public void load_similar_users() {
        Log.e("Started", "OtherUsersWorker load_similar_users");


        for (DishRatings rating : app.info.ratings) {

            database.collection("ittai-ratings")
                    .whereEqualTo("Dish_Id", rating.Dish_Id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                            Log.d("NEW RATING", task.getResult().getDocuments().toString());
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                if ((!app.info.otherUsersEmails.contains(document.getString("email"))&&document.getString("email")!=null)) {
                                    updatedUserCount++;
                                    Log.e("updatedUserCount", ""+updatedUserCount);
                                    Log.e("Adding a new user ", document.getString("email"));
                                    addNewSimilarUser(document);
                                }
                                else{
                                    if(document.getString("email")!=null){
                                        if(!document.getString("email").equals(app.info.userEmail)){// todo: make sure email is save in info by this point
                                            updateSimilarUser(document);
                                        }
                                    }
                                }
                                // todo : add update to user if already exists
                            }
                            if(updatedUserCount==0){
                                Log.e("SuccesFromHere", "109");
                                mFuture.set(Result.success());
                            }
                        } else {
                            Log.d("ERRRORRR", "Error getting documents in load_similar_users: ", task.getException());
                            mFuture.set(Result.failure());
                        }
                        // todo: fix!



                    });
        }

    }

    public void addNewSimilarUser(DocumentSnapshot document) {
        app.info.otherUsersEmails.add(document.getString("email"));
        String otherMail = document.getString("email");
        OtherUser newOtherUser = new OtherUser(otherMail);
        app.info.otherUsers.add(newOtherUser);
        Log.e("other user", document.getData().toString());
        database.collection("users")
                .whereEqualTo("email", document.getString("email"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document1 : task.getResult().getDocuments()) {

                            document1.getReference().collection("Ratings").get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            for (DocumentSnapshot document11 : task1.getResult().getDocuments()) {
                                                DishRatings newRating = new DishRatings(document11.getId(), document11.getString("Dish_Name"), (document11.getDouble("Rating")).floatValue());
                                                newOtherUser.addRating(newRating);
                                                Log.e("Rating", newRating.Dish_Name+newRating.Rating);
                                            }
                                            Paper.book(app.info.getUserEmail()).write("otherUsers", app.info.otherUsers);
                                            Log.e("otherUsersWrote", ""+app.info.otherUsers);

                                            Paper.book(app.info.getUserEmail()).write("otherUsersEmails", app.info.otherUsersEmails);
//                                            updatedUserCount--;
//                                            Log.e("updatedUserCount", ""+updatedUserCount);
//                                            if(updatedUserCount==0){
//                                                Log.e("SuccesFromHere", "155");
//                                                Paper.book().write("otherUsers", app.info.otherUsers);
////                                                Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//                                                mFuture.set(Result.success());
//                                            }

                                        }else {
                                            Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 2: ", task.getException());
                                        }
                                    });

                        }

                    } else {
                        Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 1: ", task.getException());
                    }
                });

    }

    private OtherUser findOtherUser(String email){
        for(OtherUser otherUser: app.info.otherUsers){
            if(email.equals(otherUser.getUser_email())){
                return otherUser;
            }
        }
        return null;
    }

    public void updateSimilarUser(DocumentSnapshot document){
        String otherMail = document.getString("email");
        OtherUser newOtherUser = findOtherUser(otherMail);
        Log.e("updated other user", document.getData().toString());
        database.collection("users")
                .whereEqualTo("email", document.getString("email"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document1 : task.getResult().getDocuments()) {

                            document1.getReference().collection("Ratings").get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            for (DocumentSnapshot document11 : task1.getResult().getDocuments()) {
                                                DishRatings newRating = new DishRatings(document11.getId(), document11.getString("Dish_Name"), (document11.getDouble("Rating")).floatValue());
                                                newOtherUser.addRating(newRating);
//                                                app.info.otherUsers.add(newOtherUser);
                                                Log.e("Rating", newRating.Dish_Name+newRating.Rating);
                                            }
                                            Paper.book(app.info.getUserEmail()).write("otherUsers", app.info.otherUsers);
                                            Log.e("otherUsersWrote", ""+app.info.otherUsers);

//                                            Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//                                            updatedUserCount--;
//                                            Log.e("updatedUserCount", ""+updatedUserCount);
//                                            if(updatedUserCount==0){
//                                                Log.e("SuccesFromHere", "155");
//                                                Paper.book().write("otherUsers", app.info.otherUsers);
////                                                Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//                                                mFuture.set(Result.success());
//                                            }

                                        }else {
                                            Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 2: ", task.getException());
                                        }
                                    });

                        }

                    } else {
                        Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 1: ", task.getException());
                    }
                });
    }
}
