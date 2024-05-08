package com.hss01248.screenshoot;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * @Despciption todo
 * @Author hss
 * @Date 5/7/24 6:18 PM
 * @Version 1.0
 */
public class CustomCropView extends View {
    private Paint paint;

    public void setOriginalRect(Rect cropRect) {
        this.cropRect = cropRect;
        setMinimumWidth(cropRect.width());
        setMinimumHeight(cropRect.height());
        setMeasuredDimension(cropRect.width(),cropRect.height());
        postInvalidate();
    }

    private Rect cropRect; // 裁剪区域
    private final int TOUCH_SIZE = 50; // 可触摸区域的大小
    private int activePointerId = -1; // 当前活动指针ID
    private ActiveEdge activeEdge = null; // 当前活动的边

    private enum ActiveEdge {
        LEFT, TOP, RIGHT, BOTTOM, NONE
    }

    public CustomCropView(Context context) {
        super(context);
        init();
    }

    public CustomCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFF00FF); // 设置颜色为黑色
        paint.setStyle(Paint.Style.STROKE); // 风格为描边
        paint.setStrokeWidth(5); // 描边宽度
        cropRect = new Rect(0, 0, 500, 500); // 初始裁剪区域
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(cropRect, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activeEdge = determineTouchedEdge((int) event.getX(), (int) event.getY());
                if (activeEdge != ActiveEdge.NONE) {
                    activePointerId = event.getPointerId(0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerId(0) == activePointerId && activeEdge != ActiveEdge.NONE) {
                    updateCropRect((int) event.getX(), (int) event.getY());
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                activePointerId = -1;
                activeEdge = ActiveEdge.NONE;
                if (listener != null) {
                    listener.onCropRectUpdate(cropRect);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private ActiveEdge determineTouchedEdge(int x, int y) {
        // 这个方法用于判断触摸的位置属于哪一条边
        if (Math.abs(x - cropRect.left) < TOUCH_SIZE) return ActiveEdge.LEFT;
        if (Math.abs(x - cropRect.right) < TOUCH_SIZE) return ActiveEdge.RIGHT;
        if (Math.abs(y - cropRect.top) < TOUCH_SIZE) return ActiveEdge.TOP;
        if (Math.abs(y - cropRect.bottom) < TOUCH_SIZE) return ActiveEdge.BOTTOM;
        return ActiveEdge.NONE;
    }

    private void updateCropRect(int x, int y) {
        // 这个方法根据触摸移动更新裁剪区域
        switch (activeEdge) {
            case LEFT:
                cropRect.left = x;
                break;
            case RIGHT:
                cropRect.right = x;
                break;
            case TOP:
                cropRect.top = y;
                break;
            case BOTTOM:
                cropRect.bottom = y;
                break;
        }
    }

    // Listener to return the updated coordinates of crop rectangle
    private OnCropRectUpdateListener listener;

    public interface OnCropRectUpdateListener {
        void onCropRectUpdate(Rect rect);
    }

    public void setOnCropRectUpdateListener(OnCropRectUpdateListener listener) {
        this.listener = listener;
    }
}

