package com.handu.poweroperational.ui.RecyclerView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.handu.poweroperational.ui.RecyclerView.holder.MultiTypeBaseRecyclerViewHolder;
import com.handu.poweroperational.ui.RecyclerView.model.IVisitable;
import com.handu.poweroperational.ui.RecyclerView.type.BaseTypeFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 柳梦  2016/12/30.
 */

public class MultiTypeRecyclerViewAdapter extends RecyclerView.Adapter<MultiTypeBaseRecyclerViewHolder> {

    private BaseTypeFactory baseTypeFactory;
    private List<IVisitable> models;

    public MultiTypeRecyclerViewAdapter(List<IVisitable> models, BaseTypeFactory factory) {
        this.models = models;
        this.baseTypeFactory = factory;

    }

    public MultiTypeRecyclerViewAdapter(BaseTypeFactory factory) {
        models = new ArrayList<>();
        this.baseTypeFactory = factory;
    }

    @Override
    public MultiTypeBaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = View.inflate(context, viewType, null);
        return baseTypeFactory.createViewHolder(viewType, itemView);
    }

    @Override
    public void onBindViewHolder(MultiTypeBaseRecyclerViewHolder holder, int position) {
        holder.setUpView(models.get(position), position, this);
    }

    @Override
    public int getItemCount() {
        if (null == models) {
            return 0;
        }
        return models.size();
    }

    /**
     * 填充数据,此操作会清除原来的数据
     *
     * @param list 要填充的数据
     * @return true:填充成功并调用刷新数据
     */
    public boolean fillList(List<IVisitable> list) {
        models.clear();
        boolean result = models.addAll(list);
        notifyDataSetChanged();
        return result;
    }

    /**
     * 追加集合数据
     *
     * @param list 要追加的集合数据
     * @return 追加成功并刷新
     */
    public boolean appendList(List<IVisitable> list) {
        boolean result = models.addAll(list);
        notifyDataSetChanged();
        return result;
    }

    /**
     * 清除所有数据
     */
    public void clearAll() {
        models.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return models.get(position).type();
    }

}
