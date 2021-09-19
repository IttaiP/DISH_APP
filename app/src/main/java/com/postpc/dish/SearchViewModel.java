package com.postpc.dish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    DishApplication app = (DishApplication)getApplication().getApplicationContext();
    Activity activity;
    String[] missingPermissions;




    public SearchViewModel(@NonNull Application application) {
        super(application);
    }
    // TODO: Implement the ViewModel


    // todo: code below needs to be added in relevant activity

}