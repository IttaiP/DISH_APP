package com.postpc.dish;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

public class CalcSimilaritiesWorker extends Worker {
    private final DishApplication app = (DishApplication) getApplicationContext();

    public List<String> indicesInRatings;



    public CalcSimilaritiesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        calculateSimilarities();
        return Result.success();
    }


    public void calculateSimilarities(){
        Log.e(" OTHER_USERS", app.info.otherUsers.toString());
        for(OtherUser otherUser: app.info.otherUsers){
            int both_rated = 0;
            float differece_sum = 0;
            for(DishRatings rating: otherUser.getRatings()){
                int i = indicesInRatings.indexOf(rating.Dish_Restaurant);
                if(i != -1){
                    differece_sum+=(Math.abs(app.info.ratings.get(i).Rating - rating.Rating));
                    both_rated++;
                }
            }
            otherUser.setSimilarity((float) (2.5 - differece_sum/both_rated));
            otherUser.setBoth_rated(both_rated);
            Log.e(" SIMILARITY",otherUser.getUser_email() +  otherUser.getSimilarity().toString());

        }

    }
}
