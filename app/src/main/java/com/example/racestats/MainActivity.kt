package com.example.racestats

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
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

    private var targetTime: Int = 120
    private var methodRunning = false

    @RequiresApi(Build.VERSION_CODES.Q)
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


        // Come back to last once we have it running on the pi
//        println("printing temp?")
//        cpuTemperature(this) { result ->
//            println(result)
//        }

        // Allows the user to set the desired target time
        speed.setOnClickListener {
            val dialog = NumberPicker(this)
            dialog.minValue = 1
            dialog.maxValue = 200
            dialog.value = targetTime
            dialog.wrapSelectorWheel = false
            dialog.textColor = Color.WHITE // Set the text color to white
            dialog.setBackgroundColor(Color.BLACK) // Set the background color to black
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.setOnValueChangedListener { _, _, newVal ->
                targetTime = newVal
            }

            dialog.layoutParams = lp

            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Select Target Speed (mph)")
                .setView(dialog)
                .setPositiveButton("SET", null)
                .show()

            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.YELLOW)
            positiveButton.setBackgroundColor(Color.BLACK)
        }




        // This will handle our event when a user clicks the start or stop button
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
fun uiAnimations(targetTime: Int, progressBar: ProgressBar, speedTextView: TextView, timerTextView: TextView) {
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

/**
 * This function will set the CPU Temp in are UI and will handles all things associated with the CPU temp.
 */
fun cpuTemperature(activity: Activity, callback: (Float) -> Unit) {
    val permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE)

    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "This permission is required to access the CPU temperature.", Toast.LENGTH_LONG).show()
        }

        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Permission Request")
        alertDialogBuilder.setMessage("The app needs access to external storage to read the CPU temperature. Do you allow this permission?")
        alertDialogBuilder.setPositiveButton("Allow") { _, _ ->
//            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACTION_MANAGE_ALL_SIM_PROFILES_SETTINGS), 1)
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            getCpuTemperature(callback)
        }
        alertDialogBuilder.setNegativeButton("Deny") { dialog, _ ->
            dialog.dismiss()
            // set cpu temp to be invisible
        }
        alertDialogBuilder.show()
    } else {
        getCpuTemperature(callback)
    }
}

fun getCpuTemperature(callback: (Float) -> Unit) {
    return try {
        val reader = RandomAccessFile("/sys/devices/virtual/thermal/thermal_zone0/temp", "r")
        val line: String = reader.readLine()
        if (line != null) {
            val temp = line.toFloat()
            println(temp / 1000.0f)
            callback(temp / 1000.0f)
        } else {
            println(51.0f)
            callback(51.0f)
        }
    } catch (e: Exception) {
        println("about to look for temp")
        e.printStackTrace()
        println("Temp is below")
        println(0.0f)
        callback(0.0f)
    }
}
