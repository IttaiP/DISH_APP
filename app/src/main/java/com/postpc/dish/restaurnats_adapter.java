package com.postpc.dish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class restaurnats_adapter extends RecyclerView.Adapter<restaurant_view> {

    private Context context;
    private List<Restaurant> restaurants;

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
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setAdapter(List<Restaurant> new_restaurants) {
        this.restaurants = new_restaurants;
    }
}
