package com.sdeepakkumar.fammap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @GET
    Call<GooglePlaceResponse> getNearByPlaces(@Url String url);

    /*@GET
    Call<DirectionResponseModel> getDirection(@Url String url);*/
}
