package com.sdeepakkumar.fammap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ActivitySplash extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private Permissions mPermissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash);

        mPermissions = new Permissions();

        Constants.sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);

        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (mPermissions.isLocationOk(ActivitySplash.this)) {
                    startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                }else {
                   requestPermissions();
                }
            }
        }.start();

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
                startActivity(new Intent(ActivitySplash.this, MainActivity.class));
            }
            else {
                Toast.makeText(ActivitySplash.this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(ActivitySplash.this, MainActivity.class));
        } else{
            ActivityCompat.requestPermissions(ActivitySplash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (countDownTimer != null){
            countDownTimer.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countDownTimer != null){
            countDownTimer.start();
        }
    }
}
