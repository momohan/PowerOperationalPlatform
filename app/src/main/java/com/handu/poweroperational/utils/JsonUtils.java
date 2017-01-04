package com.handu.poweroperational.utils;

import android.text.TextUtils;

/**
 * Created by 柳梦 on 2016/12/1.
 */

public class JsonUtils {

    public static final int JSON_TYPE_ERROR = -1;//错误json字符串
    public static final int JSON_TYPE_OBJECT = 0;//对象
    public static final int JSON_TYPE_ARRAY = 1;//数组

    public static int getJsonType(String str) {

        if (TextUtils.isEmpty(str)) {
            return JSON_TYPE_ERROR;
        }
        char[] strChar = str.substring(0, 1).toCharArray();
        char firstChar = strChar[0];
        if (firstChar == '{') {
            return JSON_TYPE_OBJECT;
        } else if (firstChar == '[') {
            return JSON_TYPE_ARRAY;
        } else {
            return JSON_TYPE_ERROR;
        }
    }
}
