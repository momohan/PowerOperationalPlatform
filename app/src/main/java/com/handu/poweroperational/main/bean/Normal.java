package com.handu.poweroperational.main.bean;

import com.handu.poweroperational.ui.RecyclerView.model.Visitable;
import com.handu.poweroperational.main.adapter.TypeListFactory;

/**
 * Created by 柳梦  2016/12/30.
 */

public class Normal extends Visitable {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Normal(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return TypeListFactory.TYPE_RESOURCE_NORMAL;
    }
}
