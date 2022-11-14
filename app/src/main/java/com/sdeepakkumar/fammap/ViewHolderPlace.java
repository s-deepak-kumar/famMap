package com.sdeepakkumar.fammap;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderPlace extends RecyclerView.ViewHolder {

    public TextView mPlaceType;

    public ViewHolderPlace(View itemView) {
        super(itemView);
        assignViews();
    }

    private void assignViews() {
        mPlaceType = (TextView) findViewById(R.id.placeType);
    }

    private View findViewById(final int id) {
        return itemView.findViewById(id);
    }
}
