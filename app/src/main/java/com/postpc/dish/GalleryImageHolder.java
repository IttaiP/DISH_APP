package com.postpc.dish;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class GalleryImageHolder extends RecyclerView.ViewHolder {
    private final ImageView photo_of_dish;

    public GalleryImageHolder(@NonNull View itemView) {
        super(itemView);
        photo_of_dish = itemView.findViewById(R.id.photo_of_dish);
    }

    public ImageView getPhoto_of_dish() { return photo_of_dish; }

    public void bind(String uri, Context context) {
        Log.e("IM HERE", "loading");
        Picasso.with(context).load(uri).placeholder(R.drawable.loading)
                .error(R.drawable.loading).into(photo_of_dish);
    }


}
