package com.example.a.coolweather.util;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a.coolweather.MainActivity;
import com.example.a.coolweather.R;
import com.example.a.coolweather.WeatherActivity;
import com.example.a.coolweather.db.City;
import com.example.a.coolweather.db.County;
import com.example.a.coolweather.db.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by a on 2020/12/29.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;
    private TextView title_text;
    private Button back_button;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> datalist = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countryList;
    /**
     * 选中的省
     */
    private Province selecetProvince;
    /**
     * 选中的城市
     */
    private City selectCity;
    /**
     * 当前选中的级别
     */
    private int currentlevel;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        title_text = (TextView) view.findViewById(R.id.title_text);
        back_button = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.lise_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentlevel == LEVEL_PROVINCE) {
                    selecetProvince = provinceList.get(position);
                    queeryCities();
                } else if (currentlevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounties();
                }else if (currentlevel == LEVEL_COUNTRY) {
                    String weatherId = countryList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();}
                    else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity weatherActivity=(WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.SwipeRefresh.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }

                    }
                }

        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentlevel == LEVEL_COUNTRY) {
                    queeryCities();
                } else if (currentlevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }





    /**
     * 查询所有省，优先从数据库中查询，若是没有再到服务器上查询
     */
    private void queryProvinces() {
        title_text.setText("中国");
        back_button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);

        if (provinceList.size() > 0) {
            datalist.clear();
            for (Province province : provinceList) {
                datalist.add(province.getProvincename());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询省内的市，优先从数据库中查询，若是没有再到服务器上查询
     */
    private void queeryCities() {
        title_text.setText(selecetProvince.getProvincename());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selecetProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            datalist.clear();
            for (City city : cityList) {
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_CITY;
        } else {
            int provinceCode = selecetProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询市内的县，优先从数据库中查询，若是没有再到服务器上查询
     */
    private void queryCounties() {
        title_text.setText(selectCity.getCityName());
        back_button.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid=?", String.valueOf(selectCity.getId())).find(County.class);
        if (countryList.size() > 0){
            datalist.clear();
            for (County country : countryList) {
                datalist.add(country.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_COUNTRY;
        } else {
            int provinceCode = selecetProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "country");
        }
    }

    /**
     * 根据传入的地址在服务器上查询省市县数据
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.senOkHttpRequest(address, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsetext = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responsetext);
                } else if ("city".equals(type)) {
                    result = Utility.handCityRespone(responsetext, selecetProvince.getId());
                } else if ("country".equals(type)) {
                    result = Utility.handCountyRespone(responsetext, selectCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queeryCities();
                            } else if ("country".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void closeProgressDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


}



