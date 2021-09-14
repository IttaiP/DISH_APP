package com.postpc.dish;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private EditText search;
    private RecyclerView recycler_view;
    private FirebaseFirestore database;

    private ArrayList<Restaurant> restaurants;
    private restaurnats_adapter adapter;

    private SearchViewModel mViewModel;
    private SharedViewModel sharedViewModel;


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search = view.findViewById(R.id.search_bar);
        recycler_view = view.findViewById(R.id.recycler_view);
        database = FirebaseFirestore.getInstance();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        restaurants = new ArrayList<>();
        adapter = new restaurnats_adapter(restaurants);

        //Set up recycler view
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(adapter);

        database.collection("restaurants").orderBy("name")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    restaurants = (ArrayList<Restaurant>) Objects.requireNonNull(task.getResult()).toObjects(Restaurant.class);
                    adapter.setAdapter(restaurants);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Not Found", "Error: " + task.getException().getMessage());
                }
            }
        });

        // Todo: add onclick listener for each restaurant. within, write the following:
        // sharedViewModel.setRestuarant(todo restaurant name here);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search_text = search.getText().toString();
                Log.d("Search is", search_text);
                search_in_firestore(search_text);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        view.findViewById(R.id.button_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.action_search_resturants2_to_resturant_menu);

            }
        });

        view.findViewById(R.id.button_custom_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.action_search_resturants2_to_resturant_custom_menu);

            }
        });
    }

    private void search_in_firestore(String search) {
        database.collection("restaurants").orderBy("name")
                .startAt(search).endAt("search\uf8ff")
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                restaurants = (ArrayList<Restaurant>) Objects.requireNonNull(task.getResult()).toObjects(Restaurant.class);
                adapter.setAdapter(restaurants);
                adapter.notifyDataSetChanged();
            } else {
                Log.d("Not Found", "Error: " + task.getException().getMessage());
            }
        });
    }

    public Restaurant find_restaurant_by_name(String name) {
        for(Restaurant restaurant : restaurants) {
            if(restaurant.name.equals(name)) {
                return restaurant;
            }
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }
}