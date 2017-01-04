package com.handu.poweroperational.ui.DateTimePicker;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplication;
import com.handu.poweroperational.ui.DateTimePicker.config.PickerConfig;
import com.handu.poweroperational.ui.DateTimePicker.data.Type;
import com.handu.poweroperational.ui.DateTimePicker.data.WheelCalendar;
import com.handu.poweroperational.ui.DateTimePicker.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @ 作者:柳梦
 * @ 创建时间:2016/8/1 14:53
 * @ 说明:时间选择器
 * @ 返回:
 * @ 参数:
 */
public class TimePickerDialog extends DialogFragment implements View.OnClickListener {

    PickerConfig mPickerConfig;
    private TimeWheel mTimeWheel;
    private long mCurrentMillSeconds;
    private String mResult;
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static TimePickerDialog newIntance(PickerConfig pickerConfig) {
        TimePickerDialog timePickerDialog = new TimePickerDialog();
        timePickerDialog.initialize(pickerConfig);
        return timePickerDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onResume() {
        super.onResume();
        int height = getResources().getDimensionPixelSize(R.dimen.picker_height);

        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);//Here!
        window.setGravity(Gravity.BOTTOM);
    }

    private void initialize(PickerConfig pickerConfig) {
        mPickerConfig = pickerConfig;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog_NoTitle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(initView());
        return dialog;
    }

    View initView() {
        LayoutInflater inflater = LayoutInflater.from(PowerOperationalApplication.getContext());
        View view = inflater.inflate(R.layout.timepicker_layout, null, false);
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(this);
        TextView sure = (TextView) view.findViewById(R.id.tv_sure);
        sure.setOnClickListener(this);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        View toolbar = view.findViewById(R.id.toolbar);
        title.setText(mPickerConfig.mTitleString);
        cancel.setText(mPickerConfig.mCancelString);
        sure.setText(mPickerConfig.mSureString);
        toolbar.setBackgroundColor(mPickerConfig.mThemeColor);
        mTimeWheel = new TimeWheel(view, mPickerConfig);
        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_cancel) {
            dismiss();
        } else if (i == R.id.tv_sure) {
            sureClicked();
        }
    }

    /*
    * @desc This method returns the current milliseconds. If current milliseconds is not set,
    *       this will return the system milliseconds.
    * @param none
    * @return long - the current milliseconds.
    */
    public long getCurrentMillSeconds() {
        if (mCurrentMillSeconds == 0)
            return System.currentTimeMillis();

        return mCurrentMillSeconds;
    }

    /*
    * @desc This method is called when onClick method is invoked by sure button. A Calendar instance is created and 
    *       initialized. 
    * @param none
    * @return none
    */
    void sureClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, mTimeWheel.getCurrentYear());
        calendar.set(Calendar.MONTH, mTimeWheel.getCurrentMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, mTimeWheel.getCurrentDay());
        calendar.set(Calendar.HOUR_OF_DAY, mTimeWheel.getCurrentHour());
        calendar.set(Calendar.MINUTE, mTimeWheel.getCurrentMinute());
        calendar.set(Calendar.SECOND, mTimeWheel.getCurrentSecond());
        mCurrentMillSeconds = calendar.getTimeInMillis();

        if (mPickerConfig.mCallBack != null) {
            mResult = getDateToString(mCurrentMillSeconds);
            mPickerConfig.mCallBack.onDateSet(this, mCurrentMillSeconds, mResult);
        }
        dismiss();
    }

    private String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    public static class Builder {
        PickerConfig mPickerConfig;

        public Builder() {
            mPickerConfig = new PickerConfig();
        }

        public Builder setType(Type type) {
            mPickerConfig.mType = type;
            switch (type) {
                case ALL:
                    sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    break;
                case YEAR_MONTH:
                    sf = new SimpleDateFormat("yyyy-MM");
                    break;
                case YEAR_MONTH_DAY:
                    sf = new SimpleDateFormat("yyyy-MM-dd");
                    break;
                case MONTH_DAY_HOUR_MINUTE:
                    sf = new SimpleDateFormat("MM-dd HH:mm");
                    break;
                case HOUR_MINUTE:
                    sf = new SimpleDateFormat("HH:mm");
                    break;
                case HOUR_MINUTE_SECOND:
                    sf = new SimpleDateFormat("HH:mm:ss");
                    break;
                case MINUTE_SECOND:
                    sf = new SimpleDateFormat("mm:ss");
                    break;
                case MONTH_DAY_HOUR_MINUTE_SECOND:
                    sf = new SimpleDateFormat("MM-dd HH:mm:ss");
                    break;
            }
            return this;
        }

        public Builder setThemeColor(int color) {
            mPickerConfig.mThemeColor = color;
            return this;
        }

        public Builder setCancelStringId(String left) {
            mPickerConfig.mCancelString = left;
            return this;
        }

        public Builder setSureStringId(String right) {
            mPickerConfig.mSureString = right;
            return this;
        }

        public Builder setTitleStringId(String title) {
            mPickerConfig.mTitleString = title;
            return this;
        }

        public Builder setToolBarTextColor(int color) {
            mPickerConfig.mToolBarTVColor = color;
            return this;
        }

        public Builder setWheelItemTextNormalColor(int color) {
            mPickerConfig.mWheelTVNormalColor = color;
            return this;
        }

        public Builder setWheelItemTextSelectorColor(int color) {
            mPickerConfig.mWheelTVSelectorColor = color;
            return this;
        }

        public Builder setWheelItemTextSize(int size) {
            mPickerConfig.mWheelTVSize = size;
            return this;
        }

        public Builder setCyclic(boolean cyclic) {
            mPickerConfig.cyclic = cyclic;
            return this;
        }

        public Builder setMinMillseconds(long millseconds) {
            mPickerConfig.mMinCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public Builder setMaxMillseconds(long millseconds) {
            mPickerConfig.mMaxCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public Builder setCurrentMillseconds(long millseconds) {
            mPickerConfig.mCurrentCalendar = new WheelCalendar(millseconds);
            return this;
        }

        public Builder setYearText(String year) {
            mPickerConfig.mYear = year;
            return this;
        }

        public Builder setMonthText(String month) {
            mPickerConfig.mMonth = month;
            return this;
        }

        public Builder setDayText(String day) {
            mPickerConfig.mDay = day;
            return this;
        }

        public Builder setHourText(String hour) {
            mPickerConfig.mHour = hour;
            return this;
        }

        public Builder setMinuteText(String minute) {
            mPickerConfig.mMinute = minute;
            return this;
        }

        public Builder setCallBack(OnDateSetListener listener) {
            mPickerConfig.mCallBack = listener;
            return this;
        }

        public TimePickerDialog build() {
            return newIntance(mPickerConfig);
        }

    }


}
