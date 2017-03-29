package com.handu.poweroperational.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 柳梦 on 2017/3/2.
 * 常用的楼宇和台区
 */
@Entity
public class CommonBuildingAndDA {

    @Id(autoincrement = true)
    public Long id;//主键id
    public String name;//楼宇或台区名称
    public String value;//楼宇或台区值
    @Generated(hash = 1999932540)
    public CommonBuildingAndDA(Long id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }
    @Generated(hash = 1821499355)
    public CommonBuildingAndDA() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }

}
