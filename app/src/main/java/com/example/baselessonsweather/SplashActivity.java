package com.example.baselessonsweather;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.example.baselessonsweather.db.City;
import com.example.baselessonsweather.weather.Rain;
import com.example.baselessonsweather.weather.WeatherView;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        WeatherView myWeatherView = (WeatherView) findViewById(R.id.weather_view);
        myWeatherView.setType(new Rain(this, myWeatherView));
        init();
    }

    /*
    禁用返回键
    */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if(keyCode == KeyEvent.KEYCODE_BACK){
             return  true;
         }
         return  super.onKeyDown(keyCode, event);
    }
    /*
    数据初始化
    */
    private void init(){
        City cityData = DataSupport.findLast(City.class);

        if (cityData != null){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    targetChange();
                }
            }, 2000);

        }else{
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    saveCity();
                }
            });
            thread.start();
        }

    }
     /*
    读取city.txt数据
    */
     private void saveCity(){
         List<City> cityArrayList = new ArrayList<City>();
         String str = "";
         InputStreamReader reader = null;
         InputStream is = null;
         BufferedReader bufferedReader = null;
         try {
             is = getAssets().open("city.txt");
             reader = new InputStreamReader(is);
             bufferedReader = new BufferedReader(reader);

             while ((str = bufferedReader.readLine()) != null) {
                 String[] cityArray = str.split(",");
                 City cityObj = new City();
                 cityObj.setCityCode(cityArray[0]);
                 cityObj.setCityName(cityArray[1]);
                 cityArrayList.add(cityObj);
             }
             DataSupport.saveAll(cityArrayList);
             targetChange();
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if(reader != null){
                 try {
                     reader.close();
                 }catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
     }

    /*
    页面跳转
    */
    private void targetChange(){
        Intent intent = new Intent(SplashActivity.this,CityWeatherActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}
