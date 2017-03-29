package com.handu.poweroperational.utils;

import com.handu.poweroperational.BuildConfig;

/**
 * @创建人 柳梦
 * @时间 2016/9/13 9:32
 * @说明 网络地址类
 */
public class ServiceUrl {

    /**
     * ip地址
     */
    private static String Ip = BuildConfig.Ip;
    private static String Api = "/api/AndroidApi/";
    public static String BaseIp = Ip;
    public static String BaseUrl = Ip + Api;

    //登录
    public static final String Login = "Login";
    //注册cid
    public static final String RegisterClientId = "RegisterClientId";
    //新工单列表
    public static final String GetWorkOrderList = "GetWorkOrderList";
    //更新工单状态
    public static final String UpWorkOrderState = "UpWorkOrderState";
    //上传图片
    public static final String UploadImg = "UploadImg";
    //请求已上传图片
    public static final String GetImageList = "GetImageList";
    //获取历史工单列表
    public static final String GetHistoryList = "GetHistoryList";
    //获取机构人员
    public static final String GetUserTreeJson = "GetUserTreeJson";
    //获取数据字典
    public static final String GetDictionaryData = "GetDictionaryData";
    //添加任务工单
    public static final String SaveWorkOrder = "SaveWorkOrder";
    //获取工单节点时间
    public static final String GetWorkOrderNodeTime = "GetWorkOrderNodeTime";
    //获取楼宇名称
    public static final String GetBuildingList = "GetBuildingList";
    //获取工器具
    public static final String GetM_CategoryTree = "GetM_CategoryTree";
}
