package com.handu.poweroperational.main.adapter;

import android.view.View;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.bean.Type2;
import com.handu.poweroperational.ui.RecyclerView.adapter.MultiTypeRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.holder.MultiTypeBaseRecyclerViewHolder;

/**
 * Created by 柳梦 on 2017/1/3.
 */

public class Type2ViewHolder extends MultiTypeBaseRecyclerViewHolder<Type2> {

    Type2ViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(Type2 model, int position, MultiTypeRecyclerViewAdapter adapter) {
        TextView textView = getView(R.id.tv_type2_title);
        textView.setText(model.getTitle());
        textView = getView(R.id.tv_type2_time);
        textView.setText(model.getTime());
    }
}
