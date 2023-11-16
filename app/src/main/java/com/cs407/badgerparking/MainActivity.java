package com.cs407.badgerparking;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.cs407.badgerparking", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("warnings_displayed", 0)
                .apply();

        dbHelper = new DatabaseHelper(this); // Instantiating restriction database
        try {
            dbHelper.copyDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        requestNotiPermission();
        setupNotifications();

        instantiateLocationServices();
        instantiateMenuBar(this);
        instantiateAnnounce(this);

        // Initializing the mMap
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Creating fragments for adjusting the timer
        Button adjustTime = findViewById(R.id.adjustTime);
        adjustTime.setOnClickListener(view -> {
            com.cs407.badgerparking.DatePicker datePickerDialog = new com.cs407.badgerparking.DatePicker();
            datePickerDialog.show(getSupportFragmentManager(), "DATE PICK");

            com.cs407.badgerparking.TimePicker timePickerDialog = new com.cs407.badgerparking.TimePicker();
            timePickerDialog.show(getSupportFragmentManager(), "TIME PICK");
        });
        updateTime();


        setupParkButton();
    }

    /*
     * ===============================================================
     * <----------------------- NOTIFICATIONS ----------------------->
     * ===============================================================
     */

    private final ActivityResultLauncher<String> requestNotificationLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted){
                    Toast.makeText(this, "Please allow notifications", Toast.LENGTH_LONG).show();
                }
            });

    private void requestNotiPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED){
            requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void setupNotifications(){
        NotificationHelper.getInstance().createRemindNotificationChannel(getApplicationContext());
    }


    /*
     * =======================================================
     * <----------------------- TIMER ----------------------->
     * =======================================================
     */

    private CountDownTimer timeBeforeMove;
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        sharedPreferences.edit().putInt("year", year).putInt("month", month).putInt("day", dayOfMonth).apply();
        updateTime();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute){
        sharedPreferences.edit().putInt("hour", hour).putInt("minute", minute).apply();
    }

    public void updateTime(){
        if(timeBeforeMove != null) {
            timeBeforeMove.cancel();
        }

        TextView clock = findViewById(R.id.clockDisplay);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, sharedPreferences.getInt("year", 0));
        mCalendar.set(Calendar.MONTH, sharedPreferences.getInt("month", 0));
        mCalendar.set(Calendar.DAY_OF_MONTH, sharedPreferences.getInt("day", 0));
        mCalendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("hour", 0));
        mCalendar.set(Calendar.MINUTE, sharedPreferences.getInt("minute", 0));

        Log.d("Logged", sharedPreferences.getInt("year", 0) + "/" + sharedPreferences.getInt("month", 0) + "/" + sharedPreferences.getInt("day", 0)+ " " + sharedPreferences.getInt("hour", 0) + ":" + sharedPreferences.getInt("minute", 0));

        Calendar currentTime = Calendar.getInstance();
        Date currentDate = currentTime.getTime();
        Date setDate = mCalendar.getTime();

        int difference = (int) (currentDate.getTime() - setDate.getTime())/1000;

        int hours = difference/3600;

        int countHours = 47 - hours;
        int countMinutes = 60 - ((difference/60) - hours*60);

        int countdown = countHours * 3600000  + countMinutes * 60000;

        CountDownTimer timeBeforeMove = new CountDownTimer (countdown,60000){
            public void onTick(long remain){

                long totRemSec = remain/1000;

                long remHour = totRemSec/3600;

                long remMin = (totRemSec/60) - remHour*60;

                String display;
                if(remMin > 9) {
                    display = remHour + ":" + remMin;
                }else{
                    display = remHour + ":0" + remMin;
                }

                if (remHour == 0 && remMin >= 1){
                    timerNotiManager(Math.toIntExact(remMin));
                }
                if (remMin <= 1){
                    sharedPreferences.edit().putInt("warnings_displayed", 0)
                            .apply();
                }

                clock.setText(display);
                clock.setTextColor(Color.WHITE);
            }

            @Override
            public void onFinish() {
                clock.setText("00:00");
                clock.setTextColor(Color.RED);
            }
        }.start();


    }

    //why in gods name did i chose to do it this way
    private void timerNotiManager(int remMin){
        boolean[] warnings = {
                sharedPreferences.getBoolean("5min_warning", false),
                sharedPreferences.getBoolean("10min_warning", false),
                sharedPreferences.getBoolean("15min_warning", false),
                sharedPreferences.getBoolean("20min_warning", false)
        };

        /* 0    2    4    6
        *  00   01   10   11
         */

        int warningsShown = sharedPreferences.getInt("warnings_displayed", 0);
        int timeFlag = -1;

        if (remMin <= 20 && warnings[3] && warningsShown == 0){
            sharedPreferences.edit().putInt("warnings_displayed", 1)
                    .apply();
            timeFlag = 20;
        }

        if (remMin <= 15 && warnings[2] && warningsShown == 1){
            sharedPreferences.edit().putInt("warnings_displayed", 2)
                    .apply();
            timeFlag = 15;
        }

        if (remMin <= 10 && warnings[1] && warningsShown == 2){
            sharedPreferences.edit().putInt("warnings_displayed", 3)
                    .apply();
            timeFlag = 10;
        }

        //if 00
        if (remMin <= 5 && warnings[0] && warningsShown == 3){
            sharedPreferences.edit().putInt("warnings_displayed", -1)
                    .apply();
            timeFlag = 5;
        }

        if (timeFlag != -1){
            NotificationHelper.getInstance().setNotificationContent(getString(R.string.remind_noti_channel_id),
                    timeFlag + " minutes left!");

            NotificationHelper.getInstance().showNotification(getApplicationContext(), getString(R.string.remind_noti_channel_id));
            timeFlag = -1;
        }
    }

    /*
     * =======================================================
     * <------------------- ANNOUNCEMENTS ------------------->
     * =======================================================
     */

    private String annText;

    public void instantiateAnnounce(Context context){
        Button annButton = findViewById(R.id.annViewButton);
        annButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, AnnouncementsActivity.class);
            startActivity(intent);
        });

        ParserRunnable parseRun = new ParserRunnable();
        new Thread(parseRun).start();
    }

    public class ParserRunnable implements Runnable{
        @Override
        public void run() {
            try {

                RssParser rssParser = new RssParser(getResources().getString(R.string.rss_url_engineering));
                annText =
                        String.format("%s\nDate:%s\n", rssParser.getItem(0).getTitle(),
                                rssParser.getItem(0).getPubDate());
            }
            catch (Exception e) {
                annText = "No new announcements can be loaded!";
            }

            runOnUiThread(() -> {
                TextView annTextView = findViewById(R.id.firstAnView);
                annTextView.setText(annText);
            });
        }
    }

    /*
     * ===========================================================
     * <------------------- LOCATION SERVICES ------------------->
     * ===========================================================
     */
    private LocationManager locationManager;
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
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(@NonNull String s) {

            }

            @Override
            public void onProviderDisabled(@NonNull String s) {

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


    /*
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

                if (parkedLocation == null) {
                    Toast.makeText(getApplicationContext(), "Location not available. Please wait and try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if parkedLocation is not null
                if (parkedLocation != null) {
                    // Get the parking restriction based on the user's location
                    String restriction = dbHelper.getParkingRestriction(parkedLocation.getLatitude(), parkedLocation.getLongitude());

                    //Store current time for timer
                    LocalDateTime now =  LocalDateTime.now();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

                    sharedPreferences.edit().putInt("year", now.getYear())
                            .putInt("month", now.getMonthValue() - 1)
                            .putInt("day", now.getDayOfMonth())
                            .putInt("hour", now.getHour())
                            .putInt("minute", now.getMinute()).apply();
                    updateTime();

                    // Here, you can use the obtained restriction string.
                    // For example, display it in a Toast or any other UI element:
                    Toast.makeText(getApplicationContext(), restriction, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    /*
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