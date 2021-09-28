package com.postpc.dish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.text.DecimalFormat;

public class CustomDishHolder extends RecyclerView.ViewHolder {

    private final TextView dish_name, dish_description, dish_score, order;
    private TextView restaurant_name;
    private final SwipeRevealLayout swipeRevealLayout;

    public CustomDishHolder(@NonNull View itemView) {
        super(itemView);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_description = itemView.findViewById(R.id.dish_description);
        dish_score = itemView.findViewById(R.id.dish_score);
        order = itemView.findViewById(R.id.order);
        swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
//        dishImage = itemView.findViewById(R.id.dish_image);
    }

    public SwipeRevealLayout getSwipeRevealLayout() {
        return swipeRevealLayout;
    }

    public TextView getOrder() {
        return order;
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


    @SuppressLint("DefaultLocale")
    public void bind(DishItem dish) {
        dish_name.setText(dish.getName());
        dish_description.setText(dish.getDescription());
        String num = String.format("%.2f", dish.getMatch());
        dish_score.setText("Matching is " + num);
    }
}
