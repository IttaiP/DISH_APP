package com.postpc.dish;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private String restuarant;

    public void setRestuarant(String restuarant){
        this.restuarant = restuarant;
    }

    public String getRestuarant() {
        return restuarant;
    }
}
