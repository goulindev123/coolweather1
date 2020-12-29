package com.example.a.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by a on 2020/12/29.
 */

public class HttpUtil {
    public static void senOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
