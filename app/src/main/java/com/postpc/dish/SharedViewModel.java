package com.postpc.dish;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends AndroidViewModel {
    private String restuarant;

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRestuarant(String restuarant){
        this.restuarant = restuarant;
    }

    public String getRestuarant() {
        return restuarant;
    }
}
