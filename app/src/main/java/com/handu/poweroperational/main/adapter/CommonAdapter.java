package com.handu.poweroperational.main.adapter;

/**
 * @创建人 柳梦
 * @时间 2016/9/28 12:26
 * @说明 listView 适配器
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas = new ArrayList<>();
    protected final int mItemLayoutId;
    protected int posi;

    protected CommonAdapter(int itemLayoutId) {
        this.mItemLayoutId = itemLayoutId;
    }

    public CommonAdapter() {
        this.mItemLayoutId = 0;// LayoutInflater.from(mContext);
    }

    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
        // AddmDatas(mDatas);
    }

    public CommonAdapter(Context context, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mItemLayoutId = itemLayoutId;
    }

    public void ClearmDatas() {
        this.mDatas.clear();
        notifyDataSetChanged();
    }

    public void AddmDatas(List<T> mDatas) {
        this.mDatas.addAll(mDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        posi = position;
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();

    }

    public abstract void convert(ViewHolder helper, T item);

    protected ViewHolder getViewHolder(int position, View convertView,
                                       ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

    public LayoutInflater getmInflater() {
        return mInflater;
    }

    public void setmInflater(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<T> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    public int getmItemLayoutId() {
        return mItemLayoutId;
    }

    public int getPosi() {
        return posi;
    }

    public void setPosi(int posi) {
        this.posi = posi;
    }
}
