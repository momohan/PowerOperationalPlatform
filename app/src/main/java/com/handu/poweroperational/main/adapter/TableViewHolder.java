package com.handu.poweroperational.main.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：柳梦 2016/9/28 12:26
 * 邮箱：mobyq@qq.com
 * 说明:
 */
public class TableViewHolder {

    protected SparseArray<View> mViews;
    protected View mConvertView;

    public TableViewHolder() {

    }

    protected TableViewHolder(Context context, ViewGroup parent, int layoutId) {
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @return
     */
    public static TableViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new TableViewHolder(context, parent, layoutId);
        }
        return (TableViewHolder) convertView.getTag();
    }


    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }
}
