package com.postpc.dish;

import androidx.lifecycle.ViewModelProvider;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;

public class welcome_screen extends Fragment {

    private WelcomeScreenViewModel mViewModel;
    private FirebaseAuth auth;
    private EditText name;
    private EditText email;

    public static welcome_screen newInstance() {
        return new welcome_screen();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_screen_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get text views
        name = view.findViewById(R.id.PersonName);
        email = view.findViewById(R.id.EmailAddress);
        auth = FirebaseAuth.getInstance();

        // get buttons
        Button signInButton = view.findViewById(R.id.sign_in);
        Button signUpButton = view.findViewById(R.id.sign_up);


        // must insert name and email to connect to the app
        signInButton.setEnabled(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (!email.getText().toString().matches("")){
                    signInButton.setEnabled(true);
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (!name.getText().toString().matches("")){
                    signInButton.setEnabled(true);
                }
            }
        });

        signUpButton.setOnClickListener(v -> {

            NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.action_welcome_screen_to_create_user);

        });

        signInButton.setOnClickListener(v -> {
            if (emailExist(email)){
                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_welcome_screen_to_search_resturants2);
            }
            else {
                name.setText("");
                email.setText("");
                Toast.makeText(getContext(), "Email not found, Please sign up first.", Toast.LENGTH_SHORT).show();
            }
        });

//        view.findViewById(R.id.resturant_custom_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.action_welcome_screen_to_resturant_custom_menu);
//
//            }
//        });

//        view.findViewById(R.id.rate_recommendation).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.action_welcome_screen_to_rate_recommendation);
//
//            }
//        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WelcomeScreenViewModel.class);
        // TODO: Use the ViewModel
    }

    public boolean emailExist(View v){
        AtomicBoolean emailExist = new AtomicBoolean(false);
        auth.fetchSignInMethodsForEmail(email.getText().toString())
                .addOnCompleteListener(task -> {
                    boolean check = !task.getResult().getSignInMethods().isEmpty();
                    if (check){
                        emailExist.set(true);
                        Toast.makeText(getContext(), "Email already exist", Toast.LENGTH_SHORT).show();                    }
                });
        return emailExist.get();
    }
}