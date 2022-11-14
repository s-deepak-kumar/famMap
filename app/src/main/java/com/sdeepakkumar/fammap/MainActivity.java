package com.sdeepakkumar.fammap;

import static com.sdeepakkumar.fammap.Constants.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.sdeepakkumar.fammap.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, NearLocationListener {

    private ActivityMainBinding binding;
    private GoogleMap mGoogleMap;
    private Permissions mPermissions;
    private boolean isLocationPermissionOk;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private Marker currentMarker;
    private int radius = 2000;
    private RetrofitAPI retrofitAPI;
    private List<GooglePlace> googlePlaceModelList;
    private Place selectedPlace;
    private AdapterGooglePlace googlePlaceAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean isSearched = false;

    //Google
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;

    private Dialog dDialog;
    private LinearLayout mGoogleContainer;
    private ProgressBar mProgressBarGoogle;

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();

        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();

        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        binding.currentLocationButton.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.green));
        binding.trackFamilyButton.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.gray));

        mPermissions = new Permissions();
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);
        googlePlaceModelList = new ArrayList<>();

        for (Place place : Constants.places) {

            Chip chip = new Chip(MainActivity.this);
            chip.setText(place.getName());
            chip.setId(place.getId());
            //chip.setPadding(50, 8, 50, 8);
            chip.setChipStartPadding(30);
            chip.setChipEndPadding(30);
            chip.setTextColor(getResources().getColor(R.color.white));
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(MainActivity.this, R.color.green));
            chip.setChipIcon(ResourcesCompat.getDrawable(getResources(), place.getDrawableId(), null));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);

            binding.placesGroup.addView(chip);

        }

        Glide.with(MainActivity.this)
                .load(Constants.sharedPreferences.getString(Constants.profile_pic_url_local, "User"))
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .priority( Priority.HIGH )
                .into(binding.userPic);

        binding.profileCardLayout.setOnClickListener(view -> {
            if (!Constants.sharedPreferences.getBoolean(Constants.islogin, false)) {
                openSignInDialog();
            }else {
                openProfileDialog();
            }
        });

        binding.searchBarLayout.setOnClickListener(view -> {
            startActivityForResult(new Intent(MainActivity.this, ActivitySearch.class), 101);
        });

        binding.currentLocationButton.setOnClickListener(view -> {
            binding.currentLocationButton.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.green));
            binding.trackFamilyButton.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.gray));
            if (fusedLocationProviderClient != null) {

            startLocationUpdates();
            if (currentMarker != null) {
                currentMarker.remove();
            }
        }
        });

        binding.trackFamilyButton.setOnClickListener(view -> {
            binding.currentLocationButton.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.gray));
            binding.trackFamilyButton.setImageTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.green));
            if (!Constants.sharedPreferences.getBoolean(Constants.islogin, false)){
                openSignInDialog();
            }else {
                trackFamily();
            }
        });


        binding.placesGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                if (checkedId != -1) {
                    Place placeModel = Constants.places.get(checkedId - 1);
                    //binding.edtPlaceName.setText(placeModel.getName());
                    selectedPlace = placeModel;
                    getPlaces(placeModel.getPlaceType());
                }
            }
        });

        // AFter view created

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        setUpRecyclerView();
    }

    private void openSignInDialog(){
        dDialog = new Dialog(MainActivity.this, R.style.FullScreenDialogStyle);
        dDialog.setContentView(R.layout.dialog_login);

        mGoogleContainer = dDialog.findViewById(R.id.gmailContainer);

        mProgressBarGoogle = dDialog.findViewById(R.id.progressBarGmail);

        dDialog.setCancelable(true);
        dDialog.setCanceledOnTouchOutside(true);

        dDialog.findViewById(R.id.cancelButton).setOnClickListener(view -> {
            dDialog.cancel();
        });

        mGoogleContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleContainer.setVisibility(View.GONE);
                mProgressBarGoogle.setVisibility(View.VISIBLE);

                if (!Constants.sharedPreferences.getBoolean(Constants.islogin, false)) {
                    signInGoogle();
                }
            }
        });

        dDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dDialog.show();
    }

    private void openProfileDialog(){
        dDialog = new Dialog(MainActivity.this, R.style.FullScreenDialogStyle);
        dDialog.setContentView(R.layout.dialog_profile);

        TextView mWelcomeText = dDialog.findViewById(R.id.welcomeText);
        CircleImageView mUserPic = dDialog.findViewById(R.id.userPic);
        LinearLayout mFamilyMemberContainer = dDialog.findViewById(R.id.familyMemberContainer);

        mFamilyMemberContainer.setOnClickListener(view -> {
            dDialog.cancel();
            startActivity(new Intent(MainActivity.this, ActivityFamily.class));
        });

        mWelcomeText.setText("Hello,\n" + Constants.sharedPreferences.getString(Constants.name, "User"));

        Glide.with(MainActivity.this)
                .load(Constants.sharedPreferences.getString(Constants.profile_pic_url_local, "User"))
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .priority( Priority.HIGH )
                .into(mUserPic);

        dDialog.setCancelable(true);
        dDialog.setCanceledOnTouchOutside(true);

        dDialog.findViewById(R.id.cancelButton).setOnClickListener(view -> {
            dDialog.cancel();
        });

        dDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dDialog.show();
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        int markerTag = (int) marker.getTag();
        Log.d("TAG", "onMarkerClick: " + markerTag);

        binding.placesRecyclerView.scrollToPosition(markerTag);
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;


        if (mPermissions.isLocationOk(MainActivity.this)) {
            isLocationPermissionOk = true;

            if (!isSearched){
                setUpGoogleMap();
            }
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Location Permission")
                    .setMessage("Near me required location permission to show you near by places")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions();
                        }
                    })
                    .create().show();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Showing the toast message
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
            else {
                Toast.makeText(MainActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_REQUEST_CODE);
        }
    }

    private void setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        //mGoogleMap.setMyLocationEnabled(true);
        //mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.setOnMarkerClickListener(this);

        /*try {
            // Customise map styling via JSON file
            boolean success = mGoogleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( this, R.raw.map_silver));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }*/

        setUpLocationUpdate();
    }

    private void setUpLocationUpdate() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d("TAG", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                    }
                }
                super.onLocationResult(locationResult);
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        startLocationUpdates();


    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Location updated started", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        getCurrentLocation();
    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionOk = false;
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                /*infoWindowAdapter = null;
                infoWindowAdapter = new InfoWindowAdapter(currentLocation, MainActivity.this);
                mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);*/
                moveCameraToLocation(location);

            }
        });
    }

    private void moveCameraToLocation(Location location) {

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new
                LatLng(location.getLatitude(), location.getLongitude()), 16);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location_pointer))
                .snippet(/*firebaseAuth.getCurrentUser().getDisplayName()*/"Current User");

        if (currentMarker != null) {
            currentMarker.remove();
        }

        currentMarker = mGoogleMap.addMarker(markerOptions);
        currentMarker.setTag(703);
        mGoogleMap.animateCamera(cameraUpdate);

    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d("TAG", "stopLocationUpdate: Location Update stop");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (fusedLocationProviderClient != null)
            stopLocationUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (fusedLocationProviderClient != null) {

            startLocationUpdates();
            if (currentMarker != null) {
                currentMarker.remove();
            }
        }*/
    }

    private void getPlaces(String placeName) {

        if (isLocationPermissionOk) {


            //loadingDialog.startLoading();
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                    + "&radius=" + radius + "&type=" + placeName + "&key=" +
                    getResources().getString(R.string.google_maps_key);

            if (currentLocation != null) {


                retrofitAPI.getNearByPlaces(url).enqueue(new Callback<GooglePlaceResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GooglePlaceResponse> call, @NonNull Response<GooglePlaceResponse> response) {
                        Gson gson = new Gson();
                        String res = gson.toJson(response.body());
                        Log.d("TAG", "onResponse: " + res);
                        if (response.errorBody() == null) {
                            if (response.body() != null) {
                                if (response.body().getGooglePlaceModelList() != null && response.body().getGooglePlaceModelList().size() > 0) {

                                    googlePlaceModelList.clear();
                                    mGoogleMap.clear();
                                    for (int i = 0; i < response.body().getGooglePlaceModelList().size(); i++) {
                                        googlePlaceModelList.add(response.body().getGooglePlaceModelList().get(i));
                                        addMarker(response.body().getGooglePlaceModelList().get(i), i);
                                    }

                                    googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);

                                } else if (response.body().getError() != null) {
                                    Snackbar.make(binding.getRoot(),
                                            response.body().getError(),
                                            Snackbar.LENGTH_LONG).show();
                                } else {

                                    mGoogleMap.clear();
                                    googlePlaceModelList.clear();
                                    googlePlaceAdapter.setGooglePlaceModels(googlePlaceModelList);
                                    radius += 1000;
                                    Log.d("TAG", "onResponse: " + radius);
                                    getPlaces(placeName);

                                }
                            }

                        } else {
                            Log.d("TAG", "onResponse: " + response.errorBody());
                            Toast.makeText(MainActivity.this, "Error : " + response.errorBody(), Toast.LENGTH_SHORT).show();
                        }

                        //loadingDialog.stopLoading();

                    }

                    @Override
                    public void onFailure(Call<GooglePlaceResponse> call, Throwable t) {

                        Log.d("TAG", "onFailure: " + t);
                        //loadingDialog.stopLoading();

                    }
                });
            }
        }

    }

    private void addMarker(GooglePlace googlePlace, int position) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googlePlace.getGeometry().getLocation().getLat(),
                        googlePlace.getGeometry().getLocation().getLng()))
                .title(googlePlace.getName())
                .snippet(googlePlace.getVicinity());
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);
    }

    private void trackFamily(){
        db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Family")
                .orderBy("added_by", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                DocumentReference docRef = db.collection("Users").document(document.getData().get("uid").toString());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document1 = task.getResult();
                                            if (document1.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                                                MarkerOptions markerOptions = new MarkerOptions()
                                                        .position(new LatLng(Double.parseDouble(document1.getData().get("lat").toString()),
                                                                Double.parseDouble(document1.getData().get("lng").toString())))
                                                        .title(document1.getData().get("name").toString())
                                                        .icon(getCustomIcon());
                                                markerOptions.snippet(document1.getData().get("name").toString());
                                                mGoogleMap.addMarker(markerOptions).setTag(5);
                                                mGoogleMap.addPolyline(new PolylineOptions().add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(Double.parseDouble(document1.getData().get("lat").toString()),
                                                        Double.parseDouble(document1.getData().get("lng").toString()))).width(10).color(Color.GREEN));
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("On Failure:", e.toString());
                    }
                });
    }

    private BitmapDescriptor getCustomIcon() {

        Drawable background = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_place_pointer);
        //background.setTint(getResources().getColor(R.color.green));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setUpRecyclerView() {

        binding.placesRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.placesRecyclerView.setHasFixedSize(false);
        googlePlaceAdapter = new AdapterGooglePlace(MainActivity.this, this);
        binding.placesRecyclerView.setAdapter(googlePlaceAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(binding.placesRecyclerView);

        binding.placesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position > -1) {
                    GooglePlace googlePlaceModel = googlePlaceModelList.get(position);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                            googlePlaceModel.getGeometry().getLocation().getLng()), 20));
                }
            }
        });

    }

    @Override
    public void onSaveClick(GooglePlace googlePlaceModel) {
        /*if (userSavedLocationId.contains(googlePlaceModel.getPlaceId())) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Remove Place")
                    .setMessage("Are you sure to remove this place?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removePlace(googlePlaceModel);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create().show();
        } else {
            loadingDialog.startLoading();

            locationReference.child(googlePlaceModel.getPlaceId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {

                        SavedPlaceModel savedPlaceModel = new SavedPlaceModel(googlePlaceModel.getName(), googlePlaceModel.getVicinity(),
                                googlePlaceModel.getPlaceId(), googlePlaceModel.getRating(),
                                googlePlaceModel.getUserRatingsTotal(),
                                googlePlaceModel.getGeometry().getLocation().getLat(),
                                googlePlaceModel.getGeometry().getLocation().getLng());

                        saveLocation(savedPlaceModel);
                    }

                    saveUserLocation(googlePlaceModel.getPlaceId());

                    int index = googlePlaceModelList.indexOf(googlePlaceModel);
                    googlePlaceModelList.get(index).setSaved(true);
                    googlePlaceAdapter.notifyDataSetChanged();
                    loadingDialog.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }*/
    }

    /*private void removePlace(GooglePlaceModel googlePlaceModel) {

        userSavedLocationId.remove(googlePlaceModel.getPlaceId());
        int index = googlePlaceModelList.indexOf(googlePlaceModel);
        googlePlaceModelList.get(index).setSaved(false);
        googlePlaceAdapter.notifyDataSetChanged();

        Snackbar.make(binding.getRoot(), "Place removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userSavedLocationId.add(googlePlaceModel.getPlaceId());
                        googlePlaceModelList.get(index).setSaved(true);
                        googlePlaceAdapter.notifyDataSetChanged();

                    }
                })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        userLocationReference.setValue(userSavedLocationId);
                    }
                }).show();

    }

    private void saveUserLocation(String placeId) {

        userSavedLocationId.add(placeId);
        userLocationReference.setValue(userSavedLocationId);
        Snackbar.make(binding.getRoot(), "Place Saved", Snackbar.LENGTH_LONG).show();
    }

    private void saveLocation(SavedPlaceModel savedPlaceModel) {
        locationReference.child(savedPlaceModel.getPlaceId()).setValue(savedPlaceModel);
    }*/

    @Override
    public void onDirectionClick(GooglePlace googlePlace) {

        String placeId = googlePlace.getPlaceId();
        Double lat = googlePlace.getGeometry().getLocation().getLat();
        Double lng = googlePlace.getGeometry().getLocation().getLng();

        /*Intent intent = new Intent(MainActivity.this, DirectionActivity.class);
        intent.putExtra("placeId", placeId);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);

        startActivity(intent);*/

    }

    /*private void getUserSavedLocations() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("Saved Locations");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String placeId = ds.getValue(String.class);
                        userSavedLocationId.add(placeId);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 101 && data != null) {
                isSearched = true;
                //searchedLocation.postValue(data.getStringExtra("searchedLocation"));
                String searchedLocation = data.getStringExtra("searchedLocation");
                //Toast.makeText(this, ""+data.getStringExtra("searchedLocation"), Toast.LENGTH_SHORT).show();
                List<Address> addressList = null;
                if (searchedLocation != null || !searchedLocation.equalsIgnoreCase("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(searchedLocation, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Searched Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_pin)));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }

                mapFragment.getMapAsync(this);
            }
        }else if (resultCode == 909) {
            if (requestCode == 101 && data != null) {
                //isSearched = true;
                //searchedLocation.postValue(data.getStringExtra("searchedLocation"));
                String placeType = data.getStringExtra("placeType");
                //Toast.makeText(this, ""+data.getStringExtra("searchedLocation"), Toast.LENGTH_SHORT).show();
                getPlaces(placeType);
            }
        }

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            mGoogleContainer.setVisibility(View.VISIBLE);
                            mGoogleContainer.setEnabled(true);
                            mProgressBarGoogle.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateUI(final FirebaseUser user) {
        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Toast.makeText(LoginActivity.this, "Already Inserted", Toast.LENGTH_SHORT).show();
                        DocumentReference documentReference = db.collection("Users").document(user.getUid());
                        Map<String,Object> userUpdateMap = new HashMap<>();
                        userUpdateMap.put("lat", currentLocation.getLatitude());
                        userUpdateMap.put("lng", currentLocation.getLongitude());
                        userUpdateMap.put("updated_at", FieldValue.serverTimestamp());

                        documentReference.update(userUpdateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                mGoogleContainer.setVisibility(View.VISIBLE);
                                mGoogleContainer.setEnabled(true);
                                mProgressBarGoogle.setVisibility(View.GONE);

                                if (dDialog != null){
                                    dDialog.cancel();
                                }

                                Constants.setSharedPreferencesData(document.getData().get("uid").toString(), document.getData().get("name").toString(),
                                        document.getData().get("email").toString(), document.getData().get("profile_pic_url").toString(),
                                        Double.toString(currentLocation.getLatitude()), Double.toString(currentLocation.getLongitude()), document.getData().get("last_status").toString());
                                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                    } else {
                        //Log.d(TAG, "No such document");
                        Map<String,Object> userMap = new HashMap<>();
                        userMap.put("uid", user.getUid());
                        userMap.put("name", user.getDisplayName());
                        userMap.put("email", user.getEmail());
                        userMap.put("profile_pic_url", user.getPhotoUrl().toString());
                        userMap.put("added_at", FieldValue.serverTimestamp());
                        userMap.put("updated_at", "");
                        userMap.put("lat", currentLocation.getLatitude());
                        userMap.put("lng", currentLocation.getLongitude());
                        userMap.put("last_status", "Hey, I'm using famMap!");

                        db.collection("Users").document(user.getUid()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                mGoogleContainer.setVisibility(View.VISIBLE);
                                mGoogleContainer.setEnabled(true);
                                mProgressBarGoogle.setVisibility(View.GONE);

                                if (dDialog != null){
                                    dDialog.cancel();
                                }

                                Constants.setSharedPreferencesData(user.getUid(), user.getDisplayName(), user.getEmail(),
                                        user.getPhotoUrl().toString(), Double.toString(currentLocation.getLatitude()), Double.toString(currentLocation.getLongitude()),
                                        "Hey, I'm using famMap!");
                                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}