package com.postpc.dish;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class wifiRestaurantsAdapter extends RecyclerView.Adapter<wifiRestaurantsHolder> {

    private Context context;
    private List<Restaurant> restaurants;

    private DishApplication app;

    wifiRestaurantsAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public wifiRestaurantsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.wifi_restaurant, parent, false);
        return new wifiRestaurantsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull wifiRestaurantsHolder holder, int position) {

        holder.bind(restaurants.get(position), context);
        ImageView restaurant_image = holder.get_restaurant_image();
        TextView restaurant_name = holder.get_restaurant_name();

        restaurant_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                //Added by Ittai
                app = (DishApplication) activity.getApplication();
                app.info.setRestaurant(restaurant_name.getText().toString());

                app.runWork2();
                Bundle arguments = new Bundle();
                Log.d("name", restaurant_name.getText().toString());
                arguments.putString("restaurant", restaurant_name.getText().toString());
                Fragment menu_fragment = new resturant_menu();
                Fragment search_restaurants = new SearchFragment();
                menu_fragment.setArguments(arguments);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_container, menu_fragment).addToBackStack(null).commit();

            }
        });

//        custom_menu_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                //Added by Ittai
//                app = (DishApplication) activity.getApplication();
//                app.info.setRestaurant(restaurant_name.getText().toString());
//
//
//                Bundle arguments = new Bundle();
//                Log.d("name", restaurant_name.getText().toString());
//                arguments.putString("restaurant", restaurant_name.getText().toString());
//                Fragment custom_menu_fragment = new GetCustomMenuFragment();
//                custom_menu_fragment.setArguments(arguments);
//                activity.getSupportFragmentManager().beginTransaction().replace(R.id.rec, custom_menu_fragment).addToBackStack(null).commit();
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setAdapter(List<Restaurant> new_restaurants) {
        this.restaurants = new_restaurants;
    }
}
