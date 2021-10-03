package com.postpc.dish;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.firebase.firestore.local.QueryResult;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    DishApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (DishApplication) getApplication();

        if (app.info.myID != null) {
            Log.e("FIRST IF", "in the first if");
            if (!app.info.myID.isEmpty()) {
                Log.e("SECOND IF", "in the second if");
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = new InitUserDishDataFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
            }
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.P)
//    public void initWorkListener() {
//
//        Futures.addCallback(app.workInfo,
//                new FutureCallback<WorkInfo>() {
//                    public void onSuccess(WorkInfo result) {
//                        Log.e("HERE!!", app.info.otherUsers.toString());
//                        result.getOutputData()
//
//                        Paper.book().write("otherUsers", app.info.otherUsers);
//                        Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//                        Log.e("Wrote", "otherUsers:" + app.info.otherUsers.toString());
//                        Log.e("Wrote", "otherUsersEmails:" + app.info.otherUsersEmails.toString());
//                    }
//
//                    public void onFailure(Throwable t) {
//                        t.printStackTrace();
//                    }
//                }, getMainExecutor());
//    }

//        app.workInfo.addListener(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("HERE!!", app.info.otherUsers.toString());
//
//                Paper.book().write("otherUsers", app.info.otherUsers);
//                Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//                Log.e("Wrote", "otherUsers:"+app.info.otherUsers.toString());
//                Log.e("Wrote", "otherUsersEmails:"+app.info.otherUsersEmails.toString());
//            }
//        },getMainExecutor());



//
//    .observe(this, new Observer<WorkInfo>() {
//
//            @Override
//            public void onChanged(WorkInfo workInfo) {
//                if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
//                    Log.e("HERE!!", app.info.otherUsers.toString());
//
//                    Paper.book().write("otherUsers", app.info.otherUsers);
//                    Paper.book().write("otherUsersEmails", app.info.otherUsersEmails);
//                    Log.e("Wrote", "otherUsers:"+app.info.otherUsers.toString());
//                    Log.e("Wrote", "otherUsersEmails:"+app.info.otherUsersEmails.toString());
//                }
//            }
//        });

    protected void onResume() {
        super.onResume();
        app.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = app.getCurrentActivity();
        if (this.equals(currActivity))
            app.setCurrentActivity(null);
    }
}