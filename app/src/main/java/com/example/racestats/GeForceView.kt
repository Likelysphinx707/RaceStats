package com.example.racestats

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.View

class GeForceView : View {

    private val dotRadius = 15f
    private val dotPaint = Paint().apply {
        color = Color.RED
    }

    private var leftRightForce = 0f
    private var upDownForce = 0f
    private var calibrationOffsetLeftRight = 0f
    private var calibrationOffsetUpDown = 0f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Initialization code if needed
        leftRightForce = 0f
        upDownForce = 0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 20.dpToPx() // Convert dp to pixels

        val centerX = (width - padding * 2) / 2f + padding
        val centerY = (height - padding * 2) / 2f + padding

        // Adjusted radii for better visibility
        val circleRadius1 = (width - padding * 4) / 10f
        val circleRadius2 = (width - padding * 4) / 5f // 50% of above value
        val circleRadius3 = (width - padding * 4) / 3.5f // 73% of above value

        // Draw circles
        drawCircle(canvas, centerX, centerY, circleRadius1, 0.5f)
        drawCircle(canvas, centerX, centerY, circleRadius2, 1f)
        drawCircle(canvas, centerX, centerY, circleRadius3, 1.5f)

        // Draw dot based on forces
        val dotX = centerX + leftRightForce * circleRadius3
        val dotY = centerY - upDownForce * circleRadius3
        canvas.drawCircle(dotX, dotY, dotRadius, dotPaint)

        // Display real-time values
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
        }

        canvas.drawText("Acceleration: $upDownForce", padding, height - 150f, textPaint)
        canvas.drawText("Braking: ${-upDownForce}", padding, 100f, textPaint)
        canvas.drawText("Left Force: $leftRightForce", width - 350f, height / 2f, textPaint)
        canvas.drawText("Right Force: ${-leftRightForce}", padding, height / 2f, textPaint)
    }

    fun calibrate() {
        calibrationOffsetLeftRight += -leftRightForce
        calibrationOffsetUpDown += -upDownForce
    }

    fun updateForces(leftRightForce: Float, upDownForce: Float) {
        this.leftRightForce = leftRightForce + calibrationOffsetLeftRight
        this.upDownForce = upDownForce + calibrationOffsetUpDown
        invalidate()
    }

    private fun drawCircle(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, label: Float) {
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeWidth = 5f
        }

        canvas.drawCircle(centerX, centerY, radius, paint)

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
        }

        val textX = centerX - 10f
        val textY = centerY + radius + 40f

        canvas.drawText("$label G", textX, textY, textPaint)
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }
}