package com.example.baselessonsweather;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import org.litepal.LitePalApplication;

import java.lang.reflect.Field;

/**
 * Created by jck on 2017/7/15.
 */

public class MyApplication extends Application {

    private static Context mContext;
    private static Typeface typeFace;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        LitePalApplication.initialize(mContext);
        setTypeface();
    }

    public static Context getContext(){
        return mContext;
    }

    private void setTypeface(){
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/nubia_backup.ttf");
        try{
            Field field_3 = Typeface.class.getDeclaredField("SANS_SERIF");
            field_3.setAccessible(true);
            field_3.set(null, typeFace);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
