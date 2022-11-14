package com.sdeepakkumar.fammap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import java.util.Random;

public class AdapterPlace extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Place> mlistPlace;

    public AdapterPlace(Context mContext, List<Place> mlistPlace) {
        this.mlistPlace = mlistPlace;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolderPlace(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder mainHolder, @SuppressLint("RecyclerView") final int position) {
        final ViewHolderPlace holder = (ViewHolderPlace) mainHolder;

        String[] arrayColorsName = {"random_color_1", "random_color_2", "random_color_3", "random_color_4", "random_color_5",
                "random_color_6", "random_color_7", "random_color_8", "random_color_9", "random_color_10", "random_color_11",
                "random_color_12", "random_color_13", "random_color_14"};
        String mRandomColorName = arrayColorsName[new Random().nextInt(arrayColorsName.length)];

        holder.mPlaceType.setText(capitalize(mlistPlace.get(position).getName()));
        holder.mPlaceType.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(mlistPlace.get(position).getDrawableId()), null, null, null);
        holder.mPlaceType.setTextColor(ContextCompat.getColorStateList(mContext, getResId(mRandomColorName, R.color.class)));
        holder.mPlaceType.setBackgroundTintList(ContextCompat.getColorStateList(mContext, getResId(mRandomColorName, R.color.class)));
        setTextViewDrawableColor(holder.mPlaceType, getResId(mRandomColorName, R.color.class));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra("placeType", mlistPlace.get(position).getPlaceType());
                ((Activity) mContext).setResult(909, mIntent);
                ((Activity) mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlistPlace.size();
    }

    public static String capitalize (String givenString) {
        String Separateur = " ,.-;";
        StringBuilder sb = new StringBuilder();
        boolean ToCap = true;
        for (int i = 0; i < givenString.length(); i++) {
            if (ToCap)
                sb.append(Character.toUpperCase(givenString.charAt(i)));
            else
                sb.append(Character.toLowerCase(givenString.charAt(i)));

            ToCap = Separateur.indexOf(givenString.charAt(i)) >= 0;
        }
        return sb.toString().trim();
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}