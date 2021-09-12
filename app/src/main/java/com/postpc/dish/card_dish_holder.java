package com.postpc.dish;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class card_dish_holder extends RecyclerView.ViewHolder {

    private final TextView dish_name;
    private final TextView dish_description;
    private final ImageView dish_photo;

    public card_dish_holder(@NonNull View itemView) {
        super(itemView);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_description = itemView.findViewById(R.id.dish_description);
        dish_photo = itemView.findViewById(R.id.dish_photo);
    }

    public void bind(DishItem dish_item, Context context) {
        dish_name.setText(dish_item.name);
        dish_description.setText(dish_item.description);
        int resourceId = context.getResources().getIdentifier(dish_item.photo, "drawable",
                context.getPackageName());
        dish_photo.setImageResource(resourceId);
    }
}
