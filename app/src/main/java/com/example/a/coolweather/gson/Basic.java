package com.example.a.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a on 2020/12/30.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public  update  update;
    public class update{
        @SerializedName("loc")
        public  String updatetime;
    }
}
