package com.postpc.dish;

import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WelcomeScreenFragment extends Fragment implements View.OnClickListener{

    private WelcomeScreenViewModel mViewModel;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private EditText email, password;
    private Button signInButton, signInWithGoogleButton;
    private TextView register;
    private DishApplication app;

    public static WelcomeScreenFragment newInstance() {
        return new WelcomeScreenFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_screen_fragment, container, false);
    }

//    /**
//     * If the user is already logged in to the app, when the app uploads it will take it directly
//     * to the restaurant search screen
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser user = auth.getCurrentUser();
//        if (user != null){
//            Intent intent = new Intent(this.getContext(), HomeScreen.class);
//            intent.putExtra("Full Name" , name.getText().toString()); // todo: check if this works
//            startActivity(intent);
//        }
//    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            // new user
            case R.id.welcome_screen_register:
                NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                        .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_welcome_screen_to_create_user);
                break;

            // exist user
            case R.id.welcome_screen_sign_in_button:
                firebaseFirestore.collection("users")
                        .whereEqualTo("email", email.getText().toString())
                        .whereEqualTo("password", password.getText().toString()).get()
                        .addOnCompleteListener(task -> {

                            // found user with provided email
                            if (task.isSuccessful()){
                                Intent intent = new Intent(this.getContext(), HomeScreen.class);
                                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                                if (documentSnapshotList.isEmpty()){
                                    Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()) {
                                        String name = documentSnapshot.getString("name");
                                        intent.putExtra("Full Name" , name);
                                    }
                                    intent.putExtra("Email" , email.getText().toString());
                                    app.info.setUserEmail(email.getText().toString());
                                    startActivity(intent);
                                }
                            }

                            //user was not found
                            else {
                                Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            case R.id.sign_in_with_google:
                firebaseFirestore.collection("users")
                        .whereEqualTo("email", email.getText().toString()).get()
                        .addOnCompleteListener(task -> {

                            // found user with provided email
                            if (task.isSuccessful()){
                                Intent intent = new Intent(this.getContext(), HomeScreen.class);
                                List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();

                                if (documentSnapshotList.isEmpty()){
                                    Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()) {
                                        String name = documentSnapshot.getString("name");
                                        intent.putExtra("Full Name" , name);
                                    }
                                    intent.putExtra("Email" , email.getText().toString());
                                    app.info.setUserEmail(email.getText().toString());
                                    startActivity(intent);
                                }
                            }

                            //user was not found
                            else {
                                Toast.makeText(this.getContext(), "User was not found.", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (DishApplication)getActivity().getApplication();
        app.loggedIn = true;

        // get text views
        email = view.findViewById(R.id.welcome_screen_EmailAddress_field);
        password = view.findViewById(R.id.welcome_screen_password_field);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // get buttons
        signInButton = view.findViewById(R.id.welcome_screen_sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setEnabled(false);

        signInWithGoogleButton = view.findViewById(R.id.sign_in_with_google);
        signInWithGoogleButton.setOnClickListener(this);
        signInWithGoogleButton.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                signInWithGoogleButton.setEnabled(true);
                signInButton.setEnabled(false);
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!email.getText().toString().isEmpty()) {
                    email.setText(email.getText().toString().trim());
                    signInButton.setEnabled(true);
                    signInWithGoogleButton.setEnabled(false);
                }

                if (password.getText().toString().isEmpty()){
                    email.setText(email.getText().toString().trim());
                    signInButton.setEnabled(false);
                    signInWithGoogleButton.setEnabled(true);
                }
            }
        });



        register = view.findViewById(R.id.welcome_screen_register);
        register.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WelcomeScreenViewModel.class);
        // TODO: Use the ViewModel
    }
}