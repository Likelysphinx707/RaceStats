package com.example.racestats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomProgressBar extends ProgressBar {
    private Paint marksPaint;
    private TextView coolantTemperatureTextOverlay;
    private int coolantTemperature;

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        marksPaint = new Paint();
        marksPaint.setColor(0xFFFF0000); // Red color
        marksPaint.setStrokeWidth(5); // 5dp width
        // Other paint attributes like style, etc., can be set here.
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Add code to draw marks at specific progress points
        drawMarks(canvas);
    }

    private void drawMarks(Canvas canvas) {
        // Define the progress points where you want to draw marks
        int[] progressPoints = {25, 50, 75, 106};

        int width = getWidth();
        int height = getHeight();

        for (int progress : progressPoints) {
            float xPos = width * progress / getMax();
            float yPos = height / 2f; // Center vertically

            // Draw a mark at the specified position with the calculated markHeight
            canvas.drawLine(xPos, yPos - height / 2f, xPos, yPos + height / 2f, marksPaint);
        }

        // set textview just in front of filled in portion of progress bar
        coolantTemperatureTextOverlay.setX(width * coolantTemperature / getMax() + 110);
        coolantTemperatureTextOverlay.setY(height / 2f);
        coolantTemperatureTextOverlay.setText(coolantTemperature + "°C");
    }

    public void setCoolantTemperature(int temperature) {
        coolantTemperature = temperature;
        invalidate(); // Redraw the progress bar
    }

    public void setCoolantTemperatureTextOverlay(TextView textView) {
        coolantTemperatureTextOverlay = textView;
    }
}