package com.handu.poweroperational.main.adapter;

import android.view.View;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.bean.Normal;
import com.handu.poweroperational.ui.RecyclerView.adapter.MultiTypeRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.holder.MultiTypeBaseRecyclerViewHolder;

/**
 * Created by 柳梦 on 2017/1/3.
 */

public class NormalViewHolder extends MultiTypeBaseRecyclerViewHolder<Normal> {

    NormalViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(Normal model, int position, MultiTypeRecyclerViewAdapter adapter) {
        TextView textView = getView(R.id.tv_normal_title);
        textView.setText(model.getTitle());
    }
}
