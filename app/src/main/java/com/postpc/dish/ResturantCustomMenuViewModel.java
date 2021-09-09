package com.postpc.dish;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private String myID = "E2JKt2QRRP8HnWkTAZMe";


    public void rated_dishes(){
        Log.d("Started", "SSSSS1");
        database.collection("ittai-users-test").document(myID).collection("Ratings").get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d("DATA", task.getResult().getDocuments().toString());

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Log.d("DOCUMENTTT", document.getId() + " => " + document.getData());
                            DishRatings newRating = document.toObject(DishRatings.class);
                            ratings.add(newRating);
                            Log.d("DISHEHSHSHSH", ratings.toString());

                        }
                    } else {
                        Log.d("ERRRORRR", "Error getting documents: ", task.getException());
                    }
                });
    }


//    public void load_similarities(){
//        database.collection("ittai_users_test").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            fo
//                        }
//                    }
//                })
//    }
}