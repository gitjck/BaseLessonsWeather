package com.example.baselessonsweather.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by jck on 2017/7/8.
 */

public class WeatherView extends SurfaceView implements SurfaceHolder.Callback {
    /*
    天气类型接口
    */
    public interface WeatherType {
        void onDraw(Canvas canvas);
        void onSizeChanged(Context context, int w, int h);
    }

    private DrawThread myDrawThread;
    private SurfaceHolder sfHolder;
    private Context myContext;
    private WeatherType myType;
    private int myViewWidth;
    private int myViewHeight;

    public void setType(WeatherType type) {
        myType = type;
    }

    public int getViewWidth() {
        return myViewWidth;
    }

    public int getViewHeight() {
        return myViewHeight;
    }

    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        myContext = context;
        sfHolder = getHolder();
        sfHolder.addCallback(this); //onSizeChanged
        sfHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        myViewWidth = w;
        myViewHeight = h;
        if (myType != null) {
            myType.onSizeChanged(myContext, w, h);
        }
    }
    /*
    SurfaceHolder.Callback 接口开始
    */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        myDrawThread = new DrawThread();
        myDrawThread.setRunning(true);
        myDrawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myDrawThread.setRunning(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
     /*
    SurfaceHolder.Callback 接口结束
    */


    /*
    绘制线程类
    */
    private class DrawThread extends Thread {

        // 用来停止线程的标记
        private boolean isRunning = false;
        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            // 无限循环绘制
            while (isRunning) {
                if (myType != null && myViewWidth != 0 && myViewHeight != 0) {
                    canvas = sfHolder.lockCanvas();
                    if (canvas != null) {
                        myType.onDraw(canvas);
                        if (isRunning) {
                            sfHolder.unlockCanvasAndPost(canvas);
                        } else {
                            // 停止线程
                            break;
                        }
                        // sleep
                        SystemClock.sleep(1);
                    }
                }
            }
        }
    }
}
