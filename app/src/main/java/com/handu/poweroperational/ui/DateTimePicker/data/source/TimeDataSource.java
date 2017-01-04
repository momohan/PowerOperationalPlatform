package com.handu.poweroperational.ui.DateTimePicker.data.source;


import com.handu.poweroperational.ui.DateTimePicker.data.WheelCalendar;

/**
 *@ 作者:柳梦
 *@ 创建时间:2016/8/2 10:20
 *@ 说明:
 *@ 返回:
 *@ 参数:
 */
public interface TimeDataSource {

    int getMinYear();

    int getMaxYear();

    int getMinMonth(int currentYear);

    int getMaxMonth(int currentYear);

    int getMinDay(int year, int month);

    int getMaxDay(int year, int month);

    int getMinHour(int year, int month, int day);

    int getMaxHour(int year, int month, int day);

    int getMinMinute(int year, int month, int day, int hour);

    int getMaxMinute(int year, int month, int day, int hour);

    int getMinSecond(int year, int month, int day, int hour, int minute);

    int getMaxSecond(int year, int month, int day, int hour, int minute);

    boolean isMinYear(int year);

    boolean isMinMonth(int year, int month);

    boolean isMinDay(int year, int month, int day);

    boolean isMinHour(int year, int month, int day, int hour);

    boolean isMinMinute(int year, int month, int day, int hour, int minute);

    WheelCalendar getDefaultCalendar();

}
