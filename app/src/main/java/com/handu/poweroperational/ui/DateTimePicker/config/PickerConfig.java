package com.handu.poweroperational.ui.DateTimePicker.config;


import com.handu.poweroperational.ui.DateTimePicker.data.Type;
import com.handu.poweroperational.ui.DateTimePicker.data.WheelCalendar;
import com.handu.poweroperational.ui.DateTimePicker.listener.OnDateSetListener;

/**
 * @ 作者:柳梦
 * @ 创建时间:2016/8/2 10:20
 * @ 说明:
 * @ 返回:
 * @ 参数:
 */
public class PickerConfig {

    public Type mType = DefaultConfig.TYPE;
    public int mThemeColor = DefaultConfig.COLOR;
    public String mCancelString = DefaultConfig.CANCEL;
    public String mSureString = DefaultConfig.SURE;
    public String mTitleString = DefaultConfig.TITLE;
    public int mToolBarTVColor = DefaultConfig.TOOLBAR_TV_COLOR;
    public int mWheelTVNormalColor = DefaultConfig.TV_NORMAL_COLOR;
    public int mWheelTVSelectorColor = DefaultConfig.TV_SELECTOR_COLOR;
    public int mWheelTVSize = DefaultConfig.TV_SIZE;
    public boolean cyclic = DefaultConfig.CYCLIC;
    public String mYear = DefaultConfig.YEAR;
    public String mMonth = DefaultConfig.MONTH;
    public String mDay = DefaultConfig.DAY;
    public String mHour = DefaultConfig.HOUR;
    public String mMinute = DefaultConfig.MINUTE;
    public String mSecond = DefaultConfig.SECOND;

    /**
     * The min timeMillseconds
     */
    public WheelCalendar mMinCalendar = new WheelCalendar(0);

    /**
     * The max timeMillseconds
     */
    public WheelCalendar mMaxCalendar = new WheelCalendar(0);

    /**
     * The default selector timeMillseconds
     */
    public WheelCalendar mCurrentCalendar = new WheelCalendar(0);

    public OnDateSetListener mCallBack;
}
