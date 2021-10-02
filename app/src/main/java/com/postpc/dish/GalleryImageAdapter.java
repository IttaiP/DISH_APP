package com.postpc.dish;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GalleryImageAdapter  extends RecyclerView.Adapter<GalleryImageHolder> {
    private Context context;
    private List<String> urisOfPhotos = new ArrayList<>();

    @NonNull
    @Override
    public GalleryImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_dish_photo, parent, false);
        return new GalleryImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryImageHolder holder, int position) {
        holder.bind(urisOfPhotos.get(position), context);
    }

    @Override
    public int getItemCount() {
        return urisOfPhotos.size();
    }

    public void addUri(String uri) {
        this.urisOfPhotos.add(uri);
    }
}
