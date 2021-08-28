package com.postpc.dish;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DishHolder extends RecyclerView.ViewHolder {

    private TextView dishName;
    private ImageView dishImage;

    public DishHolder(@NonNull View itemView) {
        super(itemView);
        dishName = itemView.findViewById(R.id.dish_name);
        dishImage = itemView.findViewById(R.id.dish_image);
    }

    public ImageView getDishImage() {
        return dishImage;
    }

    public TextView getDishName() {
        return dishName;
    }

    public void setDishName(String name) {
        dishName.setText(name);
    }

    public void setDishImage(ImageView dishImage) {
        // TODO
    }
}
