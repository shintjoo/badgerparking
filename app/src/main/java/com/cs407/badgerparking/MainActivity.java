package com.cs407.badgerparking;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instantiateLocationServices();
        instantiateMenuBar(this);
        instantiateAnnounce(this);
        setupParkButton();         //park button needs to be after location services

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

    public void instantiateMenuBar(Context context){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mHome){
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.mMap){
                    Intent intent = new Intent(context, MapActivity.class);
                    startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSearch){
                    Intent intent = new Intent(context, SearchActivity.class);
                    startActivity(intent);
                    return true;
                }

                if (item.getItemId() == R.id.mSettings){
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

}