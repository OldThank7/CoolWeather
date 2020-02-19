package com.example.oldthank.DB;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    /**
     * cityCode:市的代号
     * provinceId：记录当前市所需的省
     * cityName:记录市的名字
     * */
    private int id,cityCode,provinceId;
    private String cityName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
