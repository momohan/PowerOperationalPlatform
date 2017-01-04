package com.handu.poweroperational.ui.DateTimePicker.listener;


import com.handu.poweroperational.ui.DateTimePicker.TimePickerDialog;

/**
 * @ 作者:柳梦
 * @ 创建时间:2016/8/1 16:36
 * @ 说明:
 * @ 返回:
 * @ 参数:
 */
public interface OnDateSetListener {

    //timePickerView，long型的时间结果，string型的结果
    void onDateSet(TimePickerDialog timePickerView, long millSeconds, String result);
}
