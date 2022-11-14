package com.sdeepakkumar.fammap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.List;

public class AdapterString extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mlistRecentSearches;
    private int mLayout;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int positon);
    }

    public AdapterString(Context mContext, List<String> mlistRecentSearches, int mLayout, OnItemClickListener mOnItemClickListener) {
        this.mlistRecentSearches = mlistRecentSearches;
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new ViewHolderString(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder mainHolder, @SuppressLint("RecyclerView") int position) {
        final ViewHolderString holder = (ViewHolderString) mainHolder;

        holder.mStringText.setText(mlistRecentSearches.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlistRecentSearches.size();
    }
}