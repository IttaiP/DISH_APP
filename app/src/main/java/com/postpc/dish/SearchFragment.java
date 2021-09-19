package com.postpc.dish;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private EditText search;
    private RecyclerView recycler_view;
    private FirebaseFirestore database;

    private ArrayList<Restaurant> restaurants;
    private restaurnats_adapter adapter;

    private SearchViewModel mViewModel;
    private SharedViewModel sharedViewModel;

    // ---------------ADDED BY ITTAI --------------
    Activity activity = getActivity();
    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    DishApplication app = (DishApplication)activity.getApplication().getApplicationContext();
    List<String> scannedRestaurants;




    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search = view.findViewById(R.id.search_bar);
        recycler_view = view.findViewById(R.id.recycler_view);
        database = FirebaseFirestore.getInstance();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        restaurants = new ArrayList<>();
        adapter = new restaurnats_adapter(restaurants);

        //Set up recycler view
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(adapter);

        database.collection("restaurants").orderBy("name")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    restaurants = (ArrayList<Restaurant>) Objects.requireNonNull(task.getResult()).toObjects(Restaurant.class);
                    adapter.setAdapter(restaurants);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Not Found", "Error: " + task.getException().getMessage());
                }
            }
        });

        // Todo: add onclick listener for each restaurant. within, write the following:
        // sharedViewModel.setRestuarant(todo restaurant name here);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search_text = search.getText().toString();
                Log.d("Search is", search_text);
                search_in_firestore(search_text);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        view.findViewById(R.id.button_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.action_search_resturants2_to_resturant_menu);

            }
        });

        view.findViewById(R.id.button_custom_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.action_search_resturants2_to_resturant_custom_menu);

            }
        });


        // ---------------ADDED BY ITTAI --------------
        mViewModel = new ViewModelProvider(this).get(com.postpc.dish.SearchViewModel.class);
        mViewModel.activity = activity;

        view.findViewById(R.id.WifiScanbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginWifiScan();
                scannedRestaurants = app.wifiScanner.scannedRestaurants;
            }
        });
    }

    private void search_in_firestore(String search) {
        database.collection("restaurants").orderBy("name")
                .startAt(search).endAt("search\uf8ff")
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                restaurants = (ArrayList<Restaurant>) Objects.requireNonNull(task.getResult()).toObjects(Restaurant.class);
                adapter.setAdapter(restaurants);
                adapter.notifyDataSetChanged();
            } else {
                Log.d("Not Found", "Error: " + task.getException().getMessage());
            }
        });
    }

    public Restaurant find_restaurant_by_name(String name) {
        for(Restaurant restaurant : restaurants) {
            if(restaurant.name.equals(name)) {
                return restaurant;
            }
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }



    // ---------------ADDED BY ITTAI --------------


    private void beginWifiScan(){
        EnableLocation();
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
                    Snackbar snackbar = Snackbar
                            .make(activity.getWindow().getDecorView(), "GOOD CHOICE!", Snackbar.LENGTH_LONG);
                    snackbar.show();
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
                                .make(activity.getWindow().getDecorView(), "You Must Give The App Location Permission Through Phone Settings!", Snackbar.LENGTH_INDEFINITE)
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

}