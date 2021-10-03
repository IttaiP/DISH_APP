package com.postpc.dish;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GPSScanner {
    LocationManager locationManager;
    BroadcastReceiver GPSScanReceiver;

    LocationListener locationListener;
    DishApplication app;
    List<String> scannedRestaurants;
    List<String> GPSRestaurants;
    int notFoundCount;
    public MutableLiveData<List<String>> foundRestaurantsLiveData;
    public MutableLiveData<Integer> currentKM;

    List<String> restaurants = new ArrayList<>();
    List<Double> latitudes = new ArrayList<>();
    List<Double> longitudes = new ArrayList<>();
    public List<String> restaurantsInRange = new ArrayList<>();

    final Looper looper = null;
    Criteria criteria;



    GPSScanner(DishApplication app) {
        this.app = app;
        notFoundCount = 0;
        this.getCurrentKM().setValue(5); // todo: link with buttons
        initGPS();
    }

    public MutableLiveData<List<String>> getRestaurants() {
        if (foundRestaurantsLiveData == null) {
            foundRestaurantsLiveData = new MutableLiveData<List<String>>();
        }
        return foundRestaurantsLiveData;
    }

    public void setCurrentKM(int currentKM) {
        this.currentKM.setValue(currentKM);
    }

    public MutableLiveData<Integer> getCurrentKM() {
        if (currentKM == null) {
            currentKM = new MutableLiveData<Integer>();
        }
        return currentKM;    }





    public void getRestLocations() {
        app.info.database.collection("restaurants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    restaurants.add(doc.get("name").toString());
                    latitudes.add((Double) doc.get("latitude"));
                    longitudes.add((Double) doc.get("longitude"));


                }
            } else {
                Log.e("Error", "Firebase " + task.getException().getMessage());
            }
        });
    }

    public void initGPS() {
        getRestLocations();
        locationManager = (LocationManager)
                app.getSystemService(Context.LOCATION_SERVICE);

        locationListener = location -> {
            Log.d("GGG", String.valueOf(location));

            Geocoder geocoder = new Geocoder(app, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (listAddresses != null && listAddresses.size() > 0) {
                    Double latitude = listAddresses.get(0).getLatitude();
                    Double longitude = listAddresses.get(0).getLongitude();
                    restaurantsInRange.clear();

                    // todo: these 2 lines only for debugging usecase when no restaraunts found
//                    this.getRestaurants().setValue(restaurantsInRange);
//                    restaurants.clear();


                    for (int i = 0; i < restaurants.size(); ++i) {
                        if (latitude > latitudes.get(i) - (0.01 * this.getCurrentKM().getValue()) && latitude < latitudes.get(i) + (0.01 * this.getCurrentKM().getValue())) {
                            if (longitude > longitudes.get(i) - (0.01 * this.getCurrentKM().getValue()) && latitude < longitudes.get(i) + (0.01 * this.getCurrentKM().getValue())) {
                                restaurantsInRange.add(restaurants.get(i));
                                this.getRestaurants().setValue(restaurantsInRange);

                            }
                        }
                    }
                    Log.d("Restaurants In Range", restaurantsInRange.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        };

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);



    }

    public void search(FragmentActivity activity) {

        if (ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            locationManager.requestSingleUpdate(criteria, locationListener, looper);
        }
    }
}
