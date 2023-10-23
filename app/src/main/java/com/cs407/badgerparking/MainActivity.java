package com.cs407.badgerparking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    BottomNavigationView bottomNavigationView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creates a context global for intents outside of onCreate
        context = this;

        instantiateLocationServices();
        instantiateMenuBar();
    }

    //I took the code from project 4's location manager, i just wanted to move it out of onCreate - nick
    public void instantiateLocationServices(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
//                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle){

            }
            @Override
            public void onProviderEnabled(String s){

            }

            @Override
            public void onProviderDisabled(String s){

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        }
        else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location!=null){
//                    updateLocationInfo(location);
                }
            }
        }
    }

    //project 4 location code
    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //project 4 location code
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void instantiateMenuBar(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mHome){
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.mMap){
                    Intent intent = new Intent(context, MapActivity.class);
                    startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSearch){
                    Intent intent = new Intent(context, SearchActivity.class);
                    startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSettings){
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }




}