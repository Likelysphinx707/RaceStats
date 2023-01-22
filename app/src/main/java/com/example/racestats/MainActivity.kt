package com.example.racestats

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.roundToInt


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
            uiAnimations(progressBar, speed, mph, timer, recordedTimes, yellowTimesBar,  recordedTimeOne, recordedTimeTwo, recordedTimeThree)

        }
    }
}

fun random(): Int {
    val rnds = (0..1).random() // generated random from 0 to 1 included

    if (rnds == 0) {
        return 1
    } else if (rnds == 1) {
        return -1
    }

    return 0
}


/**
 * Handles the Progress Bar Animation as the speed increases or decreases
 * @param progressBar takes the progressBar variable as a param so we can manipulate it in the UI
 */
fun uiAnimations(progressBar: ProgressBar, speed: TextView, mph: TextView, timer: TextView, times: TextView, yellowTimesBar: View, recTime1: TextView, recTime2: TextView, recTime3: TextView) {
    val startMilli = System.currentTimeMillis()
    val startMilliSeconds: Int = 0
    val startSeconds: Int = (startMilli / 1000 % 60).toInt()
    val startMinutes: Int = (startMilli / 1000 / 60).toInt()
    val startTime = startMinutes + startSeconds + startMilliSeconds

    var currentMilliSeconds: Int = 0
    var currentSeconds: Int = 0
    var currentMinutes: Int = 0
    var currentTime : Long = 0


    // we will increase the max after each milestone has been hit
    progressBar.max = 60
    // need ot set currentProgress val equal to current speed
    var currentProgress = 0


    while (currentProgress <= progressBar.max) {
        // Updates the mph
        speed.text = currentProgress.toString()
        // Updates the timer
        // calculate current time to display
        var currentMilli = System.currentTimeMillis()
        currentSeconds = (currentMilli / 1000 % 60).toInt() - startSeconds
        currentMinutes = (currentMilli / 1000 / 60).toInt() - startMinutes

        timer.text = "${currentMinutes}:${currentSeconds}.${currentMilliSeconds}"

        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
            .setDuration(100)
            .start()



        currentTime = System.currentTimeMillis()

        currentProgress += random()
    }

    // makes the UI flash yellow and displays times to milestones in the corner when 'currentProgress' == progressBar.max
    Timer().schedule(400) {
        speed.setTextColor(Color.parseColor("#FFE222"))
        mph.setTextColor(Color.parseColor("#FFE222"))
        timer.setTextColor(Color.parseColor("#FFE222"))
    }

    Timer().schedule(800) {
        speed.setTextColor(Color.parseColor("#FFFFFF"))
        mph.setTextColor(Color.parseColor("#FFFFFF"))
        timer.setTextColor(Color.parseColor("#FFFFFF"))
    }

    Timer().schedule(1200) {
        speed.setTextColor(Color.parseColor("#FFE222"))
        mph.setTextColor(Color.parseColor("#FFE222"))
        timer.setTextColor(Color.parseColor("#FFE222"))
    }

    Timer().schedule(1600) {
        speed.setTextColor(Color.parseColor("#FFFFFF"))
        mph.setTextColor(Color.parseColor("#FFFFFF"))
        timer.setTextColor(Color.parseColor("#FFFFFF"))
    }

    Timer().schedule(2000) {
        speed.setTextColor(Color.parseColor("#FFE222"))
        mph.setTextColor(Color.parseColor("#FFE222"))
        timer.setTextColor(Color.parseColor("#FFE222"))
    }

    Timer().schedule(2400) {
        speed.setTextColor(Color.parseColor("#FFFFFF"))
        mph.setTextColor(Color.parseColor("#FFFFFF"))
        timer.setTextColor(Color.parseColor("#FFFFFF"))
    }

    when (currentProgress) {
        60 -> {
            times.visibility = View.VISIBLE
            yellowTimesBar.visibility = View.VISIBLE
            recTime1.visibility = View.VISIBLE
            recTime1.text = "${currentMinutes}:${currentSeconds}.${currentMilliSeconds}"
        }
        100 -> recTime2.visibility = View.VISIBLE
        120 -> recTime3.visibility = View.VISIBLE
    }
}


//
//fun cpuTemperature(): Float {
//    val process: Process
//    return try {
//        process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp")
//        process.waitFor()
//        val reader = BufferedReader(InputStreamReader(process.inputStream))
//        val line: String = reader.readLine()
//        if (line != null) {
//            val temp = line.toFloat()
//            temp / 1000.0f
//        } else {
//            51.0f
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//        0.0f
//    }
//}
