package com.example.racestats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CustomProgressBar extends ProgressBar {
    private Paint marksPaint;
    private float markHeight;

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        marksPaint = new Paint();
        marksPaint.setColor(0xFFFF0000); // Red color
        marksPaint.setStrokeWidth(5); // 40dp width
        // Other paint attributes like style, etc., can be set here.

        markHeight = getHeight(); // Set mark height initially to the progress bar's height
    }

    @Override
    protected synchronized void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        markHeight = h; // Update mark height when the progress bar size changes
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Add code to draw marks at specific progress points
        drawMarks(canvas);
    }

    private void drawMarks(Canvas canvas) {
        // Define the progress points where you want to draw marks
        int[] progressPoints = {25, 50, 75, 106, 110};

        int width = getWidth();

        for (int progress : progressPoints) {
            float xPos = width * progress / getMax();
            float yPos = getHeight() / 2f; // Center vertically

            // Draw a mark at the specified position with the calculated markHeight
            canvas.drawLine(xPos, yPos - markHeight / 2f, xPos, yPos + markHeight / 2f, marksPaint);
        }
    }
}
