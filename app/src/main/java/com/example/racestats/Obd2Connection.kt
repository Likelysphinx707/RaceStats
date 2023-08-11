package com.example.racestats

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.util.*

class Obd2Connection(private val context: Context, private val device: BluetoothDevice) {
    private var socket: BluetoothSocket? = null
    private lateinit var obd2Communication: Obd2Communication

    fun connect() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle the missing permission here
            return
        }

        socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        socket?.connect()

        val inputStream = socket!!.inputStream
        val outputStream = socket!!.outputStream

        obd2Communication = Obd2Communication(inputStream, outputStream)
    }

    fun isConnected(): Boolean {
        return socket?.isConnected == true
    }

    fun close() {
        socket?.close()
    }

    fun getObd2Communication(): Obd2Communication {
        return obd2Communication
    }
}
