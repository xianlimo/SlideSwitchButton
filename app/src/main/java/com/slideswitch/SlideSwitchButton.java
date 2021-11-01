package com.slideswitch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class SlideSwitchButton extends View {
    private boolean isOn = false;
    private float curX = 0;
    private float centerY; //y固定
    private float viewWidth;
    private float radius;
    private float lineStart; //直线段开始的位置
    private float lineEnd;   //直线段结束的位置
    private float lineWidth;
    private final int SCALE = 4; //控件长度为滑动的圆的半径的倍数

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private OnStateChangedListener onStateChangedListener;

    public SlideSwitchButton(Context context) {
        super(context);
    }

    public SlideSwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideSwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        curX = event.getX();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (curX > viewWidth / 2) {
                curX = lineEnd;
                if (!isOn) {
                    //只有状态发生改变才调用回调函数， 下同
                    if (null != onStateChangedListener) {
                        onStateChangedListener.onStateChanged(true);
                    }
                    isOn = true;
                }
            } else {
                curX = lineStart;
                if (isOn) {
                    if (null != onStateChangedListener) {
                        onStateChangedListener.onStateChanged(false);
                    }
                    isOn = false;
                }
            }
        }
        this.postInvalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*保持宽是高的SCALE / 2倍， 即圆的直径*/
        this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredWidth() * 2 / SCALE);
        viewWidth = this.getMeasuredWidth();
        radius = viewWidth / SCALE;
        lineWidth = radius * 2f; //直线宽度等于滑块直径
        curX = radius;
        centerY = this.getMeasuredWidth() / SCALE; //centerY为高度的一半
        lineStart = radius;
        lineEnd = (SCALE - 1) * radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*限制滑动范围*/
        curX = curX > lineEnd ? lineEnd : curX;
        curX = curX < lineStart ? lineStart : curX;

        /*划线*/
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        /*左边部分的线，绿色*/
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(lineStart, centerY, curX, centerY, mPaint);
        /*右边部分的线，灰色*/
        mPaint.setColor(Color.GRAY);
        canvas.drawLine(curX, centerY, lineEnd, centerY, mPaint);

        /*画圆*/
        /*画最左和最右的圆，直径为直线段宽度， 即在直线段两边分别再加上一个半圆*/
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(lineEnd, centerY, lineWidth / 2, mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(lineStart, centerY, lineWidth / 2, mPaint);
        /*圆形滑块*/
        mPaint.setColor(Color.LTGRAY);
        canvas.drawCircle(curX, centerY, radius, mPaint);

    }

    public void setOnStateChangedListener(OnStateChangedListener o) {
        this.onStateChangedListener = o;
    }

    public interface OnStateChangedListener {
        void onStateChanged(boolean state);
    }

}

