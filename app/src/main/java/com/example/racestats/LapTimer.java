package com.example.racestats;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class represents a Lap Timer that uses GPS to record lap times.
 * It also stores lap times in a SQLite database.
 */
public class LapTimer implements LocationListener {

    private static final String TAG = "LapTimer";
    private static final String DATABASE_NAME = "lap_timer_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "lap_times";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LAP_TIME = "lap_time";
    private static final String COLUMN_DATE_TIME = "date_time";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    private SQLiteDatabase database;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private long currentLapStartTime;
    private long startTime;
    private Location startLocation;
    private List<Long> lapTimes;

    /**
     * Constructor for LapTimer class.
     * Initializes the database, location manager, and other variables.
     * @param context The context of the application.
     */
    public LapTimer(Context context) {
        LapTimerOpenHelper dbHelper = new LapTimerOpenHelper(context);
        database = dbHelper.getWritableDatabase();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        lapTimes = new ArrayList<>();
    }

    /**
     * Starts the lap timer. Initiates location updates and resets lap time records.
     * @param activity The current activity.
     */
    public void startTimer(Activity activity) {
        if (isGPSEnabled) {
            if (hasLocationPermissions(activity)) {
                startLocationUpdates(activity);
                currentLapStartTime = SystemClock.elapsedRealtime(); // Initialize currentLapStartTime
                startTime = SystemClock.elapsedRealtime();
                lapTimes.clear(); // Clear previous lap times
            } else {
                // Permissions not granted, you might want to inform the user or request permissions again
                Log.e(TAG, "Location permissions not granted");
                // Request permissions again or show a message to the user
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.e(TAG, "GPS is not enabled");
            // Handle GPS not enabled
        }
    }


    /**
     * Checks if the app has the necessary location permissions.
     * @param activity The current activity.
     * @return True if location permissions are granted, false otherwise.
     */
    private boolean hasLocationPermissions(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Stops the lap timer by stopping location updates.
     */
    public void stopTimer() {
        stopLocationUpdates();
    }

    /**
     * Retrieves the list of recorded lap times.
     * @return List of lap times.
     */
    public List<Long> getLapTimes() {
        return lapTimes;
    }

    /**
     * Callback method invoked when the location changes.
     * Records a lap time when the GPS detects completion of a lap.
     * @param location The new location.
     */
    @Override
    public void onLocationChanged(Location location) {
        if (startLocation == null) {
            startLocation = location;
        }

        float distance = location.distanceTo(startLocation);

        if (distance < 10) {
            long lapTime = SystemClock.elapsedRealtime() - currentLapStartTime;
            lapTimes.add(lapTime);
            saveLapTime(lapTime);
            currentLapStartTime = SystemClock.elapsedRealtime(); // Update the start time for the next lap
        }
    }

    /**
     * Initiates location updates.
     * @param activity The current activity.
     */
    private void startLocationUpdates(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Continue with location updates if permissions are already granted
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                100,  // minimum time interval between updates (in milliseconds)
                1,     // minimum distance between updates (in meters)
                this   // LocationListener
        );
    }

    /**
     * Stops location updates.
     */
    private void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    /**
     * Retrieves the current lap time.
     * @return The current lap time in milliseconds.
     */
    public long getCurrentLapTime() {
        return SystemClock.elapsedRealtime() - currentLapStartTime;
    }

    /**
     * Saves a lap time record to the SQLite database.
     * @param lapTime The lap time to be saved.
     */
    void saveLapTime(long lapTime) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAP_TIME, lapTime);
        values.put(COLUMN_DATE_TIME, getCurrentDateTime());

        long newRowId = database.insert(TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(TAG, "Error saving lap time to database");
        }
    }

    /**
     * Retrieves the current date and time.
     * @return A string representing the current date and time.
     */
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Helper class for database creation and version management.
     */
    private static class LapTimerOpenHelper extends SQLiteOpenHelper {
        LapTimerOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LAP_TIME + " INTEGER, " +
                    COLUMN_DATE_TIME + " TEXT)";
            db.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Handle database upgrades if needed
        }
    }
}