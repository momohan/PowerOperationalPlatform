package com.handu.poweroperational.ui.DateTimePicker.data.source;

import com.handu.poweroperational.ui.DateTimePicker.config.PickerConfig;
import com.handu.poweroperational.ui.DateTimePicker.data.WheelCalendar;
import com.handu.poweroperational.ui.DateTimePicker.utils.PickerContants;
import com.handu.poweroperational.ui.DateTimePicker.utils.Utils;

import java.util.Calendar;


/**
 * @ 作者:柳梦
 * @ 创建时间:2016/8/1 17:17
 * @ 说明:
 * @ 返回:
 * @ 参数:
 */
public class TimeRepository implements TimeDataSource {
    PickerConfig mPickerConfig;
    WheelCalendar mCalendarMin, mCalendarMax;

    boolean mIsMinNoRange, mIsMaxNoRange;

    public TimeRepository(PickerConfig pickerConfig) {
        mPickerConfig = pickerConfig;
        mCalendarMin = pickerConfig.mMinCalendar;
        mCalendarMax = pickerConfig.mMaxCalendar;

        mIsMinNoRange = mCalendarMin.isNoRange();
        mIsMaxNoRange = mCalendarMax.isNoRange();
    }

    @Override
    public int getMinYear() {
        if (mIsMinNoRange)
            return PickerContants.DEFAULT_MIN_YEAR;
        else
            return mCalendarMin.year;
    }

    @Override
    public int getMaxYear() {
        if (mIsMaxNoRange)
            return getMinYear() + PickerContants.YEAR_COUNT;

        return mCalendarMax.year;
    }

    @Override
    public int getMinMonth(int year) {
        if (!mIsMinNoRange && Utils.isTimeEquals(mCalendarMin, year))
            return mCalendarMin.month;

        return PickerContants.MIN_MONTH;
    }

    @Override
    public int getMaxMonth(int year) {
        if (!mIsMaxNoRange && Utils.isTimeEquals(mCalendarMax, year))
            return mCalendarMax.month;

        return PickerContants.MAX_MONTH;
    }

    @Override
    public int getMinDay(int year, int month) {
        if (!mIsMinNoRange && Utils.isTimeEquals(mCalendarMin, year, month))
            return mCalendarMin.day;

        return PickerContants.MIN_DAY;
    }

    @Override
    public int getMaxDay(int year, int month) {
        if (!mIsMaxNoRange && Utils.isTimeEquals(mCalendarMax, year, month))
            return mCalendarMax.day;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, month - 1);

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getMinHour(int year, int month, int day) {
        if (!mIsMinNoRange && Utils.isTimeEquals(mCalendarMin, year, month, day))
            return mCalendarMin.hour;
        else
            return PickerContants.MIN_HOUR;
    }

    @Override
    public int getMaxHour(int year, int month, int day) {
        if (!mIsMaxNoRange && Utils.isTimeEquals(mCalendarMax, year, month, day))
            return mCalendarMax.hour;

        return PickerContants.MAX_HOUR;
    }

    @Override
    public int getMinMinute(int year, int month, int day, int hour) {
        if (!mIsMinNoRange && Utils.isTimeEquals(mCalendarMin, year, month, day, hour))
            return mCalendarMin.minute + 1;
        else
            return PickerContants.MIN_MINUTE;
    }

    @Override
    public int getMaxMinute(int year, int month, int day, int hour) {
        if (!mIsMaxNoRange && Utils.isTimeEquals(mCalendarMax, year, month, day, hour))
            return mCalendarMax.minute;

        return PickerContants.MAX_MINUTE;
    }

    @Override
    public int getMinSecond(int year, int month, int day, int hour, int minute) {
        if (!mIsMinNoRange && Utils.isTimeEquals(mCalendarMin, year, month, day, hour, minute))
            return mCalendarMin.second + 1;
        else
            return PickerContants.MIN_SECOND;
    }

    @Override
    public int getMaxSecond(int year, int month, int day, int hour, int minute) {
        if (!mIsMaxNoRange && Utils.isTimeEquals(mCalendarMax, year, month, day, hour, minute))
            return mCalendarMax.second;

        return PickerContants.MAX_SECOND;
    }

    @Override
    public boolean isMinYear(int year) {
        return Utils.isTimeEquals(mCalendarMin, year);
    }

    @Override
    public boolean isMinMonth(int year, int month) {
        return Utils.isTimeEquals(mCalendarMin, year, month);
    }

    @Override
    public boolean isMinDay(int year, int month, int day) {
        return Utils.isTimeEquals(mCalendarMin, year, month, day);
    }

    @Override
    public boolean isMinHour(int year, int month, int day, int hour) {
        return Utils.isTimeEquals(mCalendarMin, year, month, day, hour);
    }

    @Override
    public boolean isMinMinute(int year, int month, int day, int hour, int minute) {
        return Utils.isTimeEquals(mCalendarMin, year, month, day, hour, minute);
    }

    @Override
    public WheelCalendar getDefaultCalendar() {
        return mPickerConfig.mCurrentCalendar;
    }
}
