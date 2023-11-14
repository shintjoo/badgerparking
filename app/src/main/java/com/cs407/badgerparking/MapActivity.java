package com.cs407.badgerparking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        instantiateMenuBar(this);

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
}