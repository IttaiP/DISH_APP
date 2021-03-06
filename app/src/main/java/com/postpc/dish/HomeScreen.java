package com.postpc.dish;

import static com.google.firebase.auth.EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD;
import static com.google.firebase.auth.GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDeepLinkRequest;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityHomeScreenBinding binding;
    private DishApplication app;
    FirebaseAuth auth;
    private DrawerLayout drawer;
    private GoogleSignInClient mGoogleSignInClient;
    HashMap<String, List<String>> urisToUpload;
    TextView userName;
    TextView email;
    Intent intentCreatedMe;
    InitUserDishDataViewModel initUserDishDataViewModel;
    SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        userName = headerView.findViewById(R.id.header_user_name);
        email = headerView.findViewById(R.id.header_email);

        intentCreatedMe = getIntent();

        userName.setText(intentCreatedMe.getStringExtra("Full Name"));
        email.setText(intentCreatedMe.getStringExtra("Email"));


        app = (DishApplication) getApplicationContext();

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initLiveDataListeners();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.nav_fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    public void initLiveDataListeners(){
        initUserDishDataViewModel = new ViewModelProvider(this).get(InitUserDishDataViewModel.class);
        // Create the observer which updates the UI.
        final Observer<List<OtherUser>> otherUserLiveDataObserver = new Observer<List<OtherUser>>() {
            @Override
            public void onChanged(@Nullable final List<OtherUser> newOtherUserLiveData) {
                // todo: store other users
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        initUserDishDataViewModel.getotherUserLiveData().observe(this, otherUserLiveDataObserver);

        final Observer<List<OtherUser>> otherUserCalcLiveDataObserver = new Observer<List<OtherUser>>() {
            @Override
            public void onChanged(@Nullable final List<OtherUser> newOtherUserCalcLiveData) {
                // todo: store other users
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        initUserDishDataViewModel.getotherUserCalcSimLiveData().observe(this, otherUserCalcLiveDataObserver);

        // Create the observer which updates the UI.
        final Observer<List<String>> emailOtherUserLiveDataObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> newemailOtherUserLiveData) {
                // todo: store other users
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        initUserDishDataViewModel.getemailOtherUserLiveData().observe(this, emailOtherUserLiveDataObserver);

        // Create the observer which updates the UI.
        final Observer<List<String>> emailOtherUserCalcLiveDataObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> newemailOtherUserCalcLiveData) {
                // todo: store other users
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        initUserDishDataViewModel.getemailOtherUserCalcSimLiveData().observe(this, emailOtherUserCalcLiveDataObserver);

        final Observer<HashMap<String, List<String>>> uriObserver = toUpload -> {

            for(Map.Entry<String, List<String>> dish_photo_uri: urisToUpload.entrySet()) {
                for(String uri : dish_photo_uri.getValue()) {
                    app.info.database.collection("all-dishes").document(dish_photo_uri.getKey()).update("photos", FieldValue.arrayUnion(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("UPLOADED", "successfully");
                        }
                    });
                }
            }
        };

        sharedViewModel.getUriLiveData().observe(this, uriObserver);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, new HomeFragment()).commit();
                break;

            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, new SearchFragment()).commit();
                break;

            case R.id.nav_get_custom_menu:
//                app.runWork2();
//                getSupportFragmentManager().beginTransaction().
//                        replace(R.id.nav_fragment_container, new GetCustomMenuFragment()).commit();
                break;

            case R.id.nav_rate_recommendation:
                // todo: check if the bundle works
                Bundle bundle = new Bundle();
                String dishToRateId = "";
                try {
                    dishToRateId = app.info.getDishesToRate().get(0);
                }
                catch (Exception exception) {
                    dishToRateId = "";
                }
                bundle.putString("dish ID to rate", dishToRateId);
                Fragment fragment = new RateRecommendationFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, fragment).commit();
                break;

            case R.id.nav_swipe_dishes:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, new InitUserDishDataFragment()).commit();
                break;

            // todo: finish logout
            case R.id.nav_logout:
                app.restartData();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                HomeScreen.this.finish();

//                FirebaselUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String providerId = null;
//                if (user != null) {
//                    for (UserInfo profile : user.getProviderData()) {
//                        // Id of the provider (ex: google.com)
//                        providerId = profile.getProviderId();
//                    }
//                }
//
//                if (providerId != null) {
//                    switch (providerId) {
//                        case EMAIL_PASSWORD_SIGN_IN_METHOD:
//                            signOutEmailAndPassword();
//                            break;
//
//                        case GOOGLE_SIGN_IN_METHOD:
//                            signOutGoogle();
//                            break;
//                    }
//                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOutEmailAndPassword() {
        // Firebase sign out
        auth.signOut();

        // todo: finish logout
        getSupportFragmentManager().beginTransaction().
                replace(R.id.nav_fragment_container, WelcomeScreenFragment.newInstance()).commit();
    }

    private void signOutGoogle() {
        // Firebase sign out
        auth.signOut();

        // todo: finish logout
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.nav_fragment_container, WelcomeScreenFragment.newInstance()).commit();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addUriToUpload(String dish_id, Uri uri) {
        if(urisToUpload == null) {
            urisToUpload = new HashMap<>();
        }
        if (urisToUpload.get(dish_id) == null) {
            urisToUpload.put(dish_id, new ArrayList<>());
        }
        urisToUpload.get(dish_id).add(uri.toString());
    }

}
