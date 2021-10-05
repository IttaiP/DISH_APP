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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressWarnings("ALL")
public class CreateUser extends Fragment implements View.OnClickListener {

    private DishApplication app;
    private CreateUserViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private EditText editTextName, editTextEmail, editTextPassword;
    private Button registerButton;
    private AppCompatButton signUpWithGoogleButton;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    @Nullable
    private Bundle savedInstanceState;

    public static CreateUser newInstance() {
        return new CreateUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (DishApplication)getActivity().getApplication();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        signUpWithGoogleButton = view.findViewById(R.id.google_signIn);
        signUpWithGoogleButton.setOnClickListener(this);

        registerButton = view.findViewById(R.id.create_user_register_button);
        registerButton.setOnClickListener(this);

        editTextName = view.findViewById(R.id.create_user_person_name_field);
        editTextEmail = view.findViewById(R.id.create_user_email_address_field);
        editTextPassword = view.findViewById(R.id.create_user_password_field);

        progressBar = view.findViewById(R.id.progress_bar_register_screen);

        createRequest();
        signUpWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
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
            case R.id.create_user_register_button:
                registerUser();
//                Intent intent = new Intent(getContext(), IntroSlider.class); //todo: make sure
//                startActivity(intent); //todo: make sure
                break;
        }
    }

    // ==================================== REGISTER WITH GOOGLE ===================================
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
                        writeNewUserToFirestoreDatabase(true);
                        Intent intent = new Intent(getContext(), IntroSlider.class); //todo: make sure
                        startActivity(intent); //todo: make sure

                    } else {
                        // If sign in fails, display a message to the user.
                        Exception exception = task.getException();
                        Toast.makeText(requireContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
    // =============================================================================================


    // =============================== REGISTER WITH EMAIL & PASSWORD ==============================
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        if (password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6){
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()){
                        writeNewUserToFirestoreDatabase(false);
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(requireContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }});
    }
    private void writeNewUserToFirestoreDatabase(boolean withGoogle) {
        User user = null;
        if (withGoogle){
            FirebaseUser googleUser = FirebaseAuth.getInstance().getCurrentUser();
            if (googleUser != null) {
                String userName = googleUser.getDisplayName();
                String userEmail = googleUser.getEmail();

                user = new User(userName, userEmail, "");
            }
        }

        else if (!withGoogle){
            user = new User(editTextName.getText().toString().trim(),
                    editTextEmail.getText().toString().trim(),
                    editTextPassword.getText().toString().trim());
        }
        app.info.userEmail = user.email;
        app.info.setMyID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        app.info.setUserEmail(user.email);

        FirebaseFirestore.getInstance().collection("users").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(user)
                .addOnCompleteListener(requireActivity(), subTask -> {

                    if (subTask.isSuccessful()){
                        Toast.makeText(requireContext(),
                                "User has been registered successfully!",
                                Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        app.info.userEmail = mAuth.getCurrentUser().getEmail();
                        app.info.setMyID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        app.info.setUserEmail(mAuth.getCurrentUser().getEmail());
                        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                                .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                        assert navHostFragment != null;
                        NavController navController = navHostFragment.getNavController();
                        navController.navigate(R.id.init_user_dish_data);
                    } else {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.delete()
                                .addOnCompleteListener(requireActivity(), subTask1 -> {

                                    if (subTask1.isSuccessful()) {
                                        Log.d("DELETE CURRENT USER",
                                                "User account deleted.");
                                    }});

                        Toast.makeText(requireContext(),
                                "Failed to register! Try again...",
                                Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }});
        Intent intent = new Intent(getContext(), IntroSlider.class); //todo: make sure
        startActivity(intent); //todo: make sure
    }
    // =============================================================================================
}