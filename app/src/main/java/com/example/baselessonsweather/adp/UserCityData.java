package com.example.baselessonsweather.adp;

/**
 * Created by jck on 2017/7/13.
 */

public class UserCityData {
    private int aid;
    private String cityName;
    private String weatherPic;
    private String weatherInfo;
    private String weatherTmp;

    public UserCityData(int aid,String cityName,String weatherPic,String weatherInfo,String weatherTmp){
        this.aid = aid;
        this.cityName = cityName;
        this.weatherPic = weatherPic;
        this.weatherInfo = weatherInfo;
        this.weatherTmp = weatherTmp;
    }

    public int getaid(){
        return aid;
    }
    public String getCityName(){
        return cityName;
    }
    public String getWeatherPic(){
        return weatherPic;
    }
    public String getWeatherInfo(){
        return weatherInfo;
    }
    public String getWeatherTmp(){
        return weatherTmp;
    }
}
