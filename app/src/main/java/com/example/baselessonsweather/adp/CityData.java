package com.example.baselessonsweather.adp;

/**
 * Created by Administrator on 2017/7/10.
 */

public class CityData {
    private String cityName;
    private String cityCode;

    public CityData(String cityName,String cityCode){
        this.cityName = cityName;
        this.cityCode = cityCode;
    }
    public String getCityName(){
        return cityName;
    }
    public String getCityCode(){
        return cityCode;
    }
}
