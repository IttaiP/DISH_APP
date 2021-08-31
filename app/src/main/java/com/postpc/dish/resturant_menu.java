package com.postpc.dish;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        restaurant = sharedViewModel.getRestuarant();

        recycler_view = view.findViewById(R.id.list_of_dishes);
        dishes = new ArrayList<>();
        adapter = new DishesAdapter();

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(adapter);

//        String restaurant = getActivity().getIntent().getStringExtra("restaurant");
//        collection("dishes").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        database.collection("restaurants").document(restaurant)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DishItem item = documentSnapshot.toObject(DishItem.class);
                Log.d("Value is", item.getName());
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

        adapter.submitList(dishes);


        TextView textView = view.findViewById(R.id.restaurant_name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("pressed", "ffgrgrgrg");

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ResturantMenuViewModel.class);
        // TODO: Use the ViewModel
    }

}