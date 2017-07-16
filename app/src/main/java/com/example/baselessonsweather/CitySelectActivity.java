package com.example.baselessonsweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.baselessonsweather.adp.CityAdapter;
import com.example.baselessonsweather.adp.CityData;
import com.example.baselessonsweather.db.City;
import com.example.baselessonsweather.db.UserCity;
import com.example.baselessonsweather.gson.Weather;
import com.example.baselessonsweather.util.HttpUtil;
import com.example.baselessonsweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CitySelectActivity extends AppCompatActivity {

    private ListView lsView;
    private List<CityData> cityDataList = new ArrayList<CityData>();
    private CityAdapter adp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_city_select);
        //设置顶部导航
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_normal);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置搜索框样式
        SearchView mySearchView = (SearchView)findViewById(R.id.my_searchview);
        EditText searchEditText = (EditText)mySearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(CitySelectActivity.this, R.color.city_search_hint_color));

        //监听输入变化事件
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
                return true;
            }
        });
        //数据列表
        lsView = (ListView) findViewById(R.id.my_listview);
        adp = new CityAdapter(CitySelectActivity.this,R.layout.city_item,cityDataList);
        lsView.setAdapter(adp);
        //监听列表点击事件
        lsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CityData cityData = cityDataList.get(position);
                String cityCode = cityData.getCityCode();
                List<UserCity> userCityList = DataSupport.findAll(UserCity.class);
                if(userCityList!=null){
                    if(userCityList.size()>=5){
                        Toast.makeText(CitySelectActivity.this, "最多只能保存5个城市", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for(UserCity data : userCityList){
                        if(data.getCityCode()==cityCode){
                            Intent intent = new Intent(CitySelectActivity.this,UserCityActivity.class);
                            startActivity(intent);
                        }
                    }
                }
                requestWeather(cityCode);
            }
        });

    }
    /*
    搜索绑定数据
    */
    private void doSearch(String newText){
        if (newText != null && !newText.equals("")) {
            cityDataList.clear();
            List<City> cityArrayList = DataSupport.where("cityName like ?", "%" + newText + "%").find(City.class);
            for(City city : cityArrayList){
                cityDataList.add(new CityData(city.getCityName(),city.getCityCode()));
            }
            adp.notifyDataSetChanged();
            lsView.setSelection(0);
        }
    }
    /*获取天气数据*/
    public void requestWeather(final String weatherId) {
        showProgressDialog();
        String weatherUrl = Utility.weatherUrl(weatherId);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.hanleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        UserCity userCity = new UserCity();
                        userCity.setLoction(0);
                        userCity.setCityName(weather.basic.cityName);
                        userCity.setCityCode(weatherId);
                        userCity.setWeatherData(responseText);
                        userCity.save();

                        Intent intent = new Intent(CitySelectActivity.this,UserCityActivity.class);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(CitySelectActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    /*
    * 显示进度对话框
    */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(CitySelectActivity.this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /*
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
