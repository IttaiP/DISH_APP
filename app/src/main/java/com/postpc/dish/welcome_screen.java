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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class welcome_screen extends Fragment implements View.OnClickListener{

    private WelcomeScreenViewModel mViewModel;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private EditText name, email, password;
    private Button signInButton;
    private TextView register;

    public static welcome_screen newInstance() {
        return new welcome_screen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_screen_fragment, container, false);
    }

    /**
     * If the user is already logged in to the app, when the app uploads it will take it directly
     * to the restaurant search screen
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            Intent intent = new Intent(this.getContext(), HomeScreen.class);
            intent.putExtra("Full Name" , name.getText().toString()); // todo: check if this works
            startActivity(intent);
        }
    }

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
                                intent.putExtra("Full Name" , name.getText().toString()); // todo: check if this works
                                startActivity(intent);
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

        // get text views
        name = view.findViewById(R.id.welcome_screen_name_field);
        email = view.findViewById(R.id.welcome_screen_EmailAddress_field);
        password = view.findViewById(R.id.welcome_screen_password_field);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // get buttons
        signInButton = view.findViewById(R.id.welcome_screen_sign_in_button);
        signInButton.setOnClickListener(this);

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