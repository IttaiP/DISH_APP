package com.postpc.dish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class User {

    private String name, email;
    // View logout;

    public User(){}

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        logout = findViewById(R.id.logout);
//        name = findViewById(R.id.name);
//        email = findViewById(R.id.mail);
//
//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (signInAccount != null) {
//            name.setText(signInAccount.getDisplayName());
//            email.setText(signInAccount.getEmail());
//        }
//        logout.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//        });
//    }
}