package com.example.a.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a on 2020/12/30.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }

}
