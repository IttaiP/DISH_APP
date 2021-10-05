package com.postpc.dish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.text.DecimalFormat;

public class CustomDishHolder extends RecyclerView.ViewHolder {

    private final TextView dish_name, dish_description, dish_score;
    private final LinearLayout order;
    private TextView restaurant_name;
    private final LinearLayout linearLayout;
    private final SwipeRevealLayout swipeRevealLayout;

    public CustomDishHolder(@NonNull View itemView) {
        super(itemView);
        swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_description = itemView.findViewById(R.id.dish_description);
        dish_score = itemView.findViewById(R.id.dish_score);
        order = swipeRevealLayout.findViewById(R.id.order);
        linearLayout = itemView.findViewById(R.id.linear_layout);
//        dishImage = itemView.findViewById(R.id.dish_image);
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public SwipeRevealLayout getSwipeRevealLayout() { return swipeRevealLayout; }

    public LinearLayout getOrder() {
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


    public void bind(DishItem dish) {
        dish_name.setText(dish.getName());
        dish_description.setText(dish.getName());
        String num = String.format("%.2f", dish.getMatch());
        dish_score.setText("Matching is " + num);
        if(dish.match >= 84) {
            swipeRevealLayout.setBackgroundResource(R.drawable.background_green_recommend);
        }
        else if(dish.match >= 68) {
            swipeRevealLayout.setBackgroundResource(R.drawable.background_yellow_recommend);
        }
        else {
            swipeRevealLayout.setBackgroundResource(R.drawable.background_red_recommend);
        }
    }
}
