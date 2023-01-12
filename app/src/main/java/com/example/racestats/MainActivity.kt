package com.example.racestats

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Duration
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    // Import the variables we will be editing from the UI XML file
    private lateinit var cpuTemp: TextView
    private lateinit var speed: TextView
    private lateinit var mph: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var timer: TextView
    private lateinit var startStopTimer: androidx.appcompat.widget.AppCompatButton
    private lateinit var recordedTimes: TextView
    private lateinit var yellowTimesBar: View
    private lateinit var recordedTimeOne: TextView
    private lateinit var recordedTimeTwo: TextView
    private lateinit var recordedTimeThree: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Declare are imported variables from the XML file
        cpuTemp = findViewById(R.id.cpuTemp)
        speed = findViewById(R.id.speed)
        mph = findViewById(R.id.mph)
        progressBar = findViewById(R.id.progressBar)
        timer = findViewById(R.id.timer)
        startStopTimer = findViewById(R.id.startStopTimer)
        recordedTimes = findViewById(R.id.recordedTimes)
        yellowTimesBar = findViewById(R.id.yellowTimesBar)
        recordedTimeOne = findViewById(R.id.recordedTimeOne)
        recordedTimeTwo = findViewById(R.id.recordedTimeTwo)
        recordedTimeThree = findViewById(R.id.recordedTimeThree)


        // This wil handle our event when a user clicks the start or stop button
        startStopTimer.setOnClickListener{
            progressBarAnimation(progressBar, speed, mph, timer, recordedTimes, yellowTimesBar,  recordedTimeOne, recordedTimeTwo, recordedTimeThree)
        }
    }
}


/**
 * Handles the Progress Bar Animation as the speed increases or decreases
 * @param progressBar takes the progressBar variable as a param so we can manipulate it in the UI
 */
fun progressBarAnimation(progressBar: ProgressBar, speed: TextView, mph: TextView, timer: TextView, times: TextView, yellowTimesBar: View, recTime1: TextView, recTime2: TextView, recTime3: TextView) {
    // we will increase the max after each milestone has been hit
    progressBar.max = 60
    // need ot set currentProgress val equal to current speed
    var currentProgress = 0
    while (currentProgress < progressBar.max) {
        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
            .setDuration(500)
            .start()
        currentProgress++
    }

    // this will make the display flash 3 times when we hit a speed milestone
    Timer().schedule(500) {
        speed.setTextColor(Color.parseColor("#FFE222"))
        mph.setTextColor(Color.parseColor("#FFE222"))
        timer.setTextColor(Color.parseColor("#FFE222"))
        if(currentProgress == 60) {
            times.visibility = View.VISIBLE;
            yellowTimesBar.visibility = View.VISIBLE;
            recTime1.visibility = View.VISIBLE;
        } else if(currentProgress == 100) {
            recTime2.visibility = View.VISIBLE;
        } else if(currentProgress == 120) {
            recTime3.visibility = View.VISIBLE;
        }
    }
    Timer().schedule(900) {
        speed.setTextColor(Color.parseColor("#FFFFFF"))
        mph.setTextColor(Color.parseColor("#FFFFFF"))
        timer.setTextColor(Color.parseColor("#FFFFFF"))
    }
    Timer().schedule(1300) {
        // Changes text color
        speed.setTextColor(Color.parseColor("#FFE222"))
        mph.setTextColor(Color.parseColor("#FFE222"))
        timer.setTextColor(Color.parseColor("#FFE222"))
    }
    Timer().schedule(1700) {
        speed.setTextColor(Color.parseColor("#FFFFFF"))
        mph.setTextColor(Color.parseColor("#FFFFFF"))
        timer.setTextColor(Color.parseColor("#FFFFFF"))
    }
    Timer().schedule(2100) {
        speed.setTextColor(Color.parseColor("#FFE222"))
        mph.setTextColor(Color.parseColor("#FFE222"))
        timer.setTextColor(Color.parseColor("#FFE222"))
    }
    Timer().schedule(2500) {
        speed.setTextColor(Color.parseColor("#FFFFFF"))
        mph.setTextColor(Color.parseColor("#FFFFFF"))
        timer.setTextColor(Color.parseColor("#FFFFFF"))
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
