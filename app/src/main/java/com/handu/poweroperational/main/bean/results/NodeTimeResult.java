package com.handu.poweroperational.main.bean.results;

/**
 * Created by 柳梦 on 2016/12/12.
 * 工单节点时间
 */

public class NodeTimeResult {

    //派发
    private String AlreadyNotDistribute;
    //接收
    private String AlreadyReceive;
    //到达
    private String AlreadyArrive;
    //完成
    private String AlreadyFinish;
    //拒绝
    private String AlreadyRefuse;

    public String getAlreadyNotDistribute() {
        return AlreadyNotDistribute;
    }

    public void setAlreadyNotDistribute(String alreadyNotDistribute) {
        AlreadyNotDistribute = alreadyNotDistribute;
    }

    public String getAlreadyReceive() {
        return AlreadyReceive;
    }

    public void setAlreadyReceive(String alreadyReceive) {
        AlreadyReceive = alreadyReceive;
    }

    public String getAlreadyArrive() {
        return AlreadyArrive;
    }

    public void setAlreadyArrive(String alreadyArrive) {
        AlreadyArrive = alreadyArrive;
    }

    public String getAlreadyFinish() {
        return AlreadyFinish;
    }

    public void setAlreadyFinish(String alreadyFinish) {
        AlreadyFinish = alreadyFinish;
    }

    public String getAlreadyRefuse() {
        return AlreadyRefuse;
    }

    public void setAlreadyRefuse(String alreadyRefuse) {
        AlreadyRefuse = alreadyRefuse;
    }
}
