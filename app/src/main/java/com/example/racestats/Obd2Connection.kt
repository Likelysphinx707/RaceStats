package com.example.racestats

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class Obd2Connection(private val context: Context, private val device: BluetoothDevice) {
    private var socket: BluetoothSocket? = null

    fun connect() {
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        socket = device.createRfcommSocketToServiceRecord(uuid)

        try {
            socket?.connect()
        } catch (e: IOException) {
            // Handle connection error
        }
    }

    fun getInputStream(): InputStream? {
        return socket?.inputStream
    }

    fun getOutputStream(): OutputStream? {
        return socket?.outputStream
    }

    fun isConnected(): Boolean {
        return socket?.isConnected == true
    }

    fun close() {
        socket?.close()
    }
}
