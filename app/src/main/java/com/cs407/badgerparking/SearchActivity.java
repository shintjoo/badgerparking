package com.cs407.badgerparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
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
    private EditText searchEditText;
    private Button searchButton;
    private ListView streetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
                // Get the selected place's name, ID, and LatLng
                String placeName = place.getName();
                String placeId = place.getId();
                LatLng latLng = place.getLatLng();

                if (placeName != null) {
                    // Add the selected place's name to the list
                    locationsList.add(placeName);
                    // Notify the adapter that the data has changed
                    locationAdapter.notifyDataSetChanged();
                }

                if (latLng != null) {
                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;

                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle error
            }
        });

        /**
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String streetName = searchEditText.getText().toString().trim();
                // Convert address to coordinates
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(streetName, 1);
                    if (addresses != null && addresses.size() > 0) {
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();

                        // Use latitude and longitude
                        // Query the database using obtained coordinates to get nearby streets and restrictions

                    } else {
                        // Handle address not found



                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }



                // Update UI with nearby streets and restriction types
                //nearbyStreetsTextView.setText("Nearby Streets:\n" + nearbyStreets);
                //restrictionTypesTextView.setText("Restriction Types:\n" + restrictionTypes);
            }
        });
         **/
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