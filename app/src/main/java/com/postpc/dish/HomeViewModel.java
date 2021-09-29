package com.postpc.dish;
import android.app.Application;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends AndroidViewModel {
    DishApplication app = (DishApplication)getApplication().getApplicationContext();
    Activity activity;
    String[] missingPermissions;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

}