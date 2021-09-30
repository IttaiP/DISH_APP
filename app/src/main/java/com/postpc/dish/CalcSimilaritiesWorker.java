package com.postpc.dish;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import io.paperdb.Paper;

public class CalcSimilaritiesWorker extends Worker {
    private final DishApplication app = (DishApplication) getApplicationContext();

//    public List<String> indicesInRatings;



    public CalcSimilaritiesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

//        for(OtherUser user: app.info.otherUsers){
//            Log.e("otherUsers", user.getUser_email());
//            for(DishRatings rating: user.getRatings()){
//                Log.e("rating", rating.Dish_Restaurant+rating.Rating);
//            }
//        }

        Log.e("indicesInRatings", app.info.indicesInRatings.toString());
        Log.e("ratings", app.info.ratings.toString());

        calculateSimilarities();

        Paper.book(app.info.getUserEmail()).write("otherUsers", app.info.otherUsers);
        Paper.book(app.info.getUserEmail()).write("otherUsersEmails", app.info.otherUsersEmails);
        Paper.book(app.info.getUserEmail()).write("indicesInRatings", app.info.indicesInRatings);
        Paper.book(app.info.getUserEmail()).write("DishReccomendationScores", app.info.DishRecommendationScores);

        Log.e("otherUsers", app.info.otherUsers.toString());
        Log.e("otherUsersEmails", app.info.otherUsersEmails.toString());
        Log.e("indicesInRatings", app.info.indicesInRatings.toString());
        Log.e("ratings", app.info.ratings.toString());
        Log.e("DishReccomendationScrs", app.info.DishRecommendationScores.toString());

        return Result.success();
    }


    public void calculateSimilarities(){
        Log.e(" OTHER_USERS", app.info.otherUsers.toString());
        for(OtherUser otherUser: app.info.otherUsers){
            int both_rated = 0;
            float differece_sum = 0;
            for(DishRatings rating: otherUser.getRatings()){
                Log.d("dish is", "dish name" + rating.Dish_Name);
                int i = app.info.indicesInRatings.indexOf(rating.Dish_Id);
                Log.d("i is", "value " + i);
                if(i != -1){
                    Log.d("rating for dish ", rating.Dish_Id);
                    differece_sum+=(Math.abs(app.info.ratings.get(i).Rating - rating.Rating));
                    both_rated++;
                }
            }
            Log.d("DIFFERENCE IS ", String.valueOf(differece_sum));
            Log.d("BOTH IS ", String.valueOf(both_rated));

            otherUser.setSimilarity((float) (2.5 - differece_sum/both_rated));
//            otherUser.setBoth_rated(both_rated);
            Log.e(" SIMILARITY",otherUser.getUser_email() +  otherUser.getSimilarity().toString());

        }

    }
}
