package com.postpc.dish;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;

public class SharedViewModel extends AndroidViewModel {
    private String restuarant;
    private MutableLiveData<HashMap<String, List<String>>> uriLiveData;


    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRestuarant(String restuarant){
        this.restuarant = restuarant;
    }

    public String getRestuarant() {
        return restuarant;
    }

    public MutableLiveData<HashMap<String, List<String>>> getUriLiveData() {
        if (uriLiveData == null) {
            uriLiveData = new MutableLiveData<HashMap<String,List<String>>>();
        }
        return uriLiveData;
    }
}
