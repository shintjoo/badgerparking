package com.cs407.badgerparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        sharedPreferences = getSharedPreferences("com.cs407.badgerparking", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("warnings_displayed", 0)
                .apply();

        dbHelper = new DatabaseHelper(this); // Instantiating restriction database
        try {
            dbHelper.copyDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        instantiateMenuBar(this);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        ListView locationListView;
        ArrayAdapter<String> locationAdapter;
        List<String> locationsList = new ArrayList<>(); // Assuming you have a list to store location names

        locationListView = findViewById(R.id.StreetsList);
        locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationsList);
        locationListView.setAdapter(locationAdapter);

        Places.initialize(getApplicationContext(), "AIzaSyAezFcZEaYRLpRHRLxar7ycWIeYFmShiWw");

        // Initialize AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify types of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the selected place
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Get the selected place's name and LatLng
                String placeName = place.getName();
                LatLng latLng = place.getLatLng();
                double latitude = 0;
                double longitude = 0;
                if (latLng != null) {
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                }

                double range = 0.00075;



                if (placeName != null) {
                    LatLng sw = new LatLng(latitude - range, longitude - range);
                    LatLng ne = new LatLng(latitude + range, longitude + range);

                    LatLngBounds bounds = new LatLngBounds(sw, ne);
                    List<LatLng> coords = dbHelper.getLocationsWithinBounds(bounds);
                    locationsList.clear();
                    // Add the selected place's name to the list
                    for(int i = 0; i < coords.size(); i++) {
                        double lat = coords.get(i).latitude;
                        double lng = coords.get(i).longitude;
                        List<Address> address = null;
                        try {
                            address = geocoder.getFromLocation(lat, lng,1);
                        } catch (IOException e) {

                        }
                        String restriction = dbHelper.getParkingRestrictionExact(lat, lng);
                        if(restriction != null) {
                            locationsList.add(0, address.get(0).getAddressLine(0) + ":\n" + restriction);
                        }
                    }
                    // Notify the adapter that the data has changed
                    locationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle error
            }
        });
    }

    /*
     * ==================================================
     * <------------------- MENU BAR ------------------->
     * ==================================================
     */
    public void instantiateMenuBar(Context context) {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        MenuBarManager manager = new MenuBarManager(bottomNavigationView);
        manager.instantiate(context, 'S');
    }
}