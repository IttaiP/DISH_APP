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
    List<String> scannedRestaurants;


    WifiScanner(DishApplication app) {
        this.app = app;
        initWifi();
    }


    public void initWifi(){
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

    public void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        scannedRestaurants = new ArrayList<>();

        for (ScanResult result : results) {
            app.info.database.collection("restaurants").whereEqualTo("Wifi",result.SSID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if(task.getResult().getDocuments().size()>0){
                                if(!scannedRestaurants.contains(task.getResult().getDocuments().get(0).get("name").toString())){
                                    scannedRestaurants.add(task.getResult().getDocuments().get(0).get("name").toString());
                                    Log.e("SCANNED REST",scannedRestaurants.toString());
                                }

                            }
                        }
                    });
        }
    }





    public void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        scannedRestaurants.clear(); // todo : decide whether we want to show old ones or not
        Log.d("FAILURE", "!!!!!!");

    }


}
