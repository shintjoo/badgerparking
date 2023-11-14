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

import android.util.Log;


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

    public String getParkingRestriction(double lat, double lng) {
        String restriction = "No Data Available";

        // Using hardcoded absolute path for the database
        String DB_PATH = "/data/data/com.cs407.badgerparking/databases/parkingData.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

        // Round off the coordinates to 4 decimal places
        double roundedLat = Math.round(lat * 10000.0) / 10000.0;
        double roundedLng = Math.round(lng * 10000.0) / 10000.0;

        // Tolerance for the search
        double tolerance = 0.001;

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


}
