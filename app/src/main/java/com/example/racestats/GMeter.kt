package com.example.racestats

//import android.os.Bundle
//import android.view.animation.AlphaAnimation
//import android.view.animation.Animation
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import kotlin.math.sin
//
//class GMeter : AppCompatActivity() {
//    private lateinit var gdot: ImageView
//    private var timeElapsed: Float = 0f
//    private var maxGForce: Float = 1.5f
//    private var scaleFactor: Int = 1000
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.gmeter)
//
//        gdot = findViewById(R.id.gdot)
//
//        // Start a thread to simulate sensor events and update the red dot position
//        Thread {
//            while (true) {
//                timeElapsed += 0.1f  // Simulated time increment
//                val gForceX = calculateGForce(timeElapsed, 1.5f)  // Simulated X-axis g-force
//                val gForceY = calculateGForce(timeElapsed, 1.5f)  // Simulated Y-axis g-force
//                runOnUiThread {
//                    updateGdotPosition(gForceX, gForceY)
//                }
//                Thread.sleep(100)  // Simulated sensor delay
//            }
//        }.start()
//    }
//
//    private fun calculateGForce(timeElapsed: Float, frequency: Float): Float {
//        // Simulate a periodic sine wave to create a changing g-force
//        // Adjust this logic based on your specific requirements for testing
//        val amplitude = 2.0f  // Adjust the amplitude of the sine wave
//        return amplitude * sin(2 * Math.PI * frequency * timeElapsed).toFloat()
//    }
//
//    private fun updateGdotPosition(gForceX: Float, gForceY: Float) {
//        // Calculate the translation based on the available space
//        val translationX =
//            gForceX * dpToPx(150) / maxGForce  // Adjust the factor based on the available space
//        val translationY =
//            gForceY * dpToPx(150) / maxGForce  // Adjust the factor based on the available space
//
//        // Set the translation on both X and Y axes
//        gdot.translationX = translationX
//        gdot.translationY = translationY
//    }
//
//    private fun dpToPx(dp: Int): Float {
//        val density = resources.displayMetrics.density
//        return dp * density
//    }
//}


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GmeterActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gdot: ImageView
    private var maxGForceRecorded: Float = 0.0f // Variable to store highest G-force

    private lateinit var accelGsTextView: TextView
    private lateinit var brakingGsTextView: TextView
    private lateinit var leftGsTextView: TextView
    private lateinit var rightGsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gmeter)

        gdot = findViewById(R.id.gdot)
        accelGsTextView = findViewById(R.id.AccelGs)
        brakingGsTextView = findViewById(R.id.BrakingGs)
        leftGsTextView = findViewById(R.id.LeftGs)
        rightGsTextView = findViewById(R.id.RightGs)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("G-Meter Error", "onResume: No Accelerometer Detected")
            // Handle the case where accelerometer is not available
            // You can show a message to the user or perform any other action
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val accelX = event.values[0]
            val accelY = event.values[1]

            val leftGs = calculateLeftGs(accelX)
            val rightGs = calculateRightGs(accelX)
            val accelGs = calculateAccelGs(accelY)
            val brakingGs = calculateBrakingGs(accelY)

            updateGdotPosition(leftGs, rightGs, accelGs, brakingGs)
            updateHighScore(leftGs) // Update high score with leftGs for demonstration

            // Update UI to display the G-forces
            leftGsTextView.text = "Left G's: $leftGs"
            rightGsTextView.text = "Right G's: $rightGs"
            accelGsTextView.text = "Acceleration G's: $accelGs"
            brakingGsTextView.text = "Braking G's: $brakingGs"
        }
    }

    private fun calculateLeftGs(accelX: Float): Float {
        // Calculate G-force in the negative x direction
        return accelX.coerceIn(0f, Float.MAX_VALUE)
    }

    private fun calculateRightGs(accelX: Float): Float {
        // Calculate G-force in the positive x direction
        return -accelX.coerceIn(Float.MIN_VALUE, 0f)
    }

    private fun calculateAccelGs(accelY: Float): Float {
        // Calculate G-force in the positive y direction (acceleration)
        return accelY.coerceIn(0f, Float.MAX_VALUE)
    }

    private fun calculateBrakingGs(accelY: Float): Float {
        // Calculate G-force in the negative y direction (braking)
        return -accelY.coerceIn(Float.MIN_VALUE, 0f)
    }

    private fun updateGdotPosition(leftGs: Float, rightGs: Float, accelGs: Float, brakingGs: Float) {
        // Set the position of the red dot based on G-forces
        // Adjust this logic based on your specific requirements and scaling
        val maxGForce = 1.5f  // Maximum g-force to display
        val scaleFactor = 1000  // Scale factor for dot movement

        // Calculate total G-force
        val totalGForce = kotlin.math.sqrt(leftGs * leftGs + rightGs * rightGs + accelGs * accelGs + brakingGs * brakingGs)
        val normalizedGForce = (totalGForce / maxGForce).coerceIn(0.0f, 1.0f)
        val translationY = normalizedGForce * scaleFactor

        gdot.translationY = translationY
    }

    private fun updateHighScore(gForce: Float) {
        // Update the high score if the current G-force is higher
        if (gForce > maxGForceRecorded) {
            maxGForceRecorded = gForce
            // Update UI to display the highest G-force
            // Assuming you have a TextView with the id 'highScoreTextView'
            findViewById<TextView>(R.id.highScoreTextView).text = "Highest G-force: $maxGForceRecorded"
        }
    }
}