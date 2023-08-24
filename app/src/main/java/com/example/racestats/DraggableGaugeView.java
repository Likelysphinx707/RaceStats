package com.example.racestats;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DraggableGaugeView extends View {
    private float lastTouchX;
    private float lastTouchY;

    public DraggableGaugeView(Context context) {
        super(context);
        init();
    }

    public DraggableGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialization code, if needed
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - lastTouchX;
                float deltaY = y - lastTouchY;

                // Calculate new position and update layout
                float newX = getX() + deltaX;
                float newY = getY() + deltaY;
                setX(newX);
                setY(newY);

                lastTouchX = x;
                lastTouchY = y;
                break;
        }
        return true;
    }
}
