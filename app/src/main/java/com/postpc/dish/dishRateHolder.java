package com.postpc.dish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class dishRateHolder extends RecyclerView.ViewHolder {

    private final TextView dish_name;
    private final TextView restaurant_name;
    private final ImageView dish_image;
    private final LinearLayout rate_dish;

    public dishRateHolder(@NonNull View itemView) {
        super(itemView);
        dish_name = itemView.findViewById(R.id.dish_name);
        restaurant_name = itemView.findViewById(R.id.dish_restaurant_name);
        dish_image = itemView.findViewById(R.id.dish_image);
        rate_dish = itemView.findViewById(R.id.rate_dish);
    }


    public TextView get_dish_name() {
        return dish_name;
    }

    public TextView get_dish_restaurant_name() {
        return restaurant_name;
    }

    public LinearLayout getRate_dish() { return rate_dish; }

    public ImageView get_dish_image() { return dish_image; }


    @SuppressLint("DefaultLocale")
    public void bind(DishItem dish, Context context) {
        dish_name.setText(dish.getName());
        restaurant_name.setText(dish.getRestaurantName());
        int resourceId = context.getResources().getIdentifier(dish.photo, "drawable",
                context.getPackageName());
        dish_image.setImageResource(resourceId);
    }
}
