package com.example.oldthank.DB;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {
    /**
     * cityId：记录当前县所需的市ID
     * countyName：记录县的名字
     * weatherId：记录县对应的天气ID
     * */
    private int id,cityId;
    private String countyName,weatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
