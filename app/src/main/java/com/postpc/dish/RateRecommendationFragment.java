package com.postpc.dish;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.DashPathEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RateRecommendationFragment extends Fragment {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
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
            dishName.setText("You ordered " + dish.getName());
            restaurantName.setText("From " + dish.getRestaurantName());
        });

        // listeners
        submitButton.setOnClickListener(view1 -> {
            Map<String, Object> dishRating = new HashMap<>();
            dishRating.put("Dish_Name", dishName.getText().toString());
            dishRating.put("Rating", rating);

            app.info.database.collection("users").document(app.info.myID)
                    .collection("Ratings").document(dishToRateID).set(dishRating)
                    .addOnCompleteListener(task -> {
                        // if this dish wad already rated by the user:
                        if (task.isSuccessful()){
                            app.info.removeDishFromToRate(dishToRateID);
                            Toast.makeText(this.getContext(), "Thank you for rating!", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            Log.e("SUCCESS", "update dish to user's ratings");
                        }
                        else {
                            Log.e("FAILURE", "Failed to update");
                        }
                    });
        });

        addPhotoButton.setOnClickListener(view12 -> {
            // check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    // permission not granted, request it.
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    // show popup
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    // permission already granted
                    pickImageFromGallery();
                }
            }
            else {
                // system os is less then marshmallow
                pickImageFromGallery();
            }

        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted
                    pickImageFromGallery();
                }
                else {
                    Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri imageUri = data.getData();
            StorageReference storageReference = app.info.firebaseStorage.getReference();
            String ts = String.valueOf(System.currentTimeMillis()/1000);
            StorageReference photoRef = storageReference.child(dishToRateID + "/" + app.info.myID + ts);
            photoRef.putFile(imageUri).addOnCompleteListener(task ->
                    Log.e("SUCCESS", String.valueOf(task.getResult().getTask().getSnapshot().getUploadSessionUri())));
//            Uri uri = photoRef.getDownloadUrl().getResult();
//            Log.e("URI IS", String.valueOf(uri));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RateRecommendationViewModel.class);
        // TODO: Use the ViewModel
    }
}