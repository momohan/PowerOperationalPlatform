package com.handu.poweroperational.ui.DateTimePicker.utils;

import android.view.View;

import com.handu.poweroperational.ui.DateTimePicker.data.WheelCalendar;


/**
 * @ 作者:柳梦
 * @ 创建时间:2016/8/1 16:41
 * @ 说明:
 * @ 返回:
 * @ 参数:
 */
public class Utils {

    public static boolean isTimeEquals(WheelCalendar calendar, int... params) {
        switch (params.length) {
            case 1:
                return calendar.year == params[0];
            case 2:
                return calendar.year == params[0] &&
                        calendar.month == params[1];
            case 3:
                return calendar.year == params[0] &&
                        calendar.month == params[1] &&
                        calendar.day == params[2];
            case 4:
                return calendar.year == params[0] &&
                        calendar.month == params[1] &&
                        calendar.day == params[2] &&
                        calendar.hour == params[3];
            case 5:
                return calendar.year == params[0] &&
                        calendar.month == params[1] &&
                        calendar.day == params[2] &&
                        calendar.hour == params[3] &&
                        calendar.minute == params[4];
        }
        return false;
    }

    public static void hideViews(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
    }
}
