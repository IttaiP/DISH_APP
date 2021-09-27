package com.postpc.dish;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class RateRecommendationFragment extends Fragment {

    private DishApplication app;
    private FirebaseAuth auth;
    private RateRecommendationViewModel mViewModel;
    private RatingBar stars;
    private String dishToRateID = null;
    private ImageView dishImage;
    private TextView dishName, restaurantName;
    private FloatingActionButton addPhotoButton;
    private Button submitButton;
    private float rating;

    public static RateRecommendationFragment newInstance() {
        return new RateRecommendationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rate_recommendation_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(com.postpc.dish.RateRecommendationViewModel.class);
        app = (DishApplication) getActivity().getApplication();
        auth = FirebaseAuth.getInstance();

        // get dish to rate from bundle:
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dishToRateID = bundle.getString("dish ID to rate");
            Log.e("DISH TO RATE ID: ", dishToRateID);
        }

        // find views by ID:
        dishImage = (ImageView) view.findViewById(R.id.dish_image);
        dishName = (TextView) view.findViewById(R.id.dish_name);
        restaurantName = (TextView) view.findViewById(R.id.rate_restaurant_name);
        addPhotoButton = (FloatingActionButton) view.findViewById(R.id.add_photo_button);
        stars = (RatingBar) view.findViewById(R.id.ratingBar);
        submitButton = (Button) view.findViewById(R.id.submit_button);

        stars.setOnRatingBarChangeListener((ratingBar, v, b) -> {
            rating = stars.getRating();
        });

        // set picture
        app.info.database.collection("all-dishes").document(dishToRateID).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            DishItem dish = documentSnapshot.toObject(DishItem.class);
            assert dish != null;
            int resourceId = getContext().getResources().getIdentifier(dish.photo, "drawable",
                    getContext().getPackageName());
            dishImage.setImageResource(resourceId);
            dishName.setText(dish.getName());
            restaurantName.setText(dish.getRestaurantName());
        });

        // listeners
        submitButton.setOnClickListener(view1 -> {
            Map<String, Object> dishRating = new HashMap<>();
            dishRating.put("Dish_Name", dishName.getText().toString());
            dishRating.put("Rating", rating);

            app.info.database.collection("users").document(app.info.myID)
                    .collection("Ratings").document(dishToRateID).get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot ds = task.getResult();

                        // if this dish wad already rated by the user:
                        if (task.isSuccessful()){
                            ds.getReference().update("Rating", rating)
                                    .addOnSuccessListener(unused ->
                                            Log.e("SUCCESS", "update dish to user's ratings"))
                                    .addOnFailureListener(e ->
                                            Log.e("FAILURE", "FAILED to update dish to user's ratings"));
                        }

                        // if this is the first time the user rates this dish
                        else {
                            ds.getReference().set(dishRating)
                                    .addOnSuccessListener(unused ->
                                            Log.e("SUCCESS", "add dish to user's ratings"))
                                    .addOnFailureListener(e ->
                                            Log.e("FAILURE", "FAILED to add dish to user's ratings"));
                        }
                        Toast.makeText(this.getContext(), "Thank you for rating!", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RateRecommendationViewModel.class);
        // TODO: Use the ViewModel
    }
}