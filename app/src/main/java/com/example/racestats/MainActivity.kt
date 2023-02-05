package com.example.racestats

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*


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

    private var targetTime: Double = 120.0
    private var methodRunning = false


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
            // Check to see if start button has already been clicked or not
            if (!methodRunning) {
                methodRunning = true
                startStopTimer.text = "STOP"
                // This wil handle our event when a user clicks the start or stop button
                startStopTimer.setOnClickListener{
                    uiAnimations(targetTime, progressBar, speed, timer)
                }
                methodRunning = false
                startStopTimer.text = "START"
            } else {
                methodRunning = false
                startStopTimer.text = "START"
            }
        }
    }
}


/**
 * Handles the progress bar, mph, and timer UI display as the speed increases or decreases
 * @param progressBar takes the progressBar variable as a param so we can manipulate it in the UI
 */
fun uiAnimations(targetTime: Double, progressBar: ProgressBar, speedTextView: TextView, timerTextView: TextView) {
    val timer = Timer()
    var speed = 0
    var time = 0

    timer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            speed += 2
            time += 132

            val progress = (speed / targetTime) * 100

            // updates the mph
            speedTextView.text = "$speed"

            // updates progress bar code
            progressBar.progress = progress.toInt()

            // updates the display time
            val minutes = time / 60000
            val seconds = (time % 60000) / 1000
            val milliseconds = time % 1000
            timerTextView.text = "${formatTime(minutes)}:${formatTime(seconds)}.${formatMilliseconds(milliseconds)}"

            println("Speed: $speed mph\tTime: $time ms\tProgress: $progress%")

            if (speed >= targetTime) {

                println("Reached ${targetTime.toInt()} mph in ${formatTime(minutes)}:${formatTime(seconds)}.${formatMilliseconds(milliseconds)}")
                timer.cancel()
            }
        }
    }, 0, 100)
}

fun formatMilliseconds(time: Int): String {
    return String.format("%02d", time/10)
}

fun formatTime(time: Int): String {
    return if (time < 10) {
        "0$time"
    } else {
        "$time"
    }
}


//fun cpuTemperature(): Float {
//    val process: Process
//    return try {
//        process = Runtime.getRuntime().exec("cat /sys/class/thermal/thermal_zone1/temp")
//        process.waitFor()
//        val reader = BufferedReader(InputStreamReader(process.inputStream))
//        val line: String = reader.readLine()
//        if (line != null) {
//            val temp = line.toFloat()
//            println(temp / 1000.0f)
//            temp / 1000.0f
//        } else {
//            println(51.0f)
//            51.0f
//        }
//    } catch (e: Exception) {
//        println("about to look for temp")
//        e.printStackTrace()
//        println("Temp is below")
//        println(0.0f)
//        0.0f
//    }
//}
