package com.sdeepakkumar.fammap;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sdeepakkumar.fammap.databinding.ItemGooglePlaceBinding;

public class ViewHolderGooglePlace extends RecyclerView.ViewHolder {

    public ImageView mGooglePlaceIcon;
    public TextView mGooglePlaceName, mGooglePlaceAddress;
    public MaterialTextView mGooglePlaceTotalRating;
    public ImageView mNavigateLocationButton;
    public CardView mCardView;

    public ViewHolderGooglePlace(View itemView) {
        super(itemView);
        assignViews();
    }

    private void assignViews() {
        mGooglePlaceIcon = (ImageView) findViewById(R.id.googlePlaceIcon);
        mGooglePlaceName = (TextView) findViewById(R.id.googlePlaceName);
        mGooglePlaceAddress = (TextView) findViewById(R.id.googlePlaceAddress);
        mGooglePlaceTotalRating = (MaterialTextView) findViewById(R.id.googlePlaceTotalRating);
        mCardView = (CardView) findViewById(R.id.cardView);
    }

    private View findViewById(final int id) {
        return itemView.findViewById(id);
    }
}