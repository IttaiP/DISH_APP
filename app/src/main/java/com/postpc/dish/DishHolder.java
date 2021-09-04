package com.postpc.dish;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DishHolder extends RecyclerView.ViewHolder {

    private final TextView dish_name;
    private final TextView dish_description;
    private TextView restaurant_name;

    public DishHolder(@NonNull View itemView) {
        super(itemView);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_description = itemView.findViewById(R.id.dish_description);
//        dishImage = itemView.findViewById(R.id.dish_image);
    }


    public TextView get_dish_name() {
        return dish_name;
    }

    public TextView get_dish_description() {
        return dish_description;
    }

    public void set_dish_name(String name) {
        dish_name.setText(name);
    }

    public void set_dish_description(String description) {
        dish_description.setText(description);
    }


    public void bind(DishItem dish) {
        dish_name.setText(dish.getName());
        dish_description.setText(dish.getDescription());
    }
}
