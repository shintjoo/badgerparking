package com.cs407.badgerparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MapActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        context = this;
        instantiateMenuBar();
    }


    /**
     * ==================================================
     * <------------------- MENU BAR ------------------->
     * ==================================================
     */
    private BottomNavigationView bottomNavigationView;

    public void instantiateMenuBar(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mMap);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mHome){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.mMap){
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSearch){
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSettings){
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}