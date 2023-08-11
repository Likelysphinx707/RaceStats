package com.example.racestats

import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream


class Obd2Communication(private val inputStream: InputStream, private val outputStream: OutputStream) {

    fun sendCommand(command: String) {
        val commandWithCrLf = "$command\r\n"
        outputStream.write(commandWithCrLf.toByteArray())
        outputStream.flush()
    }

    fun readResponse(): String {
        val buffer = ByteArray(1024)
        val bytesRead = inputStream.read(buffer)
        return String(buffer, 0, bytesRead)
    }

    // Implement methods to parse OBD2 responses here
    // Parse the responses to get RPM and coolant temperature
}
