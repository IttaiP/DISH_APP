package com.postpc.dish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class restaurnats_adapter extends RecyclerView.Adapter<restaurant_view> {

    private Context context;
    private List<Restaurant> restaurants;
    private SharedViewModel sharedViewModel;

    restaurnats_adapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public restaurant_view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_adapter, parent, false);
        return new restaurant_view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull restaurant_view holder, int position) {

        holder.bind(restaurants.get(position), context);
        ImageView restaurant_image = holder.get_restaurant_image();
        TextView restaurant_name = holder.get_restaurant_name();

        restaurant_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Bundle arguments = new Bundle();
                Log.d("name", restaurant_name.getText().toString());
                arguments.putString("restaurant", restaurant_name.getText().toString());
                Fragment menu_fragment = new resturant_menu();
                menu_fragment.setArguments(arguments);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.rec, menu_fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setAdapter(List<Restaurant> new_restaurants) {
        this.restaurants = new_restaurants;
    }
}
