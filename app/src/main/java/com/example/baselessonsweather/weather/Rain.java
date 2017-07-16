package com.example.baselessonsweather.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.example.baselessonsweather.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jck on 2017/7/8.
 */

public class Rain implements WeatherView.WeatherType {

    private int mWidth;
    private int mHeight;
    private Drawable mBackground;
    private Context mContext;
    private RainHolder r;
    // 雨滴集合
    private ArrayList<RainHolder> mRains;
    // 画笔
    private Paint mPaint;


    public Rain(Context context, WeatherView wView) {
        mContext = context;
        mWidth = wView.getViewWidth();
        mHeight = wView.getViewHeight();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        // 这里雨滴的宽度统一为3
        mPaint.setStrokeWidth(3);
        mRains = new ArrayList<>();
    }

    @Override
    public void onSizeChanged(Context context, int w, int h) {
        mWidth = w;
        mHeight = h;
        mBackground = mContext.getResources().getDrawable(R.drawable.rain_sky_night);
        mBackground.setBounds(0, 0, mWidth, mHeight);
        for (int i = 0; i < 60; i++) {
            RainHolder rain = new RainHolder(
                    getRandom(1, w),
                    getRandom(1, h),
                    getRandom(dp2px(9), dp2px(15)),
                    getRandom(dp2px(5), dp2px(9)),
                    getRandom(20, 100)
            );
            mRains.add(rain);
        }
    }
    @Override
    public void onDraw(Canvas canvas) {
        clearCanvas(canvas);
        // 画背景
        mBackground.draw(canvas);
        // 画出集合中的雨点
        for (int i = 0; i < mRains.size(); i++) {
            r = mRains.get(i);
            mPaint.setAlpha(r.a);
            canvas.drawLine(r.x, r.y, r.x, r.y + r.l, mPaint);
        }
        // 将集合中的点按自己的速度偏移
        for (int i = 0; i < mRains.size(); i++) {
            r = mRains.get(i);
            r.y += r.s;
            if (r.y > mHeight) {
                r.y = -r.l;
            }
        }
    }

    protected void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    /**
     * 获取给定两数之间的一个随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 介于最大值和最小值之间的一个随机数
     */
    protected int getRandom(int min, int max) {
        if (max < min) {
            return 1;
        }
        return min + new Random().nextInt(max - min);
    }

    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    private class RainHolder {
        /**
         * 雨点 x 轴坐标
         */
        int x;
        /**
         * 雨点 y 轴坐标
         */
        int y;
        /**
         * 雨点长度
         */
        int l;
        /**
         * 雨点移动速度
         */
        int s;
        /**
         * 雨点透明度
         */
        int a;

        public RainHolder(int x, int y, int l, int s, int a) {
            this.x = x;
            this.y = y;
            this.l = l;
            this.s = s;
            this.a = a;
        }

    }
}
