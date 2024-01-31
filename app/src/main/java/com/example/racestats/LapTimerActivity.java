package com.example.racestats;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LapTimerActivity extends AppCompatActivity {

    private static final String TAG = "LapTimer";
    private static final String DATABASE_NAME = "lap_timer_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "lap_times";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LAP_TIME = "lap_time";
    private static final String COLUMN_DATE_TIME = "date_time";
    static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private SQLiteDatabase database;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private long startTime;
    private Location startLocation;
    private List<Long> lapTimes;

    private TextView lapTimeTextView;
    private Button startButton, stopButton;
    private LapTimer lapTimer;

    private Handler handler;
    private Runnable updateTimerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laptimer);

        lapTimeTextView = findViewById(R.id.lapTimeTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        lapTimer = new LapTimer(this);

        handler = new Handler();
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                updateLapTimeDisplay();
                handler.postDelayed(this, 10); // Update every 10 milliseconds
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lapTimer.startTimer(LapTimerActivity.this);
                startTime = SystemClock.elapsedRealtime();
                lapTimes = lapTimer.getLapTimes(); // Get initial lap times
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                handler.postDelayed(updateTimerRunnable, 10);
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lapTimer.stopTimer();
                handler.removeCallbacks(updateTimerRunnable); // Stop the timer updates
                updateLapTimeDisplay();
                displayAllLapTimes();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
    }

    private void updateLapTimeDisplay() {
        long currentLapTime = lapTimer.getCurrentLapTime();
        lapTimeTextView.setText(formatTime(currentLapTime));
    }

    private void displayAllLapTimes() {
        // Display all lap times in the TextView
        StringBuilder lapTimesText = new StringBuilder();
        for (int i = 0; i < lapTimes.size(); i++) {
            lapTimesText.append("Lap ").append(i + 1).append(": ").append(formatTime(lapTimes.get(i))).append("\n");
        }
        lapTimeTextView.setText(lapTimesText.toString());
    }

    private String formatTime(long milliseconds) {
        long minutes = milliseconds / (60 * 1000);
        long seconds = (milliseconds / 1000) % 60;
        long millis = (milliseconds % 1000) / 100; // Extract only the first digit of milliseconds
        return String.format("%02d:%02d:%01d", minutes, seconds, millis);
    }

}
