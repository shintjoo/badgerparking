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


    public void setupNotiBar(){
        b1 = findViewById(R.id.toggle1);
        b2 = findViewById(R.id.toggle2);
        b3 = findViewById(R.id.toggle3);
        b4 = findViewById(R.id.toggle4);


        //since creating the activity defaults the buttons to false, we only need to make them true
        if (sharedPreferences.getBoolean("5min_warning", false)){
            b1.setChecked(true);
        }

        if (sharedPreferences.getBoolean("10min_warning", false)){
            b2.setChecked(true);
        }

        if (sharedPreferences.getBoolean("15min_warning", false)){
            b3.setChecked(true);
        }

        if (sharedPreferences.getBoolean("20min_warning", false)){
            b4.setChecked(true);
        }

        notiBarClickManager();

    }

    public void notiBarClickManager(){
        View.OnClickListener notiOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationHelper.getInstance().setNotificationContent(getString(R.string.remind_noti_channel_id), "" + b1.isChecked() + " "
                        + b2.isChecked() + " " + b3.isChecked() + " " + b4.isChecked());
                NotificationHelper.getInstance().showNotification(getApplicationContext(), getString(R.string.remind_noti_channel_id));

                sharedPreferences.edit().putBoolean("5min_warning", b1.isChecked())
                                        .putBoolean("10min_warning", b2.isChecked())
                                        .putBoolean("15min_warning", b3.isChecked())
                                        .putBoolean("20min_warning", b4.isChecked())
                                        .apply();
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