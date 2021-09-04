package com.postpc.dish;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class search_resturants extends Fragment {

    private EditText search;
    private RecyclerView recycler_view;
    private FirebaseFirestore database;

    private ArrayList<Restaurant> restaurants;
    private restaurnats_adapter adapter;

    private SearchResturantsViewModel mViewModel;

    public static search_resturants newInstance() {
        return new search_resturants();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_resturants_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search = view.findViewById(R.id.search_bar);
        recycler_view = view.findViewById(R.id.recycler_view);
        database = FirebaseFirestore.getInstance();

        restaurants = new ArrayList<>();
        adapter = new restaurnats_adapter(restaurants);

        //Set up recycler view
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(adapter);

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
                navController.navigate(R.id.action_search_resturants2_to_resturant_menu);

            }
        });

        view.findViewById(R.id.button_custom_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_search_resturants2_to_resturant_custom_menu);

            }
        });
    }

    private void search_in_firestore(String search) {
        Log.d("Here", "im here");
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchResturantsViewModel.class);
        // TODO: Use the ViewModel
    }

}