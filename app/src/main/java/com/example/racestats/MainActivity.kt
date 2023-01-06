package com.example.racestats

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    // Import the variables we will be editing from the UI XML file
    private lateinit var cpuTemp: TextView
    private lateinit var speed: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var timer: TextView
    private lateinit var startStopTimer: androidx.appcompat.widget.AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Declare are imported variables from the XML file
        cpuTemp = findViewById(R.id.cpuTemp)
        speed = findViewById(R.id.speed)
        progressBar = findViewById(R.id.progressBar)
        timer = findViewById(R.id.timer)
        startStopTimer = findViewById(R.id.startStopTimer)

        // This wil handle our event when a user clicks the start or stop button
        startStopTimer.setOnClickListener {
            cpuTemperature()
        }
    }
}

fun cpuTemperature(): Float {
    val process: Process
    return try {
        process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp")
        process.waitFor()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val line: String = reader.readLine()
        if (line != null) {
            val temp = line.toFloat()
            temp / 1000.0f
        } else {
            51.0f
        }
    } catch (e: Exception) {
        e.printStackTrace()
        0.0f
    }
}

fun timer() {
    var time = 0.00

//    while(speed <= 60) {
//
//        speed += 1
//    }
}
