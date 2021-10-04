package com.postpc.dish;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetCustomMenuFragment extends Fragment {

    private GetCustomMenuViewModel mViewModel;
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private FirebaseFirestore database;
    private DividerItemDecoration vertical_decorator;
    private DividerItemDecoration horizontal_decorator;
    private ProgressBar progressBar;
    private CustomDishesAdapter adapter;
    private List<DishItem> dishes;
    private String restaurant;

    public static GetCustomMenuFragment newInstance() {
        return new GetCustomMenuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.get_custom_menu_fragment, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetCustomMenuViewModel customMenuViewModel = new ViewModelProvider(this).get(GetCustomMenuViewModel.class);
        Context context = getContext();

        database = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.list_of_dishes);
        vertical_decorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        vertical_decorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.decorator_for_dishes)));
        horizontal_decorator = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        horizontal_decorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.decorator_for_dishes)));
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_custom_menu_screen);
        recyclerView.addItemDecoration(vertical_decorator);
        recyclerView.addItemDecoration(horizontal_decorator);
        dishes = new ArrayList<>();
        adapter = new CustomDishesAdapter();

//        adapter.setDishesAdapter(dishes);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        Bundle arguments = getArguments();
        String restaurant = arguments.getString("restaurant");

        TextView restaurant_name = view.findViewById(R.id.restaurant_name);
        ImageView restaurant_image = view.findViewById(R.id.image_restaurant);

        restaurant_name.setText(restaurant);

        Log.d("restaurant", restaurant);

        // Create the observer which updates the UI.
        final Observer<HashMap<String, Float>> nameObserver = new Observer<HashMap<String, Float>>() {
            @Override
            public void onChanged(@Nullable final HashMap<String, Float> newReccomendations) {
                for(Map.Entry<String, Float> dish_recommended: customMenuViewModel.app.info.DishRecommendationScores.entrySet()) {
//                    Log.e("IM IN ", customMenuViewModel.app.info.DishReccomendationScores.toString());
                    database.collection("all-dishes").document(dish_recommended.getKey()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(dish_recommended.getValue() != null && dish_recommended.getValue() >= 50) {
                                adapter.addDishes(Objects.requireNonNull(documentSnapshot.toObject(DishItem.class)), dish_recommended.getValue());
                                Collections.sort(adapter.getDishesAdapter(), new SortByMatch());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
//                Log.e("DISHES ARE ", adapter.getDishesAdapter().toString());
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        customMenuViewModel.getLiveDataDishReccomendationScores().observe(getViewLifecycleOwner(), nameObserver);

        customMenuViewModel.personalizeReccomendation(restaurant);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GetCustomMenuViewModel.class);
        // TODO: Use the ViewModel
    }

}