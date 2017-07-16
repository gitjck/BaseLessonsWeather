package com.example.baselessonsweather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.baselessonsweather.adp.UserCityAdapter;
import com.example.baselessonsweather.adp.UserCityData;
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

public class UserCityActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public LocationClient mLocationClient;
    private BDLocationListener mListener;
    private Switch mSwitch;
    private TextView cancelTextView,delTextView;
    private ProgressDialog progressDialog;
    private ListView lsView;
    private UserCityAdapter adp;
    private List<UserCityData> itemDataList = new ArrayList<UserCityData>();
    private List<Integer> positionInt = new ArrayList<Integer>();
    private UserCityData delData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mListener);
        setContentView(R.layout.layout_user_city);
        //设置顶部导航
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_normal);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("城市管理");
        toolbar.setTitleTextColor(ContextCompat.getColor(UserCityActivity.this, R.color.city_list_text_color));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //添加城市
        Button btn = (Button) findViewById(R.id.my_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCityActivity.this,CitySelectActivity.class);
                startActivity(intent);
            }
        });
        //开关按纽 地图定位
        mSwitch = (Switch) findViewById(R.id.my_switch);
        mSwitch.setOnCheckedChangeListener(this);
        //初始化定位
        UserCity userCity = DataSupport.where("isLoction = ?", "1").findFirst(UserCity.class);
        if(userCity==null){
            weatherUI(null);
        }else{
            Weather myweather = Utility.hanleWeatherResponse(userCity.getWeatherData());
            weatherUI(myweather);
        }
        //数据列表
        cancelTextView = (TextView) findViewById(R.id.cancel_txt);
        delTextView = (TextView) findViewById(R.id.del_txt);
        /*
        UserCityData item1 = new UserCityData(1,"北海","305","小雨","26");
        UserCityData item2 = new UserCityData(1,"深圳","306","晴","36");
        UserCityData item3 = new UserCityData(1,"灵山","301","多云","30");
        UserCityData item4 = new UserCityData(1,"上海","302","大雨","22");
        itemDataList.add(item1);
        itemDataList.add(item2);
        itemDataList.add(item3);
        itemDataList.add(item4);
        */
        List<UserCity> dataList = DataSupport.where("isLoction = ?", "0").find(UserCity.class);
        for(UserCity data : dataList){
            Weather weather = Utility.hanleWeatherResponse(data.getWeatherData());
            itemDataList.add(new UserCityData(data.getId(),data.getCityName(),
                    weather.now.more.code,weather.now.more.info,weather.now.temperature));
        }
        lsView = (ListView) findViewById(R.id.my_listview);
        adp = new UserCityAdapter(UserCityActivity.this,R.layout.usercity_item,itemDataList);
        lsView.setAdapter(adp);
        lsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,View view,int position, long id) {
                adp.setEditStatus(true);
                adp.notifyDataSetChanged();
                lsView.setAdapter(adp);

                cancelTextView.setVisibility(View.VISIBLE);
                delTextView.setVisibility(View.VISIBLE);
                return true;
            }

        });
        //取消编辑
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                adp.setEditStatus(false);
                adp.notifyDataSetChanged();
                lsView.setAdapter(adp);

                cancelTextView.setVisibility(View.GONE);
                delTextView.setVisibility(View.GONE);
            }
        });
        //删除数据
        delTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {

                positionInt = adp.getPositionInt();
                if(positionInt.size()>0){
                    for(int position : positionInt){
                        delData = adp.getItem(position);
                        //数据操作
                        DataSupport.delete(UserCity.class, delData.getaid());
                        adp.remove(delData);
                    }
                    positionInt.clear();
                }
                adp.setEditStatus(false);
                adp.notifyDataSetChanged();
                lsView.setAdapter(adp);

                cancelTextView.setVisibility(View.GONE);
                delTextView.setVisibility(View.GONE);

            }
        });
    }
    /*
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(UserCityActivity.this);
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.my_switch:
                if(compoundButton.isChecked()){
                    myPermission();
                }else{
                    DataSupport.deleteAll(UserCity.class, "isLoction = ?", "1");
                    //界面更改
                    weatherUI(null);
                }
                break;
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
        showProgressDialog();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(mListener);
        mLocationClient.stop();
    }

    /*
    6.0以上系统危险权限需授权
     */
    private void myPermission(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(UserCityActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(UserCityActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(UserCityActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(UserCityActivity.this, permissions, 1);
        }else{
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用定位功能", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                String sCityName = location.getCity();
                City city = DataSupport.where("cityName = ?", sCityName).findFirst(City.class);
                requestWeather(city.getCityCode());
            }else{
                closeProgressDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwitch.setChecked(false);
                    }
                });
                Toast.makeText(UserCityActivity.this, "请检查网络是否畅通", Toast.LENGTH_SHORT).show();
            }
        }

        public void onConnectHotSpotMessage(String s, int i){}

    }
    /*获取天气数据*/
    public void requestWeather(final String weatherId) {
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
                        DataSupport.deleteAll(UserCity.class, "isLoction = ?", "1");
                        UserCity userCity = new UserCity();
                        userCity.setLoction(1);
                        userCity.setCityName(weather.basic.cityName);
                        userCity.setCityCode(weatherId);
                        userCity.setWeatherData(responseText);
                        userCity.save();
                        //调用界面更改
                        weatherUI(weather);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(UserCityActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    /*根据获取的天气数据修改界面UI*/
    private void weatherUI(Weather weather){
        ImageView noDataImg = (ImageView)findViewById(R.id.my_img1);
        ImageView isDataImg = (ImageView)findViewById(R.id.my_img2);
        TextView tepText = (TextView)findViewById(R.id.my_txt2);
        TextView cityText = (TextView)findViewById(R.id.my_txt1);
        if(weather==null){
            noDataImg.setVisibility(View.VISIBLE);
            isDataImg.setVisibility(View.GONE);
            tepText.setVisibility(View.GONE);
            cityText.setText("自动定位");
        }else{
            noDataImg.setVisibility(View.GONE);
            isDataImg.setVisibility(View.VISIBLE);
            tepText.setVisibility(View.VISIBLE);

            ApplicationInfo appInfo = getApplicationInfo();
            String picName = "w" + weather.now.more.code;
            int resID = getResources().getIdentifier(picName, "drawable", appInfo.packageName);
            isDataImg.setBackgroundResource(resID);

            tepText.setText(weather.now.more.info+"/"+weather.now.temperature);
            cityText.setText(weather.basic.cityName);
            mSwitch.setChecked(true);
        }
    }

}
