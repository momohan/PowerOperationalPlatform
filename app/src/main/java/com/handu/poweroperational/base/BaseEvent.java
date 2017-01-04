package com.handu.poweroperational.base;

/**
 * 作者：柳梦 2016/10/18 16:22
 * 邮箱：mobyq@qq.com
 * 说明: 基本事件
 */
public class BaseEvent {

    public BaseEvent() {
    }

    public BaseEvent(int eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public BaseEvent(int eventType) {
        this.eventType = eventType;
    }

    public int eventType;//事件订阅类型
    public Object data;//事件订阅数据

}
