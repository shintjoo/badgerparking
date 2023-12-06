package com.cs407.badgerparking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
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

        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        streetsList = findViewById(R.id.StreetsList);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

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
                    } else {
                        // Handle address not found
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Query your database using obtained coordinates to get nearby streets and restrictions
                // (Code to query the database)

                // Update UI with nearby streets and restriction types
                //nearbyStreetsTextView.setText("Nearby Streets:\n" + nearbyStreets);
                //restrictionTypesTextView.setText("Restriction Types:\n" + restrictionTypes);
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