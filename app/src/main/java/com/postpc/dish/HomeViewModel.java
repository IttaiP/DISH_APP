package com.postpc.dish;
import android.app.Application;
import android.app.Activity;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    DishApplication app = (DishApplication)getApplication().getApplicationContext();
    Activity activity;
    String[] missingPermissions;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

}