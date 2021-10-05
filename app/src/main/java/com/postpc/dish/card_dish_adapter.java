package com.postpc.dish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
    public void onBindViewHolder(@NonNull card_dish_holder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(dishes.get(position), context);
        Button remove = holder.itemView.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("position ", String.valueOf(position));
                Log.e("Dishes are ", dishes.toString());
                for(int i=0; i<=position; i++){
                    dishes.remove(dishes.get(0));

                }
                setDishes(dishes);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public List<DishItem> getDishes() {
        return dishes;
    }

    public void removeDish(DishItem dish) {
        dishes.remove(dish);
    }

    public void addDish(DishItem dish) {
        this.dishes.add(dish);
    }

    public void setDishes(List<DishItem> newList) {
        this.dishes = newList;
        notifyDataSetChanged();
    }

}
