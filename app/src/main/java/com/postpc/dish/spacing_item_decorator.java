package com.postpc.dish;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class spacing_item_decorator extends RecyclerView.ItemDecoration {

    private final int vertical_space_height;

    public spacing_item_decorator(int vertical_space_height) {
        this.vertical_space_height = vertical_space_height;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = vertical_space_height;
        super.getItemOffsets(outRect, view, parent, state);
    }
}
