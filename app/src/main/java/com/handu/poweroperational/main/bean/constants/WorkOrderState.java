package com.handu.poweroperational.main.bean.constants;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;

/**
 * Created by 柳梦 on 2016/11/18.
 * 工单状态
 */

public enum WorkOrderState {

    refuse(-1, PowerOperationalApplicationLike.getContext().getString(R.string.is_refuse), getColorName(PowerOperationalApplicationLike.getContext().getString(R.string.is_refuse),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.black)),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.black)),//拒绝
    unAccept(1, PowerOperationalApplicationLike.getContext().getString(R.string.is_not_accept), getColorName(PowerOperationalApplicationLike.getContext().getString(R.string.is_not_accept),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.red_light)),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.red_light)),//未接收
    unArrive(2, PowerOperationalApplicationLike.getContext().getString(R.string.is_accept), getColorName(PowerOperationalApplicationLike.getContext().getString(R.string.is_accept),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.blue_dark)),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.blue_dark)),//未到达
    unFinish(3, PowerOperationalApplicationLike.getContext().getString(R.string.is_doing), getColorName(PowerOperationalApplicationLike.getContext().getString(R.string.is_doing),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.green_dark)),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.green_dark)),//未完成
    finished(4, PowerOperationalApplicationLike.getContext().getString(R.string.is_finish), getColorName(PowerOperationalApplicationLike.getContext().getString(R.string.is_finish),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.gray_dark)),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.gray_dark)),//完成
    archived(5, PowerOperationalApplicationLike.getContext().getString(R.string.is_archived), getColorName(PowerOperationalApplicationLike.getContext().getString(R.string.is_archived),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.yellow_dark)),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.yellow_dark));//已归档

    private int state;
    private CharSequence colorName;
    private String defaultName;
    private int color;

    WorkOrderState(int state, String defaultName, CharSequence colorName, int color) {
        this.state = state;
        this.defaultName = defaultName;
        this.colorName = colorName;
        this.color = color;
    }

    @Nullable
    public static WorkOrderState getState(int state) {
        for (WorkOrderState c : WorkOrderState.values()) {
            if (c.getState() == state) {
                return c;
            }
        }
        return null;
    }

    public static WorkOrderState[] getAllState() {
        return WorkOrderState.values();
    }

    public int getState() {
        return state;
    }

    public int getTextColor() {
        return color;
    }

    public CharSequence getColorName() {
        return colorName;
    }

    public String getDefaultName() {
        return defaultName;
    }

    /**
     * 根据字符串添加颜色
     *
     * @param str
     * @return
     */
    private static CharSequence getColorName(String str, int color) {
        //文本内容
        SpannableString spannableString = new SpannableString(str);
        //文本长度
        int length = spannableString.length();
        //设置字符颜色
        spannableString.setSpan(new ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}
