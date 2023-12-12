package com.cs407.badgerparking;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    ToggleButton b1;
    ToggleButton b2;
    ToggleButton b3;
    ToggleButton b4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("com.cs407.badgerparking", Context.MODE_PRIVATE);

        instantiateMenuBar(this);
        setupNotiBar();
    }

    private void setupNotifications(){
        NotificationHelper.getInstance().createRemindNotificationChannel(getApplicationContext());
    }
    
    public void setupNotiBar(){
        b1 = findViewById(R.id.toggle1);
        b2 = findViewById(R.id.toggle2);
        b3 = findViewById(R.id.toggle3);
        b4 = findViewById(R.id.toggle4);

        //since creating the activity defaults the buttons to false, we only need to make them true
        if (sharedPreferences.getBoolean("5min_warning", false)){
            b1.setChecked(true);
        }

        if (sharedPreferences.getBoolean("15min_warning", false)){
            b2.setChecked(true);
        }

        if (sharedPreferences.getBoolean("30min_warning", false)){
            b3.setChecked(true);
        }

        if (sharedPreferences.getBoolean("60min_warning", false)){
            b4.setChecked(true);
        }
        notiBarClickManager();
    }

    public void notiBarClickManager(){
        View.OnClickListener notiOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("alarm", "5 " + b1.isChecked() + " 15 " +
                                                b2.isChecked() + " 30 "  + b3.isChecked() + " 60 " + b4.isChecked());

                sharedPreferences.edit().putBoolean("5min_warning", b1.isChecked())
                                        .putBoolean("15min_warning", b2.isChecked())
                                        .putBoolean("30min_warning", b3.isChecked())
                                        .putBoolean("60min_warning", b4.isChecked())
                                        .apply();

                if (!b1.isChecked()){
                    sharedPreferences.edit().putBoolean("5_alive", false).apply();
                }

                if (!b2.isChecked()){
                    sharedPreferences.edit().putBoolean("15_alive", false).apply();
                }

                if (!b3.isChecked()){
                    sharedPreferences.edit().putBoolean("30_alive", false).apply();
                }

                if (!b4.isChecked()){
                    sharedPreferences.edit().putBoolean("60_alive", false).apply();
                }

            }
        };

        b1.setOnClickListener(notiOnClick);
        b2.setOnClickListener(notiOnClick);
        b3.setOnClickListener(notiOnClick);
        b4.setOnClickListener(notiOnClick);
    }

    /*
     * ==================================================
     * <------------------- MENU BAR ------------------->
     * ==================================================
     */
    public void instantiateMenuBar(Context context) {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        MenuBarManager manager = new MenuBarManager(bottomNavigationView);
        manager.instantiate(context, 'E');
    }
}