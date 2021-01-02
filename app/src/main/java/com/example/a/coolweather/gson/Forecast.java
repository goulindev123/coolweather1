package com.example.a.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a on 2020/12/30.
 */

public class Forecast {
    public  String date;
    @SerializedName("tmp")
    public Temperture temperture;
    @SerializedName("cond")
    public More more;

    public class Temperture {
        public  String max;
        public String  min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
