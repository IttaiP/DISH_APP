package com.postpc.dish;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResturantCustomMenuViewModel extends AndroidViewModel {
    DishApplication app = (DishApplication)getApplication().getApplicationContext();




    private SharedViewModel sharedViewModel;

    public ResturantCustomMenuViewModel(@NonNull Application application) {
        super(application);
    }

    public void initializeSharedViewModel(SharedViewModel vmp){
        sharedViewModel = vmp;
    }




    private Float calculateSingleDishReccomendation(String dish_restaurant){
        float reccomendation = 0;
        int otherUsercount = 0;
        for(OtherUser user: app.info.otherUsers){
            otherUsercount++;
            for(DishRatings dishRating: user.getRatings()){
                if(dishRating.Dish_Restaurant.equals(dish_restaurant)){
                    reccomendation += (((dishRating.Rating-2.5)* user.getSimilarity()))*10/6.25;
                }
            }
        }
        if(otherUsercount == 0){
            return null;
        }
        return reccomendation/otherUsercount;
    }

    public void personalizeReccomendation(){
        String restaurant = "Pizza Lila" ;
//        String restaurant = sharedViewModel.getRestuarant();// todo : initialize.


        app.info.database.collection("restaurants").whereEqualTo("name", restaurant)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult().getDocuments()) {
                            document.getReference().collection("dishes").get()
                                    .addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            for(DocumentSnapshot document2 : task2.getResult().getDocuments()) {
                                                String dish_restaurant = document2.get("name").toString()+"_"+restaurant;
                                                app.info.DishReccomendationScores.put(dish_restaurant,calculateSingleDishReccomendation(dish_restaurant));
                                            }
                                        }
                                    });
                        }
                        Log.e("Reccomendation", app.info.DishReccomendationScores.toString());
                    }
                });

    }
}

