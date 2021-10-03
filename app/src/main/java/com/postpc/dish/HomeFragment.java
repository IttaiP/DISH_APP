package com.postpc.dish;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment implements dishRateAdapter.ContentListener{

    private HomeViewModel mViewModel;
    private wifiRestaurantsAdapter restaurantsAdapter;
    private wifiRestaurantsAdapter GPSrestaurantsAdapter;

    private RecyclerView restaurants_recycler_view;
    private RecyclerView GPS_restaurants_recycler_view;
    private RecyclerView dishes_recycler_view;
    private dishRateAdapter dishRateAdapter;

    AppCompatActivity activity;
    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    DishApplication app;
    List<String> scannedRestaurants;
    private ArrayList<Restaurant> restaurants;
    private ArrayList<Restaurant> wifiRestaurantsList;
    private ArrayList<Restaurant> GPSRestaurantsList;
    private ArrayList<DishItem> dishesToRate;
    private HashMap<String, Uri> urisToUpload;
    private boolean buttonPressed;
    private boolean readyToObserve;
    Button enable_wifi;
    Button enable_GPS;
    Button plusButton;
    Button minusButton;
    TextView kmTextView;
    boolean searchByKmUpdate;

    private Observer<List<String>> restsObserver;
    private Observer<List<String>> restsGPSObserver;
    private Observer<Integer> kmObserver;


    private Observer<List<Uri>> uriObserver;

//    public static HomeFragment newInstance() {
//        return new HomeFragment();
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        readyToObserve = false;
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        app = (DishApplication)activity.getApplication().getApplicationContext();
        mViewModel = new ViewModelProvider(this).get(com.postpc.dish.HomeViewModel.class);
        mViewModel.activity = activity;
        readyToObserve = true;
        searchByKmUpdate = false;
        wifiRestaurantsList = new ArrayList<>();
        GPSRestaurantsList = new ArrayList<>();
        dishesToRate = new ArrayList<>();
        restaurants_recycler_view = view.findViewById(R.id.restaurants_recycler_view);
        GPS_restaurants_recycler_view = view.findViewById(R.id.restaurants_gps_recycler_view);

        restaurantsAdapter = new wifiRestaurantsAdapter(wifiRestaurantsList);
        GPSrestaurantsAdapter = new wifiRestaurantsAdapter(GPSRestaurantsList);

        buttonPressed = false;
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        TextView not_found = view.findViewById(R.id.not_found);
        not_found.setVisibility(view.GONE);
        TextView not_found_gps = view.findViewById(R.id.not_found_gps);
        not_found_gps.setVisibility(view.GONE);

        enable_wifi = view.findViewById(R.id.enable_wifi);
        enable_GPS = view.findViewById(R.id.enable_gps);
        minusButton = view.findViewById(R.id.minus_button);
        plusButton = view.findViewById(R.id.plus_button);
        kmTextView = view.findViewById(R.id.km);
        initKMListeners();

        restaurants_recycler_view.setHasFixedSize(true);
        restaurants_recycler_view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        restaurants_recycler_view.setAdapter(restaurantsAdapter);
        GPS_restaurants_recycler_view.setHasFixedSize(true);
        GPS_restaurants_recycler_view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        GPS_restaurants_recycler_view.setAdapter(GPSrestaurantsAdapter);

        dishes_recycler_view = view.findViewById(R.id.dishes_recycler_view);
        dishRateAdapter = new dishRateAdapter(this::onItemClicked);


        TextView no_dishes_to_rate = view.findViewById(R.id.no_dishes_to_rate);
        no_dishes_to_rate.setVisibility(view.GONE);
        dishes_recycler_view.setHasFixedSize(true);
        dishes_recycler_view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        dishes_recycler_view.setAdapter(dishRateAdapter);

        app.info.database.collection("restaurants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    restaurants = (ArrayList<Restaurant>) Objects.requireNonNull(task.getResult()).toObjects(Restaurant.class);
                    restaurantsAdapter.setAdapter(restaurants);
                    GPSrestaurantsAdapter.setAdapter(restaurants);

                }
                else {
                    Log.e("Error", "Firebase " + task.getException().getMessage());
                }
            }
        });

        // Create the observer which updates the UI.
        restsObserver = wifiRestaurants -> {
            if(!readyToObserve){return;}
            if(!buttonPressed) {
                return;
            }

            // Update the UI, in this case, a TextView.
//                scannedRestaurants = app.wifiScanner.scannedRestaurants;
            if(restaurants==null) return;

            wifiRestaurantsList.clear();
            boolean found = false;

            for(String restaurantWifi: wifiRestaurants){
                for(Restaurant rest: restaurants){
                    Log.e("RestuarantName", rest.Wifi);
                    if(rest.Wifi.equals(restaurantWifi)){
                        found = true;
                        enable_wifi.setVisibility(view.GONE);
                        wifiRestaurantsList.add(rest);
                    }
                }
            }
            if(found){
                not_found.setVisibility(view.GONE);
                restaurantsAdapter.setAdapter(wifiRestaurantsList);
                restaurantsAdapter.notifyDataSetChanged();
            }
            else {
                if(wifiRestaurantsList.isEmpty()){
                    not_found.setVisibility(View.VISIBLE);
                    enable_wifi.setVisibility(View.VISIBLE);
                    if(!enable_wifi.getText().equals("search again")){
                        enable_wifi.animate().translationY(25f);
                    }
                    enable_wifi.setText("search again");
                }

            }
        };

        app.wifiScanner.getRestaurants().observe(getViewLifecycleOwner(), restsObserver);

        restsGPSObserver = GPSRestaurants -> {
            Log.e("restsGPSObserver",GPSRestaurants.toString());
            GPSRestaurantsList.clear();
            if(restaurants==null) return;
            if(!GPSRestaurants.isEmpty()){
                not_found_gps.setVisibility(View.VISIBLE);
                not_found_gps.setVisibility(View.GONE);

                for(Restaurant rest: restaurants){
                    if(GPSRestaurants.contains(rest.name)){
                        GPSRestaurantsList.add(rest);
                    }
                }
                GPSrestaurantsAdapter.setAdapter(GPSRestaurantsList);
                GPSrestaurantsAdapter.notifyDataSetChanged();

            }
            else{
                enable_GPS.setVisibility(View.VISIBLE);
                enable_GPS.animate().translationY(25f);
                not_found_gps.setVisibility(View.VISIBLE);
            }
        };

        app.gpsScanner.getRestaurants().observe(getViewLifecycleOwner(), restsGPSObserver);


        enable_wifi.setOnClickListener(view1 -> {
            buttonPressed = !buttonPressed;
            if(!buttonPressed) {
                restaurantsAdapter.setAdapter(restaurants);
            }
            else {
                beginWifiScan(enable_wifi);
            }

        });

        ArrayList<String> getDishesToRate = app.info.getDishToRate();
        if(getDishesToRate == null || getDishesToRate.isEmpty()) {
            no_dishes_to_rate.setVisibility(view.VISIBLE);
        }
        else {
        for (String dish_id : app.info.getDishToRate()) {
            app.info.database.collection("all-dishes").document(dish_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    dishRateAdapter.addDishes(Objects.requireNonNull(documentSnapshot.toObject(DishItem.class)));
                    dishRateAdapter.notifyDataSetChanged();
                }
            });
        }}

        enable_GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnableLocation();
                enable_GPS.setVisibility(getView().GONE);
                mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);




            }
        });
    }


    private void beginWifiScan(Button enable_wifi){
        EnableLocation();
        enable_wifi.setVisibility(getView().GONE);
        mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }


    public void EnableLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity.getApplicationContext())
                .checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(activity,REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });



    }

    private void WiFiScanFailure(){

    }


    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    Log.e("SUCCESS", "onActivityResult: PERMISSION GRANTED");
                    searchByKmUpdate = true;
                    app.gpsScanner.search(getActivity());
                    if(app.wifiScanner.wifiManager.isWifiEnabled()){
                        boolean success = app.wifiScanner.wifiManager.startScan();
                        if (!success) {
                            app.wifiScanner.scanFailure();

                        }
                        Log.e("FAIL REASON", String.valueOf(success));
                    }
                    else {
                        app.wifiScanner.wifiManager.setWifiEnabled(true);
                        boolean success = app.wifiScanner.wifiManager.startScan();
                        if (!success) {
                            app.wifiScanner.scanFailure();
                        }
                        app.wifiScanner.wifiManager.setWifiEnabled(false);
                    }

                } else {
                    Log.e("FAILURE", "onActivityResult: PERMISSION DENIED");
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Snackbar snackbar = Snackbar
                                .make(activity.getWindow().getDecorView(), "You Must Allow Location For Restaurant Recognition!", Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Got It", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("REGRET", "RRRRRR");
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                        enable_wifi.setVisibility(View.VISIBLE);
                        enable_GPS.setVisibility(View.VISIBLE);

                    } else {
                        Snackbar snackbar = Snackbar
                                .make(activity.getWindow().getDecorView(), "You Must Allow Location Permission Through App Settings!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Take Me There", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.e("REGRET", "RRRRRR");
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }

                                });
                        snackbar.show();
                        enable_wifi.setVisibility(View.VISIBLE);
                        enable_GPS.setVisibility(View.VISIBLE);

                    }
                }
            });



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }


    @Override
    public void onItemClicked(@NonNull DishItem item) {
        Bundle arguments = new Bundle();
        app.info.database.collection("all-dishes").whereEqualTo("name", item.name).whereEqualTo("restaurant_name", item.restaurant_name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()) {
                    Log.d("dish ID to rate", documentSnapshot.getId());
                    arguments.putString("dish ID to rate", documentSnapshot.getId());
                    Fragment rateRecommendation = new RateRecommendationFragment();
                    rateRecommendation.setArguments(arguments);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_container, rateRecommendation).addToBackStack(null).commit();
                }
            }
        });
    }

    public void initKMListeners(){
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentKM = app.gpsScanner.getCurrentKM().getValue();
                if(currentKM > 1){
                    if(searchByKmUpdate){
                        app.gpsScanner.search(getActivity());
                    }
                    app.gpsScanner.setCurrentKM(currentKM - 1);
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentKM = app.gpsScanner.getCurrentKM().getValue();
                if(currentKM < 10){
                    if(searchByKmUpdate){
                        app.gpsScanner.search(getActivity());
                    }
                    app.gpsScanner.setCurrentKM(currentKM + 1);
                }
            }
        });

        kmObserver = km -> {
            kmTextView.setText(km.toString());
        };

        app.gpsScanner.getCurrentKM().observe(getViewLifecycleOwner(), kmObserver);


    }
}