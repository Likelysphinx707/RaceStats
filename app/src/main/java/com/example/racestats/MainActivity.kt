package com.example.racestats

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val BOOT_PERMISSION_REQUEST_CODE = 123
    private lateinit var ecuInfo: View
    private lateinit var g_meter: View
    private lateinit var maintenance_records: View
    private lateinit var digital_dash: View
    private lateinit var zero_to_sixty: View
    private lateinit var settings: View

    @RequiresApi(Build.VERSION_CODES.S)
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

        // Check if permission is granted for boot
        checkBootPermission()
    }

    private fun checkBootPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!packageManager.canRequestPackageInstalls()) {
                showBootPermissionDialog()
            }
        }
    }

    private fun showBootPermissionDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Permission Required")
        dialogBuilder.setMessage("To start the app on system boot, please grant the necessary permission.")
        dialogBuilder.setPositiveButton("Grant") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            requestBootPermission()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            // Handle cancellation if needed
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun requestBootPermission() {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, BOOT_PERMISSION_REQUEST_CODE)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == BOOT_PERMISSION_REQUEST_CODE) {
                checkBootPermission()
            }
        }

//     Ask User for all needed permissions
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            Log.i("Permission: ", "Granted")
//        } else {
//            Log.i("Permission: ", "Denied")
//        }
//    }
//
//    private fun requestPermission() {
//        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // Permission is granted
//            }
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) -> {
//                // Additional rationale should be displayed
//            }
//            else -> {
//                // Permission has not been asked yet
//            }
//        }
//    }


}