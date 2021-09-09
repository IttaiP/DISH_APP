package com.postpc.dish;

import java.util.ArrayList;
import java.util.List;

public class OtherUser {
    private Float similarity;
    private String User_email;
    private List<DishRatings> ratings;
    private int both_rated;

    public OtherUser(String User_email){
        this.User_email = User_email;
        both_rated = 0;
        ratings = new ArrayList<>();
    }

    public void addRating(DishRatings rating){
        ratings.add(rating);
    }

    public List<DishRatings> getRatings() {
        return ratings;
    }

    public void setBoth_rated(int both_rated) {
        this.both_rated = both_rated;
    }

    public void setSimilarity(Float similarity) {
        this.similarity = similarity;
    }
}
