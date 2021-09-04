package com.postpc.dish;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

@SuppressWarnings("ALL")
public class CreateUser extends Fragment implements View.OnClickListener {

    private CreateUserViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private EditText editTextName, editTextEmail;
    private Button registerButton;
    private AppCompatButton signUpWithGoogleButton;
    private ProgressBar progressBar;
    @Nullable
    private Bundle savedInstanceState;

    public static CreateUser newInstance() {
        return new CreateUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Intent intent = new Intent(getActivity(), Profile.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        signUpWithGoogleButton = view.findViewById(R.id.google_signIn);
        signUpWithGoogleButton.setOnClickListener(this);

        registerButton = view.findViewById(R.id.register);
        registerButton.setOnClickListener(this);

        editTextName = view.findViewById(R.id.register_person_name);
        editTextEmail = view.findViewById(R.id.register_email_address);

        progressBar = view.findViewById(R.id.progress_bar);

        createRequest();
        signUpWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

//        view.findViewById(R.id.search_resturant_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.action_create_user_to_search_resturants2);
//
//            }
        }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("Google singin: ", "SIGNIN failed", e);
                Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(getActivity(), Profile.class);
                        startActivity(intent);

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getActivity(), "auth failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateUserViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (name.isEmpty()){
            editTextName.setError("Full name is required!");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provied valid email!");
            editTextEmail.requestFocus();
            return;
        }
    }
}