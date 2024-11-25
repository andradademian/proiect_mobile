package com.example.melodify_app.Model_Auxiliare;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    public SpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // Apply spacing only to the bottom of each item
        outRect.bottom = verticalSpaceHeight;

        // Optionally apply spacing to other sides
        // outRect.top = verticalSpaceHeight;
        // outRect.left = verticalSpaceHeight;
        // outRect.right = verticalSpaceHeight;
    }
}
