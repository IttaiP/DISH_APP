package com.postpc.dish;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class restaurant_view extends RecyclerView.ViewHolder {
    private final TextView name;
    private final TextView city;
    private final TextView category;

    public restaurant_view(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.restaurantNameTextView);
        city = itemView.findViewById(R.id.cityTextView);
        category = itemView.findViewById(R.id.categoryTextView);
    }

    public TextView get_restaurant_name() {
        return name;
    }

    public TextView get_restaurant_city() {
        return city;
    }

    public TextView get_restaurant_category() {
        return category;
    }

    public void bind(Restaurant restaurant) {
        name.setText(restaurant.name);
        city.setText(restaurant.city);
        category.setText(restaurant.category);
    }
}
