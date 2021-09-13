package com.postpc.dish;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class card_dish_adapter extends RecyclerView.Adapter<card_dish_holder> {

    private Context context;
    private List<DishItem> dishes;

    card_dish_adapter() {
        this.dishes = new ArrayList<>();
    }

    @NonNull
    @Override
    public card_dish_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.dish_card, parent, false);
        return new card_dish_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull card_dish_holder holder, int position) {
        holder.bind(dishes.get(position), context);
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public List<DishItem> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishItem> newList) {
        this.dishes = newList;
        notifyDataSetChanged();
    }

}
