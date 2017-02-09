package com.handu.poweroperational.main.bean.constants;

/**
 * Created by 柳梦 on 2016/11/10.
 * 事件类型
 */

public enum EventType {

    registerCid(0),//注册个推cid
    homeRefresh(1),//主界面刷新
    newWorkOrderUpdate(2),//新工单界面数据更新
    alarmNumUpdate(3),//告警数量更新
    selectOperationDevice(4),//选择设备后更新数据
    addWorkOrderSubmit(5),//添加工单提交
    requestOperationDevice(6);//运维请求设备数据

    private int type;

    EventType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
