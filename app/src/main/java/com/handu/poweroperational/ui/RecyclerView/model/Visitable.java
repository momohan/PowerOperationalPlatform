package com.handu.poweroperational.ui.RecyclerView.model;

/**
 * Created by 柳梦  2017/1/16.
 *
 */

public abstract class Visitable implements IVisitable {

    @Override
    public int type() {
        return getType();
    }

    public abstract int getType();
}
