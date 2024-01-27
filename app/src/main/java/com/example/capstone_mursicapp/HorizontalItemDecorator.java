package com.example.capstone_mursicapp;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalItemDecorator extends RecyclerView.ItemDecoration {
    private final int horizontalSpaceHeight;

    public HorizontalItemDecorator(int horizontalSpaceHeight){
        this.horizontalSpaceHeight = horizontalSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.right = horizontalSpaceHeight;
    }
}
