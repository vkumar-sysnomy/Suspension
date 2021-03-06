package com.farthestgate.suspensions.android.component;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.farthestgate.suspensions.android.R;

public class MarginDecoration extends RecyclerView.ItemDecoration {
  private int margin;

  public MarginDecoration(Context context) {
    margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
  }

  @Override
  public void getItemOffsets(
          Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.set(margin, margin, margin, margin);
  }
}