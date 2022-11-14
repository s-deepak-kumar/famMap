package com.sdeepakkumar.fammap;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderMember extends RecyclerView.ViewHolder {

    public CircleImageView mMemberPic;
    public TextView mMemberName, mRelation;

    public ViewHolderMember(View itemView) {
        super(itemView);
        assignViews();
    }

    private void assignViews() {
        mMemberPic = (CircleImageView) findViewById(R.id.memberPic);
        mMemberName = (TextView) findViewById(R.id.memberName);
        mRelation = (TextView) findViewById(R.id.relation);

    }

    private View findViewById(final int id) {
        return itemView.findViewById(id);
    }
}
