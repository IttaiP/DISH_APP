package com.postpc.dish;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

public class OtherUsersWorker extends Worker {
    private final DishApplication app = (DishApplication) getApplicationContext();
    private final FirebaseFirestore database = app.info.database;




    public OtherUsersWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if(app.info.otherUsers.isEmpty() || app.info.otherUsersEmails.isEmpty() || app.info.ratings.isEmpty() ) {
            return Result.failure(); // todo: check what i should do here
        }
        load_similar_users();

        Paper.book().write("otherUsers", app.info.otherUsers);
        Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);

        return Result.success();
    }




    public void load_similar_users() {
        Log.e("Started", "load_similar_users");


        for (DishRatings rating : app.info.ratings) {
            Log.e("Started", "DishRatings rating : ratings");

            database.collection("ittai-ratings")
                    .whereEqualTo("Dish_Restaurant", rating.Dish_Restaurant)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("NEW RATING", task.getResult().getDocuments().toString());
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                if (!app.info.otherUsersEmails.contains(document.get("User_email"))) {
                                    addNewSimilarUser(document);
                                }
                            }
                        } else {
                            Log.d("ERRRORRR", "Error getting documents in load_similar_users: ", task.getException());
                        }
                        // todo: fix!

                        WorkRequest secondWorkRequest =
                                new OneTimeWorkRequest.Builder(CalcSimilaritiesWorker.class)
                                        .build();
                        WorkManager.getInstance(getApplicationContext())
                                .enqueue(secondWorkRequest);

                    });
        }
    }

    public void addNewSimilarUser(DocumentSnapshot document) {
        app.info.otherUsersEmails.add(document.get("User_email").toString());
        String otherMail = document.get("User_email", String.class);
        OtherUser newOtherUser = new OtherUser(otherMail);
        Log.e("other user", document.getData().toString());
        database.collection("ittai-users-test")
                .whereEqualTo("User_email", document.get("User_email"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document1 : task.getResult().getDocuments()) {

                            document1.getReference().collection("Ratings").get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            for (DocumentSnapshot document11 : task1.getResult().getDocuments()) {
                                                DishRatings newRating = document11.toObject(DishRatings.class);
                                                newOtherUser.addRating(newRating);
                                            }

                                        }else {
                                            Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 2: ", task.getException());
                                        }
                                    });

                        }
                    } else {
                        Log.d("ERRRORRR", "Error getting documents in addNewSimilarUser layer 1: ", task.getException());
                    }
                });
        app.info.otherUsers.add(newOtherUser);
    }




}
