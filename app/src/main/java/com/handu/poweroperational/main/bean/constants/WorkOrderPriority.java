package com.handu.poweroperational.main.bean.constants;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;

/**
 * Created by 柳梦 on 2016/12/9.
 * 工单优先级
 */

public enum WorkOrderPriority {

    HIGH(1, getColorName("高", ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.red_dark)), "高"),
    MIDDLE(2, getColorName("中", ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.blue_dark)), "中"),
    LOW(3, getColorName("低", ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.green_dark)), "低");

    private int priority;
    private CharSequence colorName;
    private String defaultName;

    WorkOrderPriority(int priority, CharSequence colorName, String defaultName) {
        this.priority = priority;
        this.colorName = colorName;
        this.defaultName = defaultName;
    }

    @Nullable
    public static WorkOrderPriority getPriority(int type) {
        for (WorkOrderPriority c : WorkOrderPriority.values()) {
            if (c.getPriority() == type) {
                return c;
            }
        }
        return null;
    }

    public static WorkOrderPriority[] getAllPriority() {
        return WorkOrderPriority.values();
    }

    public String getDefaultName() {
        return defaultName;
    }

    public CharSequence getColorName() {
        return colorName;
    }

    public int getPriority() {
        return priority;
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
