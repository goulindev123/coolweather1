package com.example.a.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a.coolweather.gson.Forecast;
import com.example.a.coolweather.gson.Weather;
import com.example.a.coolweather.util.HttpUtil;
import com.example.a.coolweather.util.Utility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
public SwipeRefreshLayout SwipeRefresh;
    private  String mWeatherId;
    private ScrollView weatherlayout;
    private TextView titlecity;
    private TextView tilteUpdatedtime;
    private TextView degreetext;
    private TextView weatherinfotext;
    private LinearLayout forecastlayout;
public DrawerLayout drawerLayout;
    private Button navbutton;
    private TextView aqitext;
    private TextView pm25text;
    private TextView comforttext;
    private TextView carwashtext;
    private TextView sporttext;
private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各控件。

        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        navbutton= (Button) findViewById(R.id.nav_button);
        SwipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        weatherlayout = (ScrollView) findViewById(R.id.weather_layout);
        titlecity = (TextView) findViewById(R.id.title_city);
        tilteUpdatedtime = (TextView) findViewById(R.id.title_updata_time);
        degreetext = (TextView) findViewById(R.id.degree_text);
        weatherinfotext = (TextView) findViewById(R.id.weather_info_text);
        forecastlayout = (LinearLayout) findViewById(R.id.forecast_layout);
       bingPicImg= (ImageView) findViewById(R.id.bing_pic_img);
        aqitext = (TextView) findViewById(R.id.aqi_text);
        pm25text = (TextView) findViewById(R.id.pm25_text);
        comforttext = (TextView) findViewById(R.id.comfort_text);
        carwashtext = (TextView) findViewById(R.id.car_wash_text);
        sporttext = (TextView) findViewById(R.id.sport_text);
navbutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
});
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String binfpc=prefs.getString("bing_pic",null);
        if (binfpc!=null){
            Glide.with(this).load(binfpc).into(bingPicImg);
        }else {
            loadBingpic();
        }
        String weatherStrings = prefs.getString("weather", null);
        if (weatherStrings != null) {
            //有缓存时直接显示数据。
            Weather weather = Utility.handleWeatherResponse(weatherStrings);
           mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherlayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
            mWeatherId=getIntent().getStringExtra("weather_id");
            requestWeather(mWeatherId);
        }
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            public void  onRefresh(){
                requestWeather(mWeatherId);
            }
        });


    }

    /**
     * 根据天气ID请求城市天气信息
     *
     * @param weatherId
     */
   public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=484d9d657d9e4c7eab7d50493c3cdb59";
        HttpUtil.senOkHttpRequest(weatherUrl, new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final String responsetext = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responsetext);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responsetext);
                            editor.apply();
                            mWeatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气新信息失败", Toast.LENGTH_SHORT).show();
                        }
                        SwipeRefresh.setRefreshing(false);
                    }
                });
                loadBingpic();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气新信息失败", Toast.LENGTH_SHORT).show();
                        SwipeRefresh.setRefreshing(false);
                    }

                });
            }
        });
    }
private void loadBingpic(){
    String requestBingPic="http://guolin.tech/api/bing_pic";
    HttpUtil.senOkHttpRequest(requestBingPic, new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
        final  String bingPic=response.body().string();
            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("bing_pic",bingPic);
            editor.apply();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                }
            });
        }
    });
}

    private void showWeatherInfo(Weather weather) {
        String city_name = weather.basic.cityName;
        String update_name = weather.basic.update.updatetime;
        String degree = weather.now.temperature + "摄氏度";
        String weatherinfo = weather.now.more.info;
        titlecity.setText(city_name);
        tilteUpdatedtime.setText(update_name);
        degreetext.setText(degree);
        weatherinfotext.setText(weatherinfo);
        forecastlayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastlayout, false);
            TextView datatext = (TextView) view.findViewById(R.id.data_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.main_text);
            datatext.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperture.max);
            minText.setText(forecast.temperture.min);
            forecastlayout.addView(view);
        }
        if (weather.aqi != null) {
            aqitext.setText(weather.aqi.city.aqi);
            pm25text.setText(weather.aqi.city.pm25);
        }
        String comfoort = "舒适度:" + weather.suggestion.comfort.info;
        String carwash = "洗车指数:" + weather.suggestion.carWash.info;
        String sport = "运动建议" + weather.suggestion.sport.info;
        comforttext.setText(comfoort);
        carwashtext.setText(carwash);
        sporttext.setText(sport);
        weatherlayout.setVisibility(View.VISIBLE);
    }
}




