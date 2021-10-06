package com.postpc.dish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class resturant_menu extends Fragment implements DishesAdapter.ContentListener {

    private ResturantMenuViewModel mViewModel;
    private SharedViewModel sharedViewModel;
    private RecyclerView recycler_view;
    private FirebaseFirestore database;
    private DividerItemDecoration vertical_decorator;
    private DividerItemDecoration horizontal_decorator;
    DishApplication app;

    private DishesAdapter adapter;
    private List<DishItem> dishes;

    private String restaurant;

    public static resturant_menu newInstance() {
        return new resturant_menu();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.resturant_menu_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (DishApplication) getActivity().getApplication();
        app.runWork2();

        Context context = getContext();
        Bundle arguments = getArguments();
        restaurant = arguments.getString("restaurant");

        database = FirebaseFirestore.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
//        restaurant = sharedViewModel.getRestuarant();

        recycler_view = view.findViewById(R.id.list_of_dishes);
        vertical_decorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        vertical_decorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.decorator_for_dishes)));
        horizontal_decorator = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        horizontal_decorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.decorator_for_dishes)));

        recycler_view.addItemDecoration(vertical_decorator);
        recycler_view.addItemDecoration(horizontal_decorator);
        dishes = new ArrayList<>();
        adapter = new DishesAdapter(this::onItemClicked);
//        adapter.setDishesAdapter(dishes);

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_view.setAdapter(adapter);

        TextView restaurant_name = view.findViewById(R.id.restaurant_name);
        ImageView restaurant_image = view.findViewById(R.id.image_restaurant);

        restaurant_name.setText(restaurant);

        Log.d("restaurant", restaurant);

        database.collection("restaurants").whereEqualTo("name", restaurant)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant1 = document.toObject(Restaurant.class);
                        int resourceId = context.getResources().getIdentifier(restaurant1.code, "drawable",
                                context.getPackageName());
                        restaurant_image.setImageResource(resourceId);
                        Log.d("name", restaurant1.name);
                        document.getReference().collection("dishes").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    dishes = (ArrayList<DishItem>) Objects.requireNonNull(task.getResult()).toObjects(DishItem.class);
                                    adapter.setDishesAdapter(dishes);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.d("Not Found", "Error: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
                else {
                    Log.d("Error", "Error trying to receive");
                }
            }
        });

        Button get_custom_menu = view.findViewById(R.id.custom_menu_button);
        get_custom_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle arguments = new Bundle();
//                Log.d("name", restaurant_name.getText().toString());
                arguments.putString("restaurant", restaurant);
                Fragment custom_menu_fragment = new GetCustomMenuFragment();
                custom_menu_fragment.setArguments(arguments);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_container, custom_menu_fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ResturantMenuViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClicked(@NonNull DishItem item) {
        Bundle arguments = new Bundle();
        app.info.database.collection("all-dishes").whereEqualTo("name", item.name).whereEqualTo("restaurant_name", item.restaurant_name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()) {
                    arguments.putString("dishId", documentSnapshot.getId());
                    arguments.putSerializable("dish", item);
                    Fragment galleryFragment = new GalleryFragment();
                    galleryFragment.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_container, galleryFragment).addToBackStack(null).commit();
                }
            }
        });
    }

}