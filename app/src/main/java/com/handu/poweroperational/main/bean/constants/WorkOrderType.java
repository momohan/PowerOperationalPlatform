package com.handu.poweroperational.main.bean.constants;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;

/**
 * Created by 柳梦 on 2016/11/30.
 * 工单类型
 */

public enum WorkOrderType {

    //工作任务单
    taskWorkOrder(1, PowerOperationalApplicationLike.getContext().getString(R.string.taskWorkOrder), getColorName(PowerOperationalApplicationLike.getContext().getString(R.string.taskWorkOrder),
            ContextCompat.getColor(PowerOperationalApplicationLike.getContext(), R.color.black)));

    private int type;
    private CharSequence colorName;
    private String defaultName;

    WorkOrderType(int type, String defaultName, CharSequence colorName) {
        this.type = type;
        this.defaultName = defaultName;
        this.colorName = colorName;
    }

    @Nullable
    public static WorkOrderType getType(int type) {
        for (WorkOrderType c : WorkOrderType.values()) {
            if (c.getType() == type) {
                return c;
            }
        }
        return null;
    }

    public static WorkOrderType[] getAllType() {
        return WorkOrderType.values();
    }

    public String getDefaultName() {
        return defaultName;
    }

    public CharSequence getColorName() {
        return colorName;
    }

    public int getType() {
        return type;
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
