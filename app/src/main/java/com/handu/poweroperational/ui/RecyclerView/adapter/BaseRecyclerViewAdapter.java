package com.handu.poweroperational.ui.RecyclerView.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @ 作者:柳梦
 * @ 创建时间:2016/8/26 10:35
 * @ 说明: 基础的Adapter
 * @ 返回:
 * @ 参数:
 */

public abstract class BaseRecyclerViewAdapter<M, VH extends BaseRecyclerViewHolder> extends AbsAdapter<M, VH> {

    private List<M> dataList;

    public BaseRecyclerViewAdapter(Context context) {
        super(context);
        this.dataList = new ArrayList<>();
    }

    public BaseRecyclerViewAdapter(Context context, List<M> list) {
        super(context);
        this.dataList = new ArrayList<>();
        this.dataList.addAll(list);
    }

    /**
     * 填充数据,此操作会清除原来的数据
     *
     * @param list 要填充的数据
     * @return true:填充成功并调用刷新数据
     */
    public boolean fillList(List<M> list) {
        dataList.clear();
        boolean result = dataList.addAll(list);
        notifyDataSetChanged();
        return result;
    }

    /**
     * 清除所有数据
     */
    public void clearAll() {
        dataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 追加一条数据
     *
     * @param data 要追加的数据
     * @return true:追加成功并刷新界面
     */
    public boolean appendItem(M data) {
        boolean result = dataList.add(data);
        if (getHeaderExtraViewCount() == 0) {
            notifyItemInserted(dataList.size() - 1);
        } else {
            notifyItemInserted(dataList.size());
        }
        return result;
    }

    /**
     * 指定位置追加一条数据
     *
     * @param data 要追加的数据
     * @return true:追加成功并刷新界面
     */
    public void appendItem(M data, int position) {
        if (position >= 0 && position <= dataList.size()) {
            dataList.add(position, data);
            if (getHeaderExtraViewCount() == 0) {
                notifyItemInserted(dataList.size() - 1);
            } else {
                notifyItemInserted(dataList.size());
            }
        }
    }

    /**
     * 追加集合数据
     *
     * @param list 要追加的集合数据
     * @return 追加成功并刷新
     */
    public boolean appendList(List<M> list) {
        boolean result = dataList.addAll(list);
        notifyDataSetChanged();
        return result;
    }

    /**
     * 在最顶部前置数据
     *
     * @param data 要前置的数据
     */
    public void proposeItem(M data) {
        dataList.add(0, data);
        if (getHeaderExtraViewCount() == 0) {
            notifyItemInserted(0);
        } else {
            notifyItemInserted(getHeaderExtraViewCount());
        }
    }

    /**
     * 在顶部前置数据集合
     *
     * @param list 要前置的数据集合
     */
    public void proposeList(List<M> list) {
        dataList.addAll(0, list);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public final int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (footerView != null && position == dataList.size() + getHeaderExtraViewCount()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return getCustomViewType(position);
        }
    }

    /**
     * 获取自定义View的类型
     *
     * @param position 位置
     * @return View的类型
     */
    public abstract int getCustomViewType(int position);

    @Override
    public int getItemCount() {
        return dataList.size() + getExtraViewCount();
    }

    /**
     * 根据位置获取一条数据
     *
     * @param position View的位置
     * @return 数据
     */
    public M getItem(int position) {
        if (headerView != null && position == 0
                || position >= dataList.size() + getHeaderExtraViewCount()) {
            return null;
        }
        return headerView == null ? dataList.get(position) : dataList.get(position - 1);
    }

    /**
     * 根据ViewHolder获取数据
     *
     * @param holder ViewHolder
     * @return 数据
     */
    public M getItem(VH holder) {
        return getItem(holder.getAdapterPosition());
    }

    /**
     * 更新一条数据
     */
    public void updateItem(M data, int index) {
        //int index = dataList.indexOf(data);
        if (index < 0) {
            return;
        }
        dataList.set(index, data);
        if (headerView == null) {
            notifyItemChanged(index);
        } else {
            notifyItemChanged(index + 1);
        }
    }

    /**
     * 移除一条数据
     *
     * @param position 位置
     */
    public void removeItem(int position) {
        if (headerView == null) {
            dataList.remove(position);
        } else {
            dataList.remove(position - 1);
        }
        notifyItemRemoved(position);
    }

    /**
     * 移除一条数据
     *
     * @param data 要移除的数据
     */
    public void removeItem(M data) {
        int index = dataList.indexOf(data);
        if (index < 0) {
            return;
        }
        dataList.remove(index);
        if (headerView == null) {
            notifyItemRemoved(index);
        } else {
            notifyItemRemoved(index + 1);
        }
    }
}
