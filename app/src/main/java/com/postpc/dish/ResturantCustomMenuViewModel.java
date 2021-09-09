package com.postpc.dish;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ResturantCustomMenuViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private List<DishRatings> ratings = new ArrayList<>();
    private List<String> indicesInRatings = new ArrayList<>();
    private List<OtherUser> otherUsers = new ArrayList<>();
    private List<String> otherUsersEmails = new ArrayList<>();
    private List<DocumentSnapshot> userRatings;

    private String myID = "E2JKt2QRRP8HnWkTAZMe";


    public void load_rated_dishes() {
        Log.e("Started", "load_rated_dishes");
        database.collection("ittai-users-test").document(myID).collection("Ratings").get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.e("DATA", task.getResult().getDocuments().toString());
                        userRatings = (task.getResult().getDocuments());


                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Log.e("DOCUMENTTT", document.getId() + " => " + document.getData());
                            DishRatings newRating = document.toObject(DishRatings.class);
                            ratings.add(newRating);
                            indicesInRatings.add(newRating.Dish_Restaurant);

                        }
                    } else {
                        Log.e("ERRRORRR", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void addNewSimilarUser(DocumentSnapshot document) {
        otherUsersEmails.add(document.get("User_email").toString());
        String otherMail = document.get("User_email", String.class);
        OtherUser newOtherUser = new OtherUser(otherMail);
        Log.e("ZZZZ", document.getData().toString());
        database.collection("ittai-users-test")
                .whereEqualTo("User_email", document.get("User_email"))
                .get()
                .addOnCompleteListener(task -> {
                    Log.e("ZZZZ", task.getResult().toString());
                    Log.e("ZZZZ", task.getResult().getDocuments().toString());
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
        otherUsers.add(newOtherUser);
    }


    public void load_similar_users() {
        Log.e("Started", "load_similar_users");

        for (DishRatings rating : ratings) {
            Log.e("Started", "DishRatings rating : ratings");

            database.collection("ittai-ratings")
                    .whereEqualTo("Dish_Restaurant", rating.Dish_Restaurant)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("NEW RATING", task.getResult().getDocuments().toString());
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                if (!otherUsersEmails.contains(document.get("User_email"))) {
                                    addNewSimilarUser(document);
                                }
                            }
                        } else {
                            Log.d("ERRRORRR", "Error getting documents in load_similar_users: ", task.getException());
                        }
                    });
        }
    }

    public void calculateSimilarities(){
        Log.e(" OTHER_USERS", otherUsers.toString());
        for(OtherUser otherUser: otherUsers){
            int both_rated = 0;
            float differece_sum = 0;
            for(DishRatings rating: otherUser.getRatings()){
                int i = indicesInRatings.indexOf(rating.Dish_Restaurant);
                if(i != -1){
                    differece_sum+=(Math.abs(ratings.get(i).Rating - rating.Rating));
                    both_rated++;
                }
            }
            otherUser.setSimilarity((float) (2.5 - differece_sum/both_rated));
            otherUser.setBoth_rated(both_rated);
            Log.e(" SIMILARITY", otherUser.getSimilarity().toString());

        }

    }
}

