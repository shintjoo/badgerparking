package com.cs407.badgerparking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH; // No initial value required here.
    private static String DB_NAME = "parkingData.db";
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH =  context.getDatabasePath(DB_NAME).getAbsolutePath();
        createDatabase();
    }


    private static final String PREFS_NAME = "com.cs407.badgerparking.prefs";
    private static final String PREFS_KEY_DB_CREATED = "database_created";

    private boolean isDatabaseCreated() {
        SharedPreferences prefs = myContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREFS_KEY_DB_CREATED, false);
    }

    private void setDatabaseCreated() {
        SharedPreferences prefs = myContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREFS_KEY_DB_CREATED, true);
        editor.apply();
    }

    /**
     * Create database if it doesn't exist and copy it from assets.
     */
    private void createDatabase() {
        File dbFile = new File(DB_PATH);
        if (dbFile.exists() && isDatabaseCreated()) {
            return; // Skip copying if database already exists and is marked as created
        }


        if (!isDatabaseCreated()) {
            try {
                copyDatabase();
                setDatabaseCreated();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }


    /**
     * Copy the database from assets.
     */
    public void copyDatabase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        Log.d("DatabaseHelper", "Checking Database existence...");

        OutputStream myOutput = Files.newOutputStream(Paths.get(DB_PATH));
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Leave this empty if you're not using it.
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Leave this empty if you're not using it.
    }

    @SuppressLint("Range")
    public String getParkingRestriction(double lat, double lng) {
        String restriction = "No Data Available";

        // Using hardcoded absolute path for the database
        String DB_PATH = "/data/data/com.cs407.badgerparking/databases/parkingData.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

        // Round off the coordinates to 6 decimal places
        double roundedLat = Math.round(lat * 1000000.0) / 1000000.0;
        double roundedLng = Math.round(lng * 1000000.0) / 1000000.0;

        // Tolerance for the search
        double tolerance = 0.0005;

        // Log the rounded values
        Log.d("DatabaseHelper", "Rounded Latitude: " + roundedLat);
        Log.d("DatabaseHelper", "Rounded Longitude: " + roundedLng);

        // Modify the SQL query to look for rounded lat/lng values within the tolerance range
       String query = "SELECT restriction_text FROM parking_restrictions WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(roundedLat - tolerance),
                String.valueOf(roundedLat + tolerance),
                String.valueOf(roundedLng - tolerance),
                String.valueOf(roundedLng + tolerance)});

        if (cursor.moveToFirst()) {
            restriction = cursor.getString(cursor.getColumnIndex("restriction_text"));
            Log.d("DatabaseHelper", "Found restriction: " + restriction);  // Log the retrieved restriction
        } else {
            Log.d("DatabaseHelper", "No restrictions found for the given lat/lng.");
        }

        cursor.close();
        db.close();  // Close the database connection
        return restriction;
    }

    @SuppressLint("Range")
    public int getParkingTime(double lat, double lng){
        int limit = 0;

        // Using hardcoded absolute path for the database
        String DB_PATH = "/data/data/com.cs407.badgerparking/databases/parkingData.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

        // Round off the coordinates to 4 decimal places
        double roundedLat = Math.round(lat * 1000000.0) / 1000000.0;
        double roundedLng = Math.round(lng * 1000000.0) / 1000000.0;

        // Tolerance for the search
        double tolerance = 0.0005;

        String query = "SELECT time_limit_minutes FROM parking_restrictions WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(roundedLat - tolerance),
                String.valueOf(roundedLat + tolerance),
                String.valueOf(roundedLng - tolerance),
                String.valueOf(roundedLng + tolerance)});

        if (cursor.moveToFirst()) {
            limit = cursor.getInt(cursor.getColumnIndex("time_limit_minutes"));
            Log.d("DatabaseHelper", "Found parking time: " + limit);  // Log the retrieved restriction
        } else {
            Log.d("DatabaseHelper", "No parking time found for the given lat/lng.");
        }

        cursor.close();
        db.close();

        return limit;
    }


    public List<LatLng> getLocationsWithinBounds(LatLngBounds bounds) {
        List<LatLng> locations = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            // Define a query to select rows within the map bounds
            String selection = "latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?";
            String[] selectionArgs = {
                    String.valueOf(bounds.southwest.latitude),
                    String.valueOf(bounds.northeast.latitude),
                    String.valueOf(bounds.southwest.longitude),
                    String.valueOf(bounds.northeast.longitude)
            };

            // Execute the query
            cursor = db.query(
                    "parking_restrictions", // Table name
                    new String[]{"latitude", "longitude"}, // Columns to return
                    selection, // Selection criteria
                    selectionArgs, // Selection arguments
                    null, // Group by
                    null, // Having
                    "latitude LIMIT 10"  // Order by
            );

            // Iterate over the result set and build the list of LatLng objects
            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double lng = cursor.getDouble(cursor.getColumnIndex("longitude"));
                locations.add(new LatLng(lat, lng));
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while trying to get locations from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return locations;
    }


    // Inside DatabaseHelper class
    public List<MyClusterItem> getAllClusterItems() {
        List<MyClusterItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT latitude, longitude, restriction_text FROM parking_restrictions", null);

        while (cursor.moveToNext()) {
            double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double lng = cursor.getDouble(cursor.getColumnIndex("longitude"));
            String restrictionText = cursor.getString(cursor.getColumnIndex("restriction_text"));
            // Handle the potential null value for restriction_text
            if (restrictionText == null) {
                restrictionText = "No restriction information available";
            }
            items.add(new MyClusterItem(lat, lng, "Restriction:", restrictionText));
        }
        cursor.close();
        return items;
    }

    @SuppressLint("Range")
    public String getParkingRestrictionExact(double lat, double lng) {
        String restriction = "No Data Available";

        // Using hardcoded absolute path for the database
        String DB_PATH = "/data/data/com.cs407.badgerparking/databases/parkingData.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

        /**
        // Round off the coordinates to 4 decimal places
        double roundedLat = Math.round(lat * 1000000.0) / 1000000.0;
        double roundedLng = Math.round(lng * 1000000.0) / 1000000.0;



        // Log the rounded values
        Log.d("DatabaseHelper", "Rounded Latitude: " + roundedLat);
        Log.d("DatabaseHelper", "Rounded Longitude: " + roundedLng);
        **/
        // Tolerance for the search
        double tolerance = 0;

        // Modify the SQL query to look for rounded lat/lng values within the tolerance range
        String query = "SELECT restriction_text FROM parking_restrictions WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(lat - tolerance),
                String.valueOf(lat + tolerance),
                String.valueOf(lng - tolerance),
                String.valueOf(lng + tolerance)});

        if (cursor.moveToFirst()) {
            restriction = cursor.getString(cursor.getColumnIndex("restriction_text"));
            Log.d("DatabaseHelper", "Found restriction: " + restriction);  // Log the retrieved restriction
        } else {
            Log.d("DatabaseHelper", "No restrictions found for the given lat/lng.");
        }

        cursor.close();
        db.close();  // Close the database connection
        return restriction;
    }




}
