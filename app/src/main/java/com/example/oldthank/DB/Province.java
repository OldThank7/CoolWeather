package com.example.oldthank.DB;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {
    //每个实体类中都有对应的字段
    private int id;
    //记录省的名字
    private String provinceName;

    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
