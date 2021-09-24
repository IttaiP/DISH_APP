package com.postpc.dish;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CustomDishHolder extends RecyclerView.ViewHolder {

    private final TextView dish_name;
    private final TextView dish_description;
    private final TextView dish_score;
    private final ConstraintLayout dish;
    private TextView restaurant_name;

    public CustomDishHolder(@NonNull View itemView) {
        super(itemView);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_description = itemView.findViewById(R.id.dish_description);
        dish_score = itemView.findViewById(R.id.dish_score);
        dish = itemView.findViewById(R.id.dish);
//        dishImage = itemView.findViewById(R.id.dish_image);
    }


    public TextView get_dish_name() {
        return dish_name;
    }

    public TextView get_dish_description() {
        return dish_description;
    }

    public ConstraintLayout get_dish() {return dish;}

    public void set_dish_name(String name) {
        dish_name.setText(name);
    }

    public void set_dish_description(String description) {
        dish_description.setText(description);
    }


    public void bind(DishItem dish) {
        dish_name.setText(dish.getName());
        dish_description.setText(dish.getDescription());
        dish_score.setText(String.valueOf(dish.getMatch()));
    }
}
