package com.postpc.dish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class WifiScanner {
    WifiManager wifiManager;
    BroadcastReceiver wifiScanReceiver;
    DishApplication app;
    String[] missingPermissions;
    List<String> scannedRestaurants;


    WifiScanner(DishApplication app) {
        this.app = app;
        initWifi();
    }


    private void initWifi(){
        wifiManager = (WifiManager)
                app.getSystemService(Context.WIFI_SERVICE);

        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.app.registerReceiver(wifiScanReceiver, intentFilter);

    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        List<String> scannedRestaurants = new ArrayList<>();

        for (ScanResult result : results) {
            app.info.database.collection("restaurants").whereEqualTo("Wifi",result.SSID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            scannedRestaurants.add(task.getResult().getDocuments().get(0).get("name").toString());
                        }
                    });
        }
    }





    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        scannedRestaurants.clear(); // todo : decide whether we want to show old ones or not
        Log.d("FAILURE", "!!!!!!");

    }

    // todo: code below needs to be added in relevant activity
//    private void beginWifiScan(Activity activity){
//        permissionValidation(activity);
//        boolean success = wifiManager.startScan();
//        // get results of scan from app.WifiScanner
//        if (!success) {
//            // scan failure handling
//            scanFailure();
//        }
//    }
//
//
//    private void permissionValidation() {
//        boolean hasFineLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//        boolean hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//
//        List<String> missingPermission = new ArrayList<>();
//
//        if (!hasFineLocationPermission) {
//            missingPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//        if (!hasCoarseLocationPermission) {
//            missingPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        }
//
//        missingPermissions = missingPermission.toArray(new String[missingPermission.size()]);
//
//        Log.e("MISSING", String.valueOf(missingPermission.isEmpty()));
//
//        if (!missingPermission.isEmpty()) {
//
//            ActivityCompat.requestPermissions(
//                    activity,
//                    missingPermissions, 1);
//        }
//
//    }

//    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
//        for (int grantResult : grantResults) {
//            if (grantResult == PackageManager.PERMISSION_DENIED) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @SuppressLint("MissingSuperCall")
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.e("LENGTH", String.valueOf(grantResults.length));
//        if (hasAllPermissionsGranted(grantResults)) {
//            Log.e("SUCCES", "SSSSSSSSSSSSSSSSSSSS");
//
//            Snackbar snackbar = Snackbar
//                    .make(activity.getWindow().getDecorView(), "GOOD CHOICE!", Snackbar.LENGTH_LONG);
//            snackbar.show();
//        } else {
//            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
//                Log.e("FAILURE", "FFFFFFFFFFFFFFFFFFFFFFFF");
//                Snackbar snackbar = Snackbar
//                        .make(activity.getWindow().getDecorView(), "No Auto Restaurant Recognition For You!", Snackbar.LENGTH_INDEFINITE)
//                        .setAction("regret", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Log.e("REGRET", "RRRRRR");
//                                ActivityCompat.requestPermissions(
//                                        activity,
//                                        missingPermissions, 1);
//                            }
//                        });
//                snackbar.show();
//            }
//            else{
//                Snackbar snackbar = Snackbar
//                        .make(activity.getWindow().getDecorView(), "You Must Give The App Location Permission Through Phone Settings!", Snackbar.LENGTH_INDEFINITE)
//                        .setAction("Take Me There", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Log.e("REGRET", "RRRRRR");
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                intent.setData(uri);
//                                startActivity(intent);
//                            }
//
//                        });
//                snackbar.show();
//            }
//        }
//    }

}
