package com.handu.poweroperational.ui.RecyclerView.holder;


import android.view.View;

import com.handu.poweroperational.ui.RecyclerView.adapter.MultiTypeRecyclerViewAdapter;


/**
 * Created by 柳梦  2017/1/3.
 */

public abstract class MultiTypeBaseRecyclerViewHolder<T> extends BaseRecyclerViewHolder {

    public MultiTypeBaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setUpView(T model, int position, MultiTypeRecyclerViewAdapter adapter);
}
