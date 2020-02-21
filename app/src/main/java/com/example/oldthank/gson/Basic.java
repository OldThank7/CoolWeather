package com.example.oldthank.gson;


import com.google.gson.annotations.SerializedName;

public class Basic {

    /**
     * 表示城市名
     * */
    @SerializedName("city")
    public String cityName;

    /**
     * 表示城市对应的天气ID
     * */
    @SerializedName("id")
    public String weatherId;

    /**
     * loc表示天气的更新时间
     * */
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
