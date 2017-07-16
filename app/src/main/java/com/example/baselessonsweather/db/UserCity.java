package com.example.baselessonsweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by jck on 2017/7/7.
 */

public class UserCity extends DataSupport {
    private int id;
    private String cityName;
    private String cityCode;
    private int isLoction;
    private String weatherData;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getCityName(){
        return cityName;
    }
    public void setCityName(String cityName){
        this.cityName = cityName;
    }
    public String getCityCode(){
        return cityCode;
    }
    public void setCityCode(String cityCode){
        this.cityCode = cityCode;
    }
    public int getLoction(){
        return isLoction;
    }
    public void setLoction(int isLoction){
        this.isLoction = isLoction;
    }
    public String getWeatherData(){
        return weatherData;
    }
    public void setWeatherData(String weatherData){
        this.weatherData = weatherData;
    }
}
