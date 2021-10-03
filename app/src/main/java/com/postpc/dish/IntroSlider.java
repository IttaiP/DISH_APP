package com.postpc.dish;

import static com.github.appintro.AppIntroPageTransformerType.*;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class IntroSlider extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppIntroPageTransformerType depth = Depth.INSTANCE;
        setTransformer(depth);

        // all slides of intro
        addSlide(AppIntroFragment.newInstance("Hey!", "Let's go on a tour",
                R.drawable.first_demo, Color.rgb(19, 42, 50)));

        addSlide(AppIntroFragment.newInstance("Swipe your opinion on the dish",
                "Choose the type of food at the top of the screen.\n " +
                        "Swipe the dish to the direction you see fit, or click the red 'X' button if you do not want to answer.\n" +
                        "The more dishes you swipe, the better our offers will be :)",
                R.drawable.swipe_demo, Color.YELLOW));

        addSlide(AppIntroFragment.newInstance("Search restaurant",
                "You can search restaurant by name, location or wifi.",
                R.drawable.search_demo, Color.rgb(19, 42, 50)));

        addSlide(AppIntroFragment.newInstance("Get your custom menu",
                "Click on the button below to find out which of the dishes in the restaurant there is a high probability you will like.",
                R.drawable.custom_menu_demo, Color.BLUE));

        addSlide(AppIntroFragment.newInstance("See real photos by other users",
                "Click on the dish to see real photos of it taken by other users.",
                R.drawable.photos_by_users_demo, Color.GREEN));

        addSlide(AppIntroFragment.newInstance("Make an order",
                "Swipe left the dish you want.",
                R.drawable.order_demo, Color.RED));

        addSlide(AppIntroFragment.newInstance("Rate out recommendation",
                "After you tried the dish, tell us what you thought by rating it.",
                R.drawable.rate_demo, Color.MAGENTA));

        setSkipButtonEnabled(true);
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Toast.makeText(this, "Skipped App Intro", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Toast.makeText(this, "App Intro ended", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
