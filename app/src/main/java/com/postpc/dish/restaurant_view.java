package com.postpc.dish;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;

public class restaurant_view extends RecyclerView.ViewHolder {
    private final TextView name;
    private final TextView city;
    private final TextView category;
    private final ImageView image;
    private final AppCompatButton custom_menu_button;

    public restaurant_view(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.restaurantNameTextView);
        city = itemView.findViewById(R.id.cityTextView);
        category = itemView.findViewById(R.id.categoryTextView);
        image = itemView.findViewById(R.id.restaurantImageView);
        custom_menu_button = itemView.findViewById(R.id.custom_menu_button);
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

    public ImageView get_restaurant_image() { return image; }

    public AppCompatButton get_custom_menu_button() { return custom_menu_button; }

    public void bind(Restaurant restaurant, Context context) {
        name.setText(restaurant.name);
        city.setText(restaurant.city);
        category.setText(restaurant.category);
        int resourceId = context.getResources().getIdentifier(restaurant.code, "drawable",
                context.getPackageName());
        image.setImageResource(resourceId);

//        image.setImageResource(context.getResources().getDrawable(resourceId));
//        image.setImageURI(Uri.fromFile(new File("src/main/res/drawable/" + restaurant.code + ".png"))
    }
}
