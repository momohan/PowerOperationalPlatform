package com.handu.poweroperational.utils;

import android.os.Environment;

import java.io.File;

/**
 * 作者：柳梦 2016/9/20 11:51
 * 邮箱：mobyq@qq.com
 * 说明: 常
 */
public class AppConstant {

    //TAG标签
    public static final String TAG = "柳梦";
    //错误日志保存路径
    public static final String LOG_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "HanDu_App" + File.separator + "App_error_log" + File.separator + "PowerOperationalPlatform";
    //压缩后图片保存路径
    public static final String COMPRESS_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "HanDu_App" + File.separator + "App_Image" + File.separator + "PowerOperationalPlatform";
    //APK下载路径
    public static final String APK_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "HanDu_App" + File.separator + "App_download";

    public static final String userId = "userId";//用户id
    public static final String password = "password";//密码
    public static final String username = "username";//用户名
    public static final String realName = "realName";//真实名称
    public static final String organizeId = "organizeId";//组织id
    public static final String departmentId = "departmentId";//部门id

    public static final String currentDeviceName = "currentDeviceName";
    public static final String currentDeviceId = "currentDeviceId";
    public static final String currentDeviceType = "currentDeviceType";
}
