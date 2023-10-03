package com.example.racestats

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sin

class GMeter : AppCompatActivity() {
    private lateinit var gdot: ImageView
    private var timeElapsed: Float = 0f
    private var maxGForce: Float = 1.5f
    private var scaleFactor: Int = 1005

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gmeter)

        gdot = findViewById(R.id.gdot)

        // Start a thread to simulate sensor events and update the red dot position
        Thread {
            while (true) {
                timeElapsed += 0.1f  // Simulated time increment
                val gForceX = calculateGForce(timeElapsed, 0.5f)  // Simulated X-axis g-force
                val gForceY = calculateGForce(timeElapsed, 0.5f)  // Simulated Y-axis g-force
                runOnUiThread {
                    updateGdotPosition(gForceX, gForceY)
                }
                Thread.sleep(100)  // Simulated sensor delay
            }
        }.start()
    }

    private fun calculateGForce(timeElapsed: Float, frequency: Float): Float {
        // Simulate a periodic sine wave to create a changing g-force
        // Adjust this logic based on your specific requirements for testing
        val amplitude = 2.0f  // Adjust the amplitude of the sine wave
        return amplitude * sin(2 * Math.PI * frequency * timeElapsed).toFloat()
    }

    private fun updateGdotPosition(gForceX: Float, gForceY: Float) {
        // Calculate the translation based on the available space
        val translationX = gForceX * dpToPx(150) / maxGForce  // Adjust the factor based on the available space
        val translationY = gForceY * dpToPx(150) / maxGForce  // Adjust the factor based on the available space

        // Set the translation on both X and Y axes
        gdot.translationX = translationX
        gdot.translationY = translationY
    }

    private fun dpToPx(dp: Int): Float {
        val density = resources.displayMetrics.density
        return dp * density
    }

}



//    private lateinit var sensorManager: SensorManager
//    private lateinit var accelerometer: Sensor
//    private lateinit var gdot: ImageView

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.gmeter)
//
//        gdot = findViewById(R.id.gdot)
//
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//
//        if (accelerometer != null) {
//            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
//        } else {
//            Log.d("G-Meter Error", "onResume: No Accelerometer Detected")
//            // Handle the case where accelerometer is not available
//            // You can show a message to the user or perform any other action
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        sensorManager.unregisterListener(this)
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // Do nothing
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
//            val gForce = calculateGForce(event.values[0], event.values[1], event.values[2])
//            updateGdotPosition(gForce)
//        }
//    }
//
//    private fun calculateGForce(x: Float, y: Float, z: Float): Float {
//        // Calculate the total g-force
//        return kotlin.math.sqrt(x * x + y * y + z * z)
//    }
//
//    private fun updateGdotPosition(gForce: Float) {
//        // Set the position of the red dot based on g-force
//        // Adjust this logic based on your specific requirements and scaling
//        val maxGForce = 5.0f  // Maximum g-force to display
//        val scaleFactor = 1000  // Scale factor for dot movement
//
//        val normalizedGForce = (gForce / maxGForce).coerceIn(0.0f, 1.0f)
//        val translationY = normalizedGForce * scaleFactor
//
//        gdot.translationY = translationY
//    }
//
//}