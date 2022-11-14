package com.sdeepakkumar.fammap;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
    public static final String TAG = "FamMap";
    public static final int STORAGE_REQUEST_CODE = 101;
    public static final int LOCATION_REQUEST_CODE = 201;

    public static SharedPreferences sharedPreferences;
    public static final String pref_name = "pref_name";
    public static final String uid ="uid";
    public static final String name = "name";
    public static final String email = "email";
    public static final String lat = "lat";
    public static final String lng = "lng";
    public static final String last_status = "last_status";
    public static final String profile_pic_url_local = "profile_pic_url_local";

    public static final String islogin = "is_login";

    public static void resetSharedPreferencesData(){
        SharedPreferences.Editor editor = Constants.sharedPreferences.edit();
        editor.putString(Constants.profile_pic_url_local, "");
        editor.putString(Constants.uid, "");
        editor.putString(Constants.name, "");
        editor.putString(Constants.email, "");
        editor.putString(Constants.lat, "");
        editor.putString(Constants.lng, "");
        editor.putString(Constants.lng, "");
        editor.putString(Constants.last_status, "");
        editor.putBoolean(Constants.islogin, false);
        editor.apply();
    }

    public static void setSharedPreferencesData(String mUid, String mName, String mEmail, String mProfilePicUrl, String mLat,
                                                String mLng, String mLastStatus){
        SharedPreferences.Editor editor = Constants.sharedPreferences.edit();
        editor.putString(Constants.profile_pic_url_local, mProfilePicUrl);
        editor.putString(Constants.uid, mUid);
        editor.putString(Constants.name, mName);
        editor.putString(Constants.email, mEmail);
        editor.putString(Constants.lat, mLat);
        editor.putString(Constants.lng, mLng);
        editor.putString(Constants.last_status, mLastStatus);
        editor.putBoolean(Constants.islogin, true);
        editor.apply();
    }

    public static final ArrayList<Place> places = new ArrayList<>(
            Arrays.asList(
                    new Place(1, R.drawable.ic_restaurant, "Restaurant", "restaurant"),
                    new Place(2, R.drawable.ic_atm, "ATM", "atm"),
                    new Place(3, R.drawable.ic_gas_station, "Gas", "gas_station"),
                    new Place(4, R.drawable.ic_shopping_cart, "Groceries", "supermarket"),
                    new Place(5, R.drawable.ic_hotel, "Hotels", "hotel"),
                    new Place(6, R.drawable.ic_pharmacy, "Pharmacies", "pharmacy"),
                    new Place(7, R.drawable.ic_hospital, "Hospitals & Clinics", "hospital"),
                    new Place(8, R.drawable.ic_saloon, "Beauty Salons", "beauty_salon"),
                    new Place(9, R.drawable.ic_car_wash, "Car Wash", "car_wash")

            )
    );
}
