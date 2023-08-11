package com.example.racestats

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class DigitalDash : AppCompatActivity() {
    private lateinit var connectButton: Button
    private lateinit var rpmTextView: TextView
    private lateinit var coolantTempTextView: TextView

    private lateinit var obd2Connection: Obd2Connection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.digital_dash)

        // Set the activity to full-screen mode
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        connectButton = findViewById(R.id.connectButton)
        rpmTextView = findViewById(R.id.rpmTextView)
        coolantTempTextView = findViewById(R.id.coolantTempTextView)

        val bluetoothManager = BluetoothManager(this)

        connectButton.setOnClickListener {
            if (!bluetoothManager.isBluetoothSupported()) {
                // Handle unsupported Bluetooth
                return@setOnClickListener
            }

            if (!bluetoothManager.isBluetoothEnabled()) {
                bluetoothManager.enableBluetooth()
            }

            val pairedDevices: Set<BluetoothDevice> = bluetoothManager.getPairedDevices()

            // Select a device from paired devices
            val selectedDevice: BluetoothDevice? = pairedDevices.firstOrNull()

            if (selectedDevice != null) {
                obd2Connection = Obd2Connection(this, selectedDevice)
                obd2Connection.connect()

                // Now you can start sending OBD2 commands and receiving responses
                // Implement your OBD2 command handling and UI updates here
            }
        }
    }
}