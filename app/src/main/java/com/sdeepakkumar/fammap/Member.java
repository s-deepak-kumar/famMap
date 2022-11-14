package com.sdeepakkumar.fammap;

public class Member {
    private String mUid, mName, mEmail, mProfileUrl, mRelation, mAddedBy;
    private boolean mPendingConfirmation;

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getProfileUrl() {
        return mProfileUrl;
    }

    public void setProfileUrl(String mProfileUrl) {
        this.mProfileUrl = mProfileUrl;
    }

    public String getRelation() {
        return mRelation;
    }

    public void setRelation(String mRelation) {
        this.mRelation = mRelation;
    }

    public String getAddedBy() {
        return mAddedBy;
    }

    public void setAddedBy(String mAddedBy) {
        this.mAddedBy = mAddedBy;
    }

    public boolean isPendingConfirmation() {
        return mPendingConfirmation;
    }

    public void setPendingConfirmation(boolean mPendingConfirmation) {
        this.mPendingConfirmation = mPendingConfirmation;
    }
}
