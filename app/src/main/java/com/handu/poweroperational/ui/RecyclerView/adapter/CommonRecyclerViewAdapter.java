package com.handu.poweroperational.ui.RecyclerView.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.handu.poweroperational.ui.RecyclerView.holder.BaseRecyclerViewHolder;

import java.util.List;

/**
 * 作者：柳梦 2016/10/12 11:34
 * 邮箱：mobyq@qq.com
 * 说明: 通用的RecyclerViewAdapter
 */
public abstract class CommonRecyclerViewAdapter<T> extends BaseRecyclerViewAdapter<T, BaseRecyclerViewHolder> {

    private final int mItemLayoutId;

    public CommonRecyclerViewAdapter(Context context, int itemLayoutId) {
        super(context);
        this.mItemLayoutId = itemLayoutId;
    }

    public CommonRecyclerViewAdapter(Context context, int itemLayoutId, List<T> list) {
        super(context, list);
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public BaseRecyclerViewHolder createCustomViewHolder(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(parent, mItemLayoutId);
    }

    @Override
    public void bindCustomViewHolder(BaseRecyclerViewHolder holder, int position) {
        convert(holder, getItem(position), position);
    }

    @Override
    public int getCustomViewType(int position) {
        return 0;
    }

    public abstract void convert(BaseRecyclerViewHolder holder, T item, int position);
}
