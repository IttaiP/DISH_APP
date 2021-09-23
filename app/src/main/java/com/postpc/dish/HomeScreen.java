package com.postpc.dish;

import static com.google.firebase.auth.EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD;
import static com.google.firebase.auth.GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.postpc.dish.databinding.ActivityHomeScreenBinding;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityHomeScreenBinding binding;
    private DishApplication app;
    FirebaseAuth auth;
    private DrawerLayout drawer;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        app = (DishApplication) getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.nav_fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
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
                app.runWork2();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, new SearchFragment()).commit();
                break;

            case R.id.nav_get_custom_menu:
                app.runWork2();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, new GetCustomMenuFragment()).commit();
                break;

            case R.id.nav_rate_recommendation:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, new RateRecommendationFragment()).commit();
                break;

            case R.id.nav_swipe_dishes:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.nav_fragment_container, new InitUserDishDataFragment()).commit();
                break;

            case R.id.nav_logout:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String providerId = null;
                if (user != null) {
                    for (UserInfo profile : user.getProviderData()) {
                        // Id of the provider (ex: google.com)
                        providerId = profile.getProviderId();
                    }
                }

                if (providerId != null) {
                    switch (providerId) {
                        case EMAIL_PASSWORD_SIGN_IN_METHOD:
                            signOutEmailAndPassword();
                            break;

                        case GOOGLE_SIGN_IN_METHOD:
                            signOutGoogle();
                            break;
                    }
                }
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // todo: signOutGoogle
    private void signOutGoogle() {
        // Firebase sign out
        auth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent intent = new Intent(this, MainActivity.class);

        });
    }


//        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.appBarMainScreen.toolbar);
//        binding.appBarMainScreen.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home,
//                R.id.nav_search,R.id.nav_get_custom_menu, R.id.nav_rate_recommendation)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_screen);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_screen, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_screen);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
    }
