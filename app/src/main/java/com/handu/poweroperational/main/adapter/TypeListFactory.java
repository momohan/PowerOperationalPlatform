package com.handu.poweroperational.main.adapter;

import android.view.View;

import com.handu.poweroperational.R;
import com.handu.poweroperational.ui.RecyclerView.holder.MultiTypeBaseRecyclerViewHolder;
import com.handu.poweroperational.ui.RecyclerView.type.BaseTypeFactory;

/**
 * Created by 柳梦 2016/12/30.
 */

public class TypeListFactory implements BaseTypeFactory {

    public static final int TYPE_RESOURCE_NORMAL = R.layout.layout_item_normal;
    public static final int LAYOUT_ITEM_TYPE_1 = R.layout.layout_item_type_1;
    public static final int LAYOUT_ITEM_TYPE_2 = R.layout.layout_item_type_2;

    @Override
    public MultiTypeBaseRecyclerViewHolder createViewHolder(int type, View itemView) {

        MultiTypeBaseRecyclerViewHolder holder = new NormalViewHolder(itemView);
        switch (type) {
            case TYPE_RESOURCE_NORMAL:
                holder = new NormalViewHolder(itemView);
                break;
            case LAYOUT_ITEM_TYPE_1:
                holder = new Type1ViewHolder(itemView);
                break;
            case LAYOUT_ITEM_TYPE_2:
                holder = new Type2ViewHolder(itemView);
                break;
        }
        return holder;
    }
}
