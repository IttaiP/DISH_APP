package com.postpc.dish;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements dishRateAdapter.ContentListener{

    private HomeViewModel mViewModel;
    private wifiRestaurantsAdapter restaurantsAdapter;
    private RecyclerView restaurants_recycler_view;
    private RecyclerView dishes_recycler_view;
    private dishRateAdapter dishRateAdapter;

    Activity activity;
    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    DishApplication app;
    List<String> scannedRestaurants;
    private ArrayList<Restaurant> restaurants;
    private ArrayList<Restaurant> wifiRestaurantsList;
    private ArrayList<DishItem> dishesToRate;
    private boolean buttonPressed;

//    public static HomeFragment newInstance() {
//        return new HomeFragment();
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        app = (DishApplication)activity.getApplication().getApplicationContext();
        mViewModel = new ViewModelProvider(this).get(com.postpc.dish.HomeViewModel.class);
        mViewModel.activity = activity;

        wifiRestaurantsList = new ArrayList<>();

        restaurants_recycler_view = view.findViewById(R.id.restaurants_recycler_view);
        restaurantsAdapter = new wifiRestaurantsAdapter(wifiRestaurantsList);

        buttonPressed = false;
        TextView not_found = view.findViewById(R.id.not_found);
        not_found.setVisibility(view.GONE);
        Button enable_wifi = view.findViewById(R.id.enable_wifi);
        restaurants_recycler_view.setHasFixedSize(true);
        restaurants_recycler_view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        restaurants_recycler_view.setAdapter(restaurantsAdapter);

        dishes_recycler_view = view.findViewById(R.id.dishes_recycler_view);
        dishRateAdapter = new dishRateAdapter();
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
                }
                else {
                    Log.e("Error", "Firebase " + task.getException().getMessage());
                }
            }
        });

        // Create the observer which updates the UI.
        final Observer<List<String>> restsObserver = wifiRestaurants -> {
            // Update the UI, in this case, a TextView.
//                scannedRestaurants = app.wifiScanner.scannedRestaurants;
            Log.e("wifiRestaurants",wifiRestaurants.toString());
//            Log.e("HERE", restaurants.toString());
            wifiRestaurantsList.clear();

            for(String restaurantWifi: wifiRestaurants){
                for(Restaurant rest: restaurants){
                    Log.e("RestuarantName", rest.Wifi);
                    if(rest.Wifi.equals(restaurantWifi)){
                        // todo: change button
                        enable_wifi.setVisibility(view.GONE);
                        wifiRestaurantsList.add(rest);
                    }
                }
            }
            restaurantsAdapter.setAdapter(wifiRestaurantsList);
            restaurantsAdapter.notifyDataSetChanged();
            Log.e("itemCount","" + restaurantsAdapter.getItemCount());
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        app.wifiScanner.getRestaurants().observe(getViewLifecycleOwner(), restsObserver);

        enable_wifi.setOnClickListener(view1 -> {
            buttonPressed = !buttonPressed;
            if(!buttonPressed) {
                restaurantsAdapter.setAdapter(restaurants);
            }
            else {
                Log.e("restaurants", restaurants.toString());
                beginWifiScan(enable_wifi);
            }

        });

        ArrayList<String> getDishesToRate = app.info.getDishToRate();
        if(getDishesToRate == null) {
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

    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    Log.e("SUCCESS", "onActivityResult: PERMISSION GRANTED");
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
                                .make(activity.getWindow().getDecorView(), "You Must Allow Location For Restaurant Recognition!", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Got It", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("REGRET", "RRRRRR");
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
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
                    }
                }
            });



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onItemClicked(@NonNull DishItem item) {

    }
}