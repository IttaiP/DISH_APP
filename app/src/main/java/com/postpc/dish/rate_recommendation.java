package com.postpc.dish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class rate_recommendation extends Fragment {

    private RateRecommendationViewModel mViewModel;
    private RatingBar stars;
    private TextView tView;
    private Button rateBtn;

    public static rate_recommendation newInstance() {
        return new rate_recommendation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rate_recommendation_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel =new ViewModelProvider(this).get(RateRecommendationViewModel.class);
//        mViewModel.setUser_Email("shmu@gmail.com");
//        mViewModel.setRestuarant("Pizza Lila");
        // todo: add user email , and restaurant and dish (after chosed dish) to shared preferences

        stars = (RatingBar) view.findViewById(R.id.ratingBar);
        tView = (TextView) view.findViewById(R.id.ratingText);
        rateBtn = (Button)view.findViewById(R.id.btnRate);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int noofstars = stars.getNumStars();
                float getrating = stars.getRating();
                tView.setText("Rating: " + getrating + "/" + noofstars);
//                mViewModel.rateDish(getrating, "Margarita" );
            }
        });



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RateRecommendationViewModel.class);
        // TODO: Use the ViewModel
    }

}