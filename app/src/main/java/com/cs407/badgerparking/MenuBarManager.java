package com.cs407.badgerparking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MenuBarManager {
    private BottomNavigationView menuBar;

    public MenuBarManager(BottomNavigationView menuBar){
        this.menuBar = menuBar;

    }


    public void instantiate(Context context, char select){
        setSelected(select);
        menuBar.setOnItemSelectedListener(menuClickListener(context));

    }

    public void setSelected(char select){
        switch (select){
            case 'H':
                menuBar.setSelectedItemId(R.id.mHome);
                break;
            case 'M':
                menuBar.setSelectedItemId(R.id.mMap);
                break;
            case 'S':
                menuBar.setSelectedItemId(R.id.mSearch);
                break;
            case 'E':
                menuBar.setSelectedItemId(R.id.mSettings);
                break;
            default:
                Log.i("LOG", "valid selections are 'H'ome, 'M'ap, 'S'earch, s'E'ttings");
        }
    }


    public NavigationBarView.OnItemSelectedListener menuClickListener(Context context) {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mHome){
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.mMap){
                    Intent intent = new Intent(context, MapActivity.class);
                    context.startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSearch){
                    Intent intent = new Intent(context, SearchActivity.class);
                    context.startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSettings){
                    Intent intent = new Intent(context, SettingsActivity.class);
                    context.startActivity(intent);
                    return true;
                }
                return false;
            }
        };
    }
}
