package com.example.a.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by a on 2020/12/29.
 */

public class Province extends DataSupport{
    private int id;
    private String provinceName;
    private int provinceCode;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getProvincename() {
        return provinceName;
    }
    public void setProvincename(String provincename) {
        this.provinceName = provincename;
    }
    public int getProvinceCode() {
        return provinceCode;
    }
    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
