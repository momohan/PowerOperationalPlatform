package com.handu.poweroperational.ui.DateTimePicker.utils;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.handu.poweroperational.R;
import com.handu.poweroperational.ui.DateTimePicker.TimePickerDialog;
import com.handu.poweroperational.ui.DateTimePicker.data.Type;
import com.handu.poweroperational.ui.DateTimePicker.listener.OnDateSetListener;
import com.handu.poweroperational.utils.Tools;


/**
 * 作者：'柳梦' on 2016/8/1 15:28
 * 邮箱：mobyq@qq.com
 * 说明：时间选择类
 */
public class DateTimePicker {

    public static final String ALL = "all";//年月日时分
    public static final String YEAR_MONTH = "year_month";//年月
    public static final String YEAR_MONTH_DAY = "year_month_day";//年月日
    public static final String MONTH_DAY_HOUR_MIN = "month_day_hour_minute";//月日时分
    public static final String HOUR_MINUTE = "hour_minute";//时分
    public static final String MONTH_DAY_HOUR_MINUTE_SECOND = "month_day_hour_minute_second";//月日时分秒
    public static final String HOUR_MINUTE_SECOND = "hour_minute_second";//时分秒
    public static final String MINUTE_SECOND = "minute_second";//分秒

    /**
     * @param context           上下文
     * @param fragmentManager   fragment管理器
     * @param type              时间类型
     * @param onDateSetListener 时间选择监听
     */
    public static void show(Context context, FragmentManager fragmentManager,
                            String type, OnDateSetListener onDateSetListener, String currentDateTime) {
        long currentTimeMillis = System.currentTimeMillis();
        if (!TextUtils.isEmpty(currentDateTime))
            currentTimeMillis = Tools.convertDateStrToMillis(currentDateTime);
        TimePickerDialog dialog;
        //20年的前后时间
        long twentyYears = 30L * 365 * 1000 * 60 * 60 * 24L;

        switch (type) {
            case ALL:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.ALL)
                        .setCallBack(onDateSetListener)
                        .setMinMillseconds(System.currentTimeMillis() - twentyYears)
                        .setMaxMillseconds(System.currentTimeMillis() + twentyYears)
                        .setThemeColor(ContextCompat.getColor(context, R.color.timepicker_dialog_bg))
                        .setWheelItemTextNormalColor(ContextCompat.getColor(context, R.color.timetimepicker_default_name_color))
                        .setWheelItemTextSelectorColor(ContextCompat.getColor(context, R.color.timepicker_toolbar_bg))
                        .setWheelItemTextSize(12)
                        .build();
                dialog.show(fragmentManager, type);
                break;
            case YEAR_MONTH:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.YEAR_MONTH)
                        .setMinMillseconds(System.currentTimeMillis() - twentyYears)
                        .setMaxMillseconds(System.currentTimeMillis() + twentyYears)
                        .setThemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setCallBack(onDateSetListener)
                        .build();
                dialog.show(fragmentManager, type);
                break;
            case YEAR_MONTH_DAY:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.YEAR_MONTH_DAY)
                        .setMinMillseconds(System.currentTimeMillis() - twentyYears)
                        .setMaxMillseconds(System.currentTimeMillis() + twentyYears)
                        .setThemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setCallBack(onDateSetListener)
                        .build();
                dialog.show(fragmentManager, type);
                break;
            case MONTH_DAY_HOUR_MIN:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.MONTH_DAY_HOUR_MINUTE)
                        .setThemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setCallBack(onDateSetListener)
                        .build();
                dialog.show(fragmentManager, type);
                break;
            case HOUR_MINUTE:
                dialog = new TimePickerDialog.Builder()
                        .setType(Type.HOUR_MINUTE)
                        .setCurrentMillseconds(currentTimeMillis)
                        .setThemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setCallBack(onDateSetListener)
                        .build();
                dialog.show(fragmentManager, type);
                break;
            case MINUTE_SECOND:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.MINUTE_SECOND)
                        .setThemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setCallBack(onDateSetListener)
                        .build();
                dialog.show(fragmentManager, type);
                break;
            case HOUR_MINUTE_SECOND:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.HOUR_MINUTE_SECOND)
                        .setThemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setCallBack(onDateSetListener)
                        .build();
                dialog.show(fragmentManager, type);
                break;
            case MONTH_DAY_HOUR_MINUTE_SECOND:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.MONTH_DAY_HOUR_MINUTE_SECOND)
                        .setThemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setCallBack(onDateSetListener)
                        .build();
                dialog.show(fragmentManager, type);
                break;


            default:
                dialog = new TimePickerDialog.Builder()
                        .setCurrentMillseconds(currentTimeMillis)
                        .setType(Type.ALL)
                        .setCallBack(onDateSetListener)
                        .setMinMillseconds(System.currentTimeMillis() - twentyYears)
                        .setMaxMillseconds(System.currentTimeMillis() + twentyYears)
                        .setThemeColor(ContextCompat.getColor(context, R.color.timepicker_dialog_bg))
                        .setWheelItemTextNormalColor(ContextCompat.getColor(context, R.color.timetimepicker_default_name_color))
                        .setWheelItemTextSelectorColor(ContextCompat.getColor(context, R.color.timepicker_toolbar_bg))
                        .setWheelItemTextSize(12)
                        .build();
                dialog.show(fragmentManager, type);
                break;
        }
    }
}
