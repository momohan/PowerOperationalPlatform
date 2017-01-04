package com.handu.poweroperational.main.bean.results;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 柳梦 on 2016/12/12.
 * 数据字典
 */

public class DataDictionaryResult implements Parcelable {


    private String ItemId;
    private String ItemDetailId;
    private String ItemName;

    public String getItemDetailId() {
        return ItemDetailId;
    }

    public void setItemDetailId(String itemDetailId) {
        ItemDetailId = itemDetailId;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String ItemId) {
        this.ItemId = ItemId;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public DataDictionaryResult() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ItemId);
        dest.writeString(this.ItemDetailId);
        dest.writeString(this.ItemName);
    }

    protected DataDictionaryResult(Parcel in) {
        this.ItemId = in.readString();
        this.ItemDetailId = in.readString();
        this.ItemName = in.readString();
    }

    public static final Creator<DataDictionaryResult> CREATOR = new Creator<DataDictionaryResult>() {
        @Override
        public DataDictionaryResult createFromParcel(Parcel source) {
            return new DataDictionaryResult(source);
        }

        @Override
        public DataDictionaryResult[] newArray(int size) {
            return new DataDictionaryResult[size];
        }
    };
}
