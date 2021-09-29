package com.postpc.dish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiScanner {
    WifiManager wifiManager;
    BroadcastReceiver wifiScanReceiver;
    DishApplication app;
    List<String> scannedRestaurants;
    List<String> wifiRestaurants;
    int notFoundCount;
    public MutableLiveData<List<String>> foundRestaurantsLiveData;


    WifiScanner(DishApplication app) {
        this.app = app;
        notFoundCount = 0;
        initWifi();
    }

    public MutableLiveData<List<String>> getRestaurants() {
        if (foundRestaurantsLiveData == null) {
            foundRestaurantsLiveData = new MutableLiveData<List<String>>();
        }
        return foundRestaurantsLiveData;
    }


    public void initWifi() {
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
        AtomicBoolean found = new AtomicBoolean(false);
        List<ScanResult> results = wifiManager.getScanResults();
        scannedRestaurants = new ArrayList<>();
        wifiRestaurants = new ArrayList<>();
        int resultsLength = results.size();
        for (ScanResult result : results) {
            app.info.database.collection("restaurants").whereEqualTo("Wifi", result.SSID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                found.set(true);
                                if (!scannedRestaurants.contains(task.getResult().getDocuments().get(0).get("name").toString())) {
                                    scannedRestaurants.add(task.getResult().getDocuments().get(0).get("name").toString());
                                    wifiRestaurants.add(task.getResult().getDocuments().get(0).get("Wifi").toString());
                                    this.getRestaurants().setValue(wifiRestaurants);
                                    Log.e("SCANNED REST", scannedRestaurants.toString());
                                }

                            }
                            else{
                                if(found.compareAndSet(true, true)){return;}
                                Log.e("CHECK", "not found count:"+notFoundCount+". results length:"+resultsLength);
                                wifiRestaurants.add("NONE FOUND!");
                                wifiRestaurants.clear();
                                this.getRestaurants().setValue(wifiRestaurants);
                            }
                        }
                    });
        }
    }


    public void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        scanSuccess();
        Log.d("Scan Failed", "showing old results");

    }


}
