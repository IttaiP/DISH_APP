package com.postpc.dish;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

public class DishHolder extends RecyclerView.ViewHolder {

    private final SwipeRevealLayout swipeRevealLayout;
    private final LinearLayout order;
    private final LinearLayout linearLayout;
    private final TextView dish_name;
    private final TextView dish_description;
    private TextView restaurant_name;

    public DishHolder(@NonNull View itemView) {
        super(itemView);
        swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_description = itemView.findViewById(R.id.dish_description);
        order = swipeRevealLayout.findViewById(R.id.order);
        linearLayout = itemView.findViewById(R.id.linear_layout);
    }

    public LinearLayout getOrder() {
        return order;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public SwipeRevealLayout getSwipeRevealLayout() { return swipeRevealLayout; }

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
