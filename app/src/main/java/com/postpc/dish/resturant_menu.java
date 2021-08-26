package com.postpc.dish;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class resturant_menu extends Fragment {

    private ResturantMenuViewModel mViewModel;
    private RecyclerView recyclerView;
    List<DishItem> dishes;

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
        recyclerView = view.findViewById(R.id.list_of_dishes);

        List<DishItem> dishes = new ArrayList<>();
        dishes.add(new DishItem("salad", "aroma", R.drawable.salad_2756467_1920));

        DishesAdapter adapter = new DishesAdapter();
        recyclerView.setAdapter(adapter);
        adapter.submitList(dishes); // adapter got method "submitList()" by inheritance

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));

        dishes.add(new DishItem("salmon", "aroma324", R.drawable.salmon_518032_1920));
        adapter.submitList(dishes);
        dishes.add(new DishItem("sartgrtglad2", "aroergtgma", R.drawable.cala_w6ftfbpcs9i_unsplash));
        adapter.submitList(dishes);



        TextView textView = view.findViewById(R.id.restaurant);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dishes.add(new DishItem("sartgrtglad2", "aroergtgma", R.drawable.salmon_518032_1920));
                adapter.submitList(dishes);
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