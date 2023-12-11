package com.cs407.badgerparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        instantiateMenuBar(this);

        dbHelper = new DatabaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapActivity);
        mapFragment.getMapAsync(this);

    }

    /*
     * ==================================================
     * <------------------- MENU BAR ------------------->
     * ==================================================
     */

    public void instantiateMenuBar(Context context) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        MenuBarManager manager = new MenuBarManager(bottomNavigationView);
        manager.instantiate(context, 'M');
    }






    private ClusterManager<MyClusterItem> mClusterManager;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.0731, -89.4012), 12)); // Adjust zoom as needed
        // Initialize the ClusterManager with the context and the map.
        mClusterManager = new ClusterManager<>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {
        // Retrieve locations from the database. Assuming you have a method that returns a List<MyClusterItem>.
        List<MyClusterItem> items = dbHelper.getAllClusterItems();

        // Add each item to the cluster manager.
        for (MyClusterItem item : items) {
            mClusterManager.addItem(item);
        }

        // Force a re-cluster. You might want to call this outside the loop for efficiency.
        mClusterManager.cluster();
    }




    @Override
    public void onCameraIdle() {
        mClusterManager.cluster();
    }

}