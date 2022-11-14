package com.sdeepakkumar.fammap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GooglePlaceResponse {
    @SerializedName("results")
    @Expose
    private List<GooglePlace> googlePlaceModelList;

    @SerializedName("error_message")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<GooglePlace> getGooglePlaceModelList() {
        return googlePlaceModelList;
    }

    public void setGooglePlaceModelList(List<GooglePlace> googlePlaceModelList) {
        this.googlePlaceModelList = googlePlaceModelList;
    }
}
