package com.handu.poweroperational.main.bean;

/**
 * Created by 柳梦 on 2016/12/6.
 */

public class MaterialsModel {

    public String id;//物资id
    public String text;//物资名称
    public int count;//已选数量

    public MaterialsModel(String id, String text) {
        this.id = id;
        this.text = text;
    }
}
