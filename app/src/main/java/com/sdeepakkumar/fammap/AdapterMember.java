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

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

public class AdapterMember extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Member> mlistMember;

    public AdapterMember(Context mContext, List<Member> mlistMember) {
        this.mlistMember = mlistMember;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHolderMember(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder mainHolder, @SuppressLint("RecyclerView") final int position) {
        final ViewHolderMember holder = (ViewHolderMember) mainHolder;

        Glide.with(mContext)
                .load(mlistMember.get(position).getProfileUrl())
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .priority( Priority.HIGH )
                .into(holder.mMemberPic);

        holder.mMemberName.setText(mlistMember.get(position).getName());
        holder.mRelation.setText(mlistMember.get(position).getRelation().equals("")?"Not specified":mlistMember.get(position).getRelation());
    }

    @Override
    public int getItemCount() {
        return mlistMember.size();
    }
}