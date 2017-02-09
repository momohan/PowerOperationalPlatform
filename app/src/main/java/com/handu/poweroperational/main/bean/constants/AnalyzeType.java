package com.handu.poweroperational.main.bean.constants;

/**
 * Created by 柳梦 on 2017/2/7.
 */

public enum AnalyzeType {

    current(1, "三项电流"),
    voltage(2, "三项电压"),
    residueCurrent(3, "剩余电流"),
    voltageThreePhaseUnBalance(4, "三相电压不平衡"),
    currentThreePhaseUnBalance(5, "三相电流不平衡");

    private int type;
    private String name;

    AnalyzeType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public static AnalyzeType[] getAllAnalyzeType() {
        return AnalyzeType.values();
    }
}
