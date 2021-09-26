package com.postpc.dish;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

import io.paperdb.Paper;

public class GetCustomMenuViewModel extends AndroidViewModel {

    private SharedViewModel sharedViewModel;
    DishApplication app;
    private MutableLiveData<HashMap<String, Float>> LiveDataDishReccomendationScores;

    public MutableLiveData<HashMap<String, Float>> getLiveDataDishReccomendationScores() {
        if (LiveDataDishReccomendationScores == null) {
            LiveDataDishReccomendationScores = new MutableLiveData<HashMap<String, Float>>();
        }
        return LiveDataDishReccomendationScores;
    }

    public GetCustomMenuViewModel(@NonNull Application application) {
        super(application);
        app = (DishApplication) getApplication();
    }

//    public void ResturantCustomMenuViewModel(@NonNull Application application) {
//        super(application);
//    }

    public void initializeSharedViewModel(SharedViewModel vmp){
        sharedViewModel = vmp;
    }

    private Float calculateSingleDishRecommendation(String dish_restaurant){
        float recommendation = 0;
        int otherUsercount = 0;
        for(OtherUser user: app.info.otherUsers){
            for(DishRatings dishRating: user.getRatings()){
                if(dishRating.Dish_Id.equals(dish_restaurant)){
                    Log.e("FOUND ", "similar");
                    recommendation += (((dishRating.Rating-2.5)* user.getSimilarity()))*10/6.25;
                    otherUsercount++;
                }
            }
        }
        if(otherUsercount == 0){
            return null;
        }
        Log.e("final score is ", "Score " + recommendation/otherUsercount);
        return recommendation/otherUsercount;
    }

    public void personalizeReccomendation(String restaurant){
//        String restaurant = app.info.getRestuarant();
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
                                                String dish_restaurant = document2.getId();
                                                app.info.DishRecommendationScores.put(dish_restaurant, calculateSingleDishRecommendation(dish_restaurant));
                                                LiveDataDishReccomendationScores.setValue(app.info.DishRecommendationScores);
//                                                Log.e("Reccomendation", app.info.DishReccomendationScores.toString());
                                            }
                                        }
                                    });
                        }
                    }
                });
        Paper.book().write("DishReccomendationScores", app.info.DishRecommendationScores);


    }

    public void LiverDataaa(){

    }
}