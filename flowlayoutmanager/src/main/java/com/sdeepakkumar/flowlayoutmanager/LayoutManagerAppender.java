package com.sdeepakkumar.flowlayoutmanager;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anton Dudakov (dude.bw@gmail.com) on 04/10/16.
 */

public class LayoutManagerAppender {

    View mView;
    RecyclerView.LayoutManager mLayoutManager;
    Rect mRect;
    Alignment alignment;

    public LayoutManagerAppender(View view, RecyclerView.LayoutManager layoutManager, Rect rect, Alignment alignment) {
        mView = view;
        mLayoutManager = layoutManager;
        this.mRect = new Rect(rect);
        this.alignment = alignment;
    }

    public void layout(int addition) {
        if (alignment == Alignment.CENTER)
            mLayoutManager.layoutDecorated(mView, mRect.left + addition, mRect.top, mRect.right + addition, mRect.bottom);
        else
            mLayoutManager.layoutDecorated(mView, mRect.left, mRect.top, mRect.right, mRect.bottom);

    }
}
