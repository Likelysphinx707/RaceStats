package com.example.racestats

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var ecuInfo: View
    private lateinit var g_meter: View
    private lateinit var maintenance_records: View
    private lateinit var digital_dash: View
    private lateinit var zero_to_sixty: View
    private lateinit var settings: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the activity to full-screen mode
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        // Grab our link variables
        ecuInfo = findViewById(R.id.ecuInfo)
        g_meter = findViewById(R.id.g_meter)
        maintenance_records = findViewById(R.id.maintenance_records)
        digital_dash = findViewById(R.id.digital_dash)
        zero_to_sixty = findViewById(R.id.zero_to_sixty)
        settings = findViewById(R.id.settings)

        // Links to other views
        ecuInfo.setOnClickListener {
            val intent = Intent(this, EcuData::class.java)
            startActivity(intent)
        }

        g_meter.setOnClickListener {
            val intent = Intent(this, GMeter::class.java)
            startActivity(intent)
        }

        maintenance_records.setOnClickListener {
            val intent = Intent(this, ServiceRecords::class.java)
            startActivity(intent)
        }

        digital_dash.setOnClickListener {
            val intent = Intent(this, BluetoothDeviceFinder::class.java)
            startActivity(intent)
        }

        zero_to_sixty.setOnClickListener {
            val intent = Intent(this, ZeroToSixty::class.java)
            startActivity(intent)
        }

        settings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

    }
}