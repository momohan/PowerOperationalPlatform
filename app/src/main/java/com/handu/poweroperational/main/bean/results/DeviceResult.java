package com.handu.poweroperational.main.bean.results;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 柳梦 on 2016/11/22.
 * 设备信息
 */

public class DeviceResult implements Parcelable {

    private int deviceId;
    private String deviceName;
    private int deviceType;

    public DeviceResult(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceResult(int deviceId, String deviceName, int deviceType) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.deviceId);
        dest.writeString(this.deviceName);
        dest.writeInt(this.deviceType);
    }

    protected DeviceResult(Parcel in) {
        this.deviceId = in.readInt();
        this.deviceName = in.readString();
        this.deviceType = in.readInt();
    }

    public static final Creator<DeviceResult> CREATOR = new Creator<DeviceResult>() {
        @Override
        public DeviceResult createFromParcel(Parcel source) {
            return new DeviceResult(source);
        }

        @Override
        public DeviceResult[] newArray(int size) {
            return new DeviceResult[size];
        }
    };
}
