package com.example.a.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by a on 2020/12/29.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private  int provinceId;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String CityName() {
        return cityName;
    }
    public void setCityName(String provincename) {
        this.cityName = provincename;
    }
    public int getCityCode() {
        return cityCode;
    }
    public void setCityCode(int provinceCode) {
        this.cityCode = provinceCode;
    }
    public int getProvinceId() {
        return provinceId;
    }
    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

}
