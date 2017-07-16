package com.example.baselessonsweather.util;

import android.text.TextUtils;

import com.example.baselessonsweather.db.City;
import com.example.baselessonsweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jck on 2017/7/6.
 */

public class Utility {

    /*
   处理天气JSON数据
   */
    public static Weather hanleWeatherResponse(String response){
        if (!TextUtils.isEmpty(response)) {
            try{
                JSONObject jsonObj = new JSONObject(response);
                JSONArray jsonArr = jsonObj.getJSONArray("HeWeather5");
                String weatherContent = jsonArr.getJSONObject(0).toString();
                return new Gson().fromJson(weatherContent,Weather.class);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String weatherUrl(String weatherId){
        String key = "3fe48ea73ddc4ba6945ee29fb7b33d08";
        String url = "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key="+key;
        return url;
    }
}
