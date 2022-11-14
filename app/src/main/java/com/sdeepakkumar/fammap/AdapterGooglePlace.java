package com.sdeepakkumar.fammap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.sdeepakkumar.fammap.databinding.ItemGooglePlaceBinding;

import java.util.List;

public class AdapterGooglePlace extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GooglePlace> mGooglePlaces;
    private NearLocationListener mNearLocationListener;
    private Context mContext;

    public AdapterGooglePlace(Context mContext, NearLocationListener mNearLocationListener) {
        this.mContext = mContext;
        this.mNearLocationListener = mNearLocationListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_google_place, parent, false);
        return new ViewHolderGooglePlace(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderGooglePlace) {
            populateItem((ViewHolderGooglePlace) holder, position);
        }

    }

    @SuppressLint("SetTextI18n")
    private void populateItem(final ViewHolderGooglePlace holder, final int position) {

        String photoString = "";
        if (mGooglePlaces.get(position).getPhotos() != null){
            if (mGooglePlaces.get(position).getPhotos().size() > 0){
                photoString = "https://maps.googleapis.com/maps/api/place/photo" +
                        "?maxwidth=200" +
                        "&photo_reference=" + mGooglePlaces.get(position).getPhotos().get(0).getPhotoReference() +
                        "&key="+mContext.getString(R.string.google_maps_key);
            }
        }

        Glide.with(mContext)
                .load(photoString)
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .priority( Priority.HIGH )
                .into(holder.mGooglePlaceIcon);

        if (mGooglePlaces.get(position).getRating() != null){
            holder.mGooglePlaceTotalRating.setText(Double.toString(mGooglePlaces.get(position).getRating()));
        }
        holder.mGooglePlaceName.setText(mGooglePlaces.get(position).getName());
        holder.mGooglePlaceAddress.setText(mGooglePlaces.get(position).getVicinity());

    }

    @Override
    public int getItemCount() {
        if (mGooglePlaces != null)
            return mGooglePlaces.size();
        else
            return 0;
    }

    public void setGooglePlaceModels(List<GooglePlace> googlePlaces) {
        this.mGooglePlaces = googlePlaces;
        notifyDataSetChanged();
    }
}