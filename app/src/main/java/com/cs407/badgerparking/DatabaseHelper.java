package com.cs407.badgerparking;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH = ""; // It should be empty initially, I'll adjust it below.
    private static String DB_NAME = "parkingData.db";
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH = myContext.getDatabasePath(DB_NAME).toString(); // Adjust the path here.
        createDatabase();
    }

    /**
     * Create database if it doesn't exist and copy it from assets.
     */
    private void createDatabase() {
        boolean dbExist = checkDatabase();

        if (!dbExist) {
            this.getReadableDatabase(); // This will create an empty database.
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exists.
     */
    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // Database doesn't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    /**
     * Copy the database from assets.
     */
    private void copyDatabase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        OutputStream myOutput = new FileOutputStream(DB_PATH);

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
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT restriction_text FROM parking_restrictions WHERE latitude = ? AND longitude = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(lat), String.valueOf(lng)});

        if (cursor.moveToFirst()) {
            restriction = cursor.getString(cursor.getColumnIndex("restriction_text"));
        }
        cursor.close();
        return restriction;
    }
}
