package com.example.baselessonsweather.adp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baselessonsweather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jck on 2017/7/13.
 */

public class UserCityAdapter extends ArrayAdapter<UserCityData> {
    private int resourceId;
    private boolean isEdit = false;
    private List<Integer> positionInt = new ArrayList<Integer>();

    public void setEditStatus(boolean editStatus){
        this.isEdit = editStatus;
    }

    public List<Integer> getPositionInt(){
        return this.positionInt;
    }


    public UserCityAdapter(Context context, int textViewResourceId, List<UserCityData> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        UserCityData itemData = getItem(position); // 获取当前项的数据实例
        View view;
        UserCityAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            if(isEdit==true){
                view = LayoutInflater.from(getContext()).inflate(R.layout.usercity_item_edit, parent, false);
            }else{
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            }

            viewHolder = new UserCityAdapter.ViewHolder();
            viewHolder.weather_txt = (TextView) view.findViewById (R.id.weather_txt);
            viewHolder.city_txt = (TextView) view.findViewById (R.id.city_txt);
            viewHolder.left_img = (ImageView) view.findViewById (R.id.left_img);

            if(isEdit==true){
                viewHolder.right_checkbox = (CheckBox)view.findViewById (R.id.right_checkbox);
                viewHolder.right_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            positionInt.add(position);
                        }else{
                            for (int i = 0; i < positionInt.size(); i++) {
                                if (positionInt.get(i) == position) {
                                    positionInt.remove(i);
                                }
                            }
                        }
                    }
                });
            }else{
                viewHolder.right_img = (ImageView) view.findViewById (R.id.right_img);
            }

            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (UserCityAdapter.ViewHolder) view.getTag(); // 重新获取ViewHolder
        }

        viewHolder.weather_txt.setText(itemData.getWeatherInfo()+"/"+itemData.getWeatherTmp() + "℃");
        viewHolder.city_txt.setText(itemData.getCityName());

        ApplicationInfo appInfo = getContext().getApplicationInfo();
        String picName = "w" + itemData.getWeatherPic();
        int resID = getContext().getResources().getIdentifier(picName, "drawable", appInfo.packageName);
        viewHolder.left_img.setBackgroundResource(resID);
        return view;
    }

    class ViewHolder {
        ImageView left_img;
        TextView weather_txt;
        TextView city_txt;
        ImageView right_img;
        CheckBox right_checkbox;
    }
}
