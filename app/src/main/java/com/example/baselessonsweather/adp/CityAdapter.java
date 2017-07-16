package com.example.baselessonsweather.adp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baselessonsweather.R;

import java.util.List;

/**
 * Created by jck on 2017/7/10.
 */

public class CityAdapter extends ArrayAdapter<CityData> {

    private int resourceId;

    public CityAdapter(Context context, int textViewResourceId, List<CityData> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityData cityData = getItem(position); // 获取当前项的city实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.cityName = (TextView) view.findViewById (R.id.textView_cityname);
            //viewHolder.cityCode = (TextView) view.findViewById (R.id.textView_citycode);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.cityName.setText(cityData.getCityName());
        //viewHolder.cityCode.setText(cityData.getCityCode());
        return view;
    }

    class ViewHolder {
        TextView cityName;
        //TextView cityCode;
    }
}
