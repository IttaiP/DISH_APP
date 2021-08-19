package com.postpc.dish;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FirestoreTrial extends AppCompatActivity {

    static class User {
        public String first_name = "";
        public String last_name = "";
        public String id = "";

        User() {}

        User(String first_name, String last_name, String id) {
            this.first_name = first_name;
            this.last_name = last_name;
            this.id = id;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firestore_trial);
        TextView textView = findViewById(R.id.textViewFirestoreData);

        FirestoreTrial.User user = new FirestoreTrial.User("Gil", "Rosenberg", UUID.randomUUID().toString());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(user.id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("COMMENT", "Successfully added");
            }
        });

        firestore.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    ArrayList<FirestoreTrial.User> all_users = new ArrayList<>();
                    documents.forEach( documentSnapshot -> {
                        FirestoreTrial.User user1 = documentSnapshot.toObject(FirestoreTrial.User.class);
                        if(user1 != null) {
                            all_users.add(user1);
                        }
                    });
                    Log.d("COMMENT", "Get users from firebase: " + all_users);
                });
    }
}
