package com.example.baselessonsweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CityWeatherActivity extends AppCompatActivity {

    private ImageView addImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_city_weather);
        findView();
        bindEvent();
    }

    private void findView(){
        addImageView = (ImageView)findViewById(R.id.img_rightbottom);
    }

    private void bindEvent(){
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                Intent intent = new Intent(CityWeatherActivity.this,UserCityActivity.class);
                startActivity(intent);
            }
        });
    }
}
