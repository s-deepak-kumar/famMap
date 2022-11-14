package com.sdeepakkumar.fammap;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderString extends RecyclerView.ViewHolder {

    public TextView mStringText;

    public ViewHolderString(View itemView) {
        super(itemView);
        assignViews();
    }

    private void assignViews() {
        mStringText = (TextView) findViewById(R.id.stringText);
    }

    private View findViewById(final int id) {
        return itemView.findViewById(id);
    }
}
