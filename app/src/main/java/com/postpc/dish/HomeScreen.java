package com.postpc.dish;

import static com.google.firebase.auth.EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD;
import static com.google.firebase.auth.GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private AppBarConfiguration mAppBarConfiguration;
//    private ActivityHomeScreenBinding binding;
    private DishApplication app;
    FirebaseAuth auth;
    private DrawerLayout drawer;
    private GoogleSignInClient mGoogleSignInClient;
    TextView userName;
    TextView email;
    Intent intentCreatedMe;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
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
//                            signOutEmailAndPassword();
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
            startActivity(intent);
        });
    }
}
