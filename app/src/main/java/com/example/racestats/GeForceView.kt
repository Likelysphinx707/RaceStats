package com.example.racestats

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GeForceView : View {

    // Dot properties
    private val dotRadius = 15f
    private val dotPaint = Paint().apply {
        color = Color.RED
    }

    // Current forces
    private var leftRightForce = 0f
    private var upDownForce = 0f

    // Calibration offsets to reset forces to 0
    private var calibrationOffsetLeftRight = 0f
    private var calibrationOffsetUpDown = 0f

    private val trailPaint = Paint().apply {
        color = Color.parseColor("#C60000")
        alpha = 75 // Adjust the transparency here (0 to 255)
        style = Paint.Style.FILL
    }

    private val trailSize = 35
    private val trailDotRadius = dotRadius / 2 + 2  // Adjust the size of the trail dots here
    private val trailDots = mutableListOf<Pair<Float, Float>>()

    // Constructors
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

    // Drawing method
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate center coordinates
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

        // Draw the trail dots
        for (i in 0 until trailDots.size) {
            val trailDot = trailDots[i]
            val dotX = centerX + trailDot.first * circleRadius3
            val dotY = centerY - trailDot.second * circleRadius3

            val alpha = (255 - (i * (255 / trailSize))).coerceAtLeast(0)
            trailPaint.alpha = alpha

            canvas.drawCircle(dotX, dotY, trailDotRadius, trailPaint)
        }

        // Draw circles
        drawCircle(canvas, centerX, centerY, circleRadius1, 0.5f)
        drawCircle(canvas, centerX, centerY, circleRadius2, 1f)
        drawCircle(canvas, centerX, centerY, circleRadius3, 1.5f)

        // Draw dot based on forces
        val dotX = centerX + leftRightForce * circleRadius3
        val dotY = centerY - upDownForce * circleRadius3
        canvas.drawCircle(dotX, dotY, dotRadius, dotPaint)

        // Update trail dots
        trailDots.add(0, Pair(leftRightForce, upDownForce))
        if (trailDots.size > trailSize) {
            trailDots.removeAt(trailDots.size - 1)
        }

        // Display real-time values in Gs
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
        }

        val accelerationG = upDownForce * 1.5f  // Scale by the largest circle's radius
        val brakingG = if (upDownForce > 0) 0f else -upDownForce * 1.5f  // Scale by the largest circle's radius
        val leftForceG = leftRightForce * 1.5f  // Scale by the largest circle's radius
        val rightForceG = if (leftRightForce > 0) 0f else -leftRightForce * 1.5f  // Scale by the largest circle's radius

        canvas.drawText("Acceleration: ${String.format("%.2f G", accelerationG)}", padding, height - 150f, textPaint)
        canvas.drawText("Braking: ${String.format("%.2f G", brakingG)}", padding, 100f, textPaint)
        canvas.drawText("Left Force: ${String.format("%.2f G", leftForceG)}", width - 350f, height / 2f, textPaint)
        canvas.drawText("Right Force: ${String.format("%.2f G", rightForceG)}", padding, height / 2f, textPaint)
    }

    // Calibration method to reset forces to 0
    fun calibrate() {
        calibrationOffsetLeftRight += -leftRightForce
        calibrationOffsetUpDown += -upDownForce
    }

    // Method to update forces
    fun updateForces(leftRightForce: Float, upDownForce: Float) {
        this.leftRightForce = leftRightForce + calibrationOffsetLeftRight
        this.upDownForce = upDownForce + calibrationOffsetUpDown
        invalidate()
    }

    // Draw a circle with a label
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