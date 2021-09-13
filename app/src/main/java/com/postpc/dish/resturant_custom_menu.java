package com.postpc.dish;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class resturant_custom_menu extends Fragment {
    DishApplication app = (DishApplication)getActivity().getApplicationContext();

    private ResturantCustomMenuViewModel mViewModel;

    public static resturant_custom_menu newInstance() {
        return new resturant_custom_menu();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.resturant_custom_menu_fragment, container, false);
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

        // TODO: Use the ViewModel
    }

}