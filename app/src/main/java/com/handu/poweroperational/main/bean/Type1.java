package com.handu.poweroperational.main.bean;

import com.handu.poweroperational.main.adapter.TypeListFactory;
import com.handu.poweroperational.ui.RecyclerView.model.Visitable;

/**
 * Created by 柳梦 on 2017/1/16.
 */

public class Type1 extends Visitable {

    private String title;
    private String time;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Type1(String title, String time, String content) {
        this.title = title;
        this.time = time;
        this.content = content;
    }

    @Override
    public int getType() {
        return TypeListFactory.LAYOUT_ITEM_TYPE_1;
    }
}
