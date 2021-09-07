package com.postpc.dish;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
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

import java.util.concurrent.atomic.AtomicBoolean;

public class welcome_screen extends Fragment implements View.OnClickListener{

    private WelcomeScreenViewModel mViewModel;
    private FirebaseAuth auth;
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
            NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.resturant_custom_menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.welcome_screen_register:
                NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                        .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                assert navHostFragment != null;
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_welcome_screen_to_create_user);
                break;

//            case R.id.welcome_screen_sign_in_button:
//                NavHostFragment navHostFragment1 = (NavHostFragment) requireActivity()
//                        .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//                assert navHostFragment1 != null;
//                NavController navController1 = navHostFragment1.getNavController();
//                navController1.navigate(R.id.resturant_custom_menu);
//                break;

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

        // get buttons
        signInButton = view.findViewById(R.id.welcome_screen_sign_in_button);
        register = view.findViewById(R.id.welcome_screen_register);

        register.setOnClickListener(v -> {

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