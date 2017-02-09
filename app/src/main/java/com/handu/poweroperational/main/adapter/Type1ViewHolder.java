package com.handu.poweroperational.main.adapter;

import android.view.View;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.bean.Type1;
import com.handu.poweroperational.ui.RecyclerView.adapter.MultiTypeRecyclerViewAdapter;
import com.handu.poweroperational.ui.RecyclerView.holder.MultiTypeBaseRecyclerViewHolder;

/**
 * Created by 柳梦 on 2017/1/3.
 */

public class Type1ViewHolder extends MultiTypeBaseRecyclerViewHolder<Type1> {

    Type1ViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(Type1 model, int position, MultiTypeRecyclerViewAdapter adapter) {
        TextView textView = getView(R.id.tv_type1_title);
        textView.setText(model.getTitle());
        textView = getView(R.id.tv_type1_time);
        textView.setText(model.getTime());
        textView = getView(R.id.tv_type1_content);
        textView.setText(model.getContent());
    }
}
