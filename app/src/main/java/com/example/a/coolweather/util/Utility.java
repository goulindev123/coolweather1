package com.example.a.coolweather.util;

import android.text.TextUtils;

import com.example.a.coolweather.db.City;
import com.example.a.coolweather.db.County;
import com.example.a.coolweather.db.Province;
import com.example.a.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a on 2020/12/29.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceobjext = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvincename(provinceobjext.getString("name"));
                    province.setProvinceCode(provinceobjext.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handCityRespone(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityobjext = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityobjext.getString("name"));
                    city.setCityCode(cityobjext.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public static boolean handCountyRespone(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyobjext = allCounties.getJSONObject(i);
                   County county = new County();
                    county.setCountyName(countyobjext.getString("name"));
                    county.setWeatherId(countyobjext.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public static Weather handleWeatherResponse(String reponse){
        try {
            JSONObject jsonObject = new JSONObject(reponse);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weathercontent = jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(weathercontent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}