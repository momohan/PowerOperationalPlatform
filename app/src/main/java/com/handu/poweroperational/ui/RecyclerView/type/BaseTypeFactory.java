package com.handu.poweroperational.ui.RecyclerView.type;

import android.view.View;

import com.handu.poweroperational.ui.RecyclerView.holder.MultiTypeBaseRecyclerViewHolder;

/**
 * Created by 柳梦  2016/12/30.
 */

public interface BaseTypeFactory {
    MultiTypeBaseRecyclerViewHolder createViewHolder(int type, View itemView);
}
