package com.cs407.badgerparking;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiateLocationServices();
        instantiateMenuBar(this);
        instantiateAnnounce(this);
        setupParkButton();         //park button needs to be after location services


        // Initializing the mMap
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }




        //for clock management
        TextView clock = findViewById(R.id.clockDisplay);
        Button adjustTime = findViewById(R.id.adjustTime);
        adjustTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.badgerparking", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt("hours", hours).apply();
                        sharedPreferences.edit().putInt("minutes", minutes).apply();
                    }
                }, hour, minute, true);
            }
        });
    }

    public void updateTime(){
        int currHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currMin = Calendar.getInstance().get(Calendar.MINUTE);
        TextView clock = findViewById(R.id.clockDisplay);

        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.badgerparking", Context.MODE_PRIVATE);

        int setHour = sharedPreferences.getInt("hours", 0);
        int setMin = sharedPreferences.getInt("minutes", 0);

        int remHour = 23 - (currHour - setHour);
        int remMin = 60 - (currMin - setMin);

        clock.setText(remHour + ":" + remMin);
    }


    /**
     * =======================================================
     * <------------------- ANNOUNCEMENTS ------------------->
     * =======================================================
     */

    private RssParser rssParser;
    private ParserRunnable parseRun;
    private String annText;
    private Button annButton;

    public void instantiateAnnounce(Context context){

        annButton = findViewById(R.id.annViewButton);
        annButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnnouncementsActivity.class);
                startActivity(intent);
            }
        });

        parseRun = new ParserRunnable();
        new Thread(parseRun).start();
    }


    public class ParserRunnable implements Runnable{
        @Override
        public void run() {
            try {
            rssParser = new RssParser("https://www.cityofmadison.com/feed/news/traffic-engineering");
                annText =
                        String.format("%s\nDate:%s\n", rssParser.getItem(0).getTitle(),
                                rssParser.getItem(0).getPubDate());
            }
            catch (Exception e) {
                annText = "No new announcements can be loaded!";
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView annTextView = (TextView) findViewById(R.id.firstAnView);
                    Log.i("LOG", annText);
                    annTextView.setText(annText);
                }
            });
        }
    }

    /**
     * ===========================================================
     * <------------------- LOCATION SERVICES ------------------->
     * ===========================================================
     */




    private LocationManager locationManager;
    private LocationListener locationListener;
    public Location savedLocation;
    public Address savedAddress;

    private GoogleMap mMap;



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Check if the last known location is not null, and if so, update the map's location
        if (savedLocation != null) {
            updateLocationInfo(savedLocation);
        }
    }




    //project 4 location code
    public void instantiateLocationServices(){
        locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle){

            }
            @Override
            public void onProviderEnabled(String s){

            }

            @Override
            public void onProviderDisabled(String s){

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        }
        else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location!=null){
                    updateLocationInfo(location);
                }
            }
        }
    }

    //project 4 location code
    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //project 4 location code
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void updateLocationInfo(Location location){
        savedLocation = location;
        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> listAddresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0) {
                savedAddress = listAddresses.get(0);
            }

            // If mMap is not null and you have permissions, move the camera to the user's current location.
            if (mMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * =====================================================
     * <------------------- PARK BUTTON ------------------->
     * =====================================================
     */

    public Address parkedAddress;
    public Location parkedLocation;

    public void setupParkButton(){
        ImageButton parkButton = findViewById(R.id.parkButton);
        parkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkedAddress = savedAddress;
                parkedLocation = savedLocation;
            }
        });
    }

    /**
     * ==================================================
     * <------------------- MENU BAR ------------------->
     * ==================================================
     */

    private BottomNavigationView bottomNavigationView;

    public void instantiateMenuBar(Context context) {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        MenuBarManager manager = new MenuBarManager(bottomNavigationView);
        manager.instantiate(context, 'H');
    }

}