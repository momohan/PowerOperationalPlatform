package com.handu.poweroperational.ui.RecyclerView;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * @ 作者:柳梦
 * @ 创建时间:2016/8/26 10:36
 * @ 说明:点击事件
 * @ 返回:
 * @ 参数:
 */

public class ItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private OnItemClickListener clickListener;
    private GestureDetectorCompat gestureDetector;

    public interface OnItemClickListener {
        /**
         * 点击时回调
         *
         * @param view     点击的View
         * @param position 点击的位置
         */
        void onItemClick(View view, int position);

        /**
         * 长点击时回调
         *
         * @param view     点击的View
         * @param position 点击的位置
         */
        void onItemLongClick(View view, int position);
    }

    public ItemClickListener(final RecyclerView recyclerView, OnItemClickListener listener) {
        this.clickListener = listener;
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (childView != null && clickListener != null) {
                            clickListener.onItemLongClick(childView,
                                    recyclerView.getChildAdapterPosition(childView));
                        }
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
