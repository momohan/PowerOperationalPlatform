package com.handu.poweroperational.main.bean;

import com.handu.poweroperational.main.adapter.TypeListFactory;
import com.handu.poweroperational.ui.RecyclerView.model.Visitable;

/**
 * Created by 柳梦 on 2017/1/16.
 */

public class Type2 extends Visitable {

    private String title;
    private String time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Type2(String title, String time) {
        this.title = title;
        this.time = time;
    }

    @Override
    public int getType() {
        return TypeListFactory.LAYOUT_ITEM_TYPE_2;
    }
}
