package com.cs407.badgerparking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;
    private TextView nearbyStreetsTextView;
    private TextView restrictionTypesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        instantiateMenuBar(this);

        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        nearbyStreetsTextView = findViewById(R.id.nearby_streets_text_view);
        restrictionTypesTextView = findViewById(R.id.restriction_types_text_view);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String streetName = searchEditText.getText().toString().trim();

                // Use Geocoding APIs to get coordinates from street name
                // (Code to retrieve coordinates from street name)

                // Query your database using obtained coordinates to get nearby streets and restrictions
                // (Code to query the database)

                // Update UI with nearby streets and restriction types
                nearbyStreetsTextView.setText("Nearby Streets:\n" + nearbyStreets);
                restrictionTypesTextView.setText("Restriction Types:\n" + restrictionTypes);
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