package com.example.racestats;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    private ProgressBar coolantTemperatureGauge;
    private TextView coolantTemperatureText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.gaugetest);

        coolantTemperatureGauge = findViewById(R.id.coolantTemperatureGauge);
        coolantTemperatureText = findViewById(R.id.coolantTemperatureText);

        // Set the coolant temperature (0 to 126, adjust as needed)
        int coolantTemperature = 96; // Change this temperature value

        // Update the temperature text
        coolantTemperatureText.setText(coolantTemperature + "°C");

        // Update the gauge color based on temperature
        updateGaugeColor(coolantTemperature);
    }

    // Method to update the gauge color based on temperature
    private void updateGaugeColor(int temperature) {
        if (temperature > 105) {
            coolantTemperatureGauge.setProgressDrawable(getResources().getDrawable(R.drawable.horizontal_gauge_high));
        } else if(temperature > 96) {
            coolantTemperatureGauge.setProgressDrawable(getResources().getDrawable(R.drawable.horizontal_gauge_medium));
        } else {
            coolantTemperatureGauge.setProgressDrawable(getResources().getDrawable(R.drawable.horizontal_gauge));
        }

        coolantTemperatureGauge.setProgress(temperature);
    }
}
