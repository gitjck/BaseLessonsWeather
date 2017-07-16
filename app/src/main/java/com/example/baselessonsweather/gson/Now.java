package com.example.baselessonsweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jck on 2017/7/12.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

        public String code;

    }
}
