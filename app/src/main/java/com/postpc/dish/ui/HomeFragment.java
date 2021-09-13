package com.postpc.dish.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.postpc.dish.R;
public class resturant_custom_menu extends Fragment {
    DishApplication app = (DishApplication)getActivity().getApplicationContext();

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(ResturantCustomMenuViewModel.class);
        app.load_rated_dishes_from_sp();
        Log.e("rated dishes", app.info.ratings.toString());



//        view.findViewById(R.id.button1).setOnClickListener(view1 -> app.load_similar_users());

//        view.findViewById(R.id.button3).setOnClickListener(view12 -> mViewModel.calculateSimilarities());

        view.findViewById(R.id.button2).setOnClickListener(view13 -> mViewModel.personalizeReccomendation());





    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

}