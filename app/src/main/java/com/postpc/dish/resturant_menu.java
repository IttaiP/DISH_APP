package com.postpc.dish;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class resturant_menu extends Fragment {

    private ResturantMenuViewModel mViewModel;
    private SharedViewModel sharedViewModel;
    private RecyclerView recycler_view;
    private FirebaseFirestore database;

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

        database = FirebaseFirestore.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        restaurant = sharedViewModel.getRestuarant();

        recycler_view = view.findViewById(R.id.list_of_dishes);
        dishes = new ArrayList<>();
        adapter = new DishesAdapter();
//        adapter.setDishesAdapter(dishes);

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_view.setAdapter(adapter);

        Bundle arguments = getArguments();
        String restaurant = arguments.getString("restaurant");

//        Restaurant restaurant1 = search_resturants.newInstance().find_restaurant_by_name(restaurant);

        Log.d("restaurant", restaurant);

//        String restaurant = getActivity().getIntent().getStringExtra("restaurant");
//        collection("dishes").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        database.collection("restaurants").whereEqualTo("name", restaurant)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant1 = document.toObject(Restaurant.class);
                        Log.d("name", restaurant1.name);
                        document.getReference().collection("dishes").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    dishes = (ArrayList<DishItem>) Objects.requireNonNull(task.getResult()).toObjects(DishItem.class);
//                                    for (DishItem dish : dishes) {
//                                        Log.d("dish name", dish.getName());
//                                    }
                                    adapter.setDishesAdapter(dishes);
//                                    adapter.submitList(dishes);
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
//        {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    dishes = (ArrayList<DishItem>) Objects.requireNonNull(task.getResult()).toObjects(DishItem.class);
//                    adapter.setAdapter(dishes);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Log.d("Not Found", "Error: " + task.getException().getMessage());
//                }
//            }
//        });

//        adapter.submitList(dishes);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ResturantMenuViewModel.class);
        // TODO: Use the ViewModel
    }

}