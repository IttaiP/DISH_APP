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

public class wifiRestaurantsHolder extends RecyclerView.ViewHolder {
    private final TextView name;
    private final TextView category;
    private final ImageView image;

    public wifiRestaurantsHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.restaurant_name);
        category = itemView.findViewById(R.id.restaurant_category);
        image = itemView.findViewById(R.id.restaurant_image);
    }

    public TextView get_restaurant_name() {
        return name;
    }

    public TextView get_restaurant_category() {
        return category;
    }

    public ImageView get_restaurant_image() { return image; }

    public void bind(Restaurant restaurant, Context context) {
        name.setText(restaurant.name);
        category.setText(restaurant.category);
        int resourceId = context.getResources().getIdentifier(restaurant.code, "drawable",
                context.getPackageName());
        image.setImageResource(resourceId);
    }
}
