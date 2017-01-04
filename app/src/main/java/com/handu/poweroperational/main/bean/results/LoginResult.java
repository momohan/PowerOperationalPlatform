package com.handu.poweroperational.main.bean.results;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 柳梦 on 2016/11/25.
 */

public class LoginResult implements Parcelable {

    private String UserId;//用户id
    private String RealName;//真实姓名
    private String NickName;//昵称
    private String HeadIcon;//头像id
    private String OrganizeId;//组织id
    private String DepartmentId;//部门id

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getHeadIcon() {
        return HeadIcon;
    }

    public void setHeadIcon(String headIcon) {
        HeadIcon = headIcon;
    }

    public String getOrganizeId() {
        return OrganizeId;
    }

    public void setOrganizeId(String organizeId) {
        OrganizeId = organizeId;
    }

    public String getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(String departmentId) {
        DepartmentId = departmentId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UserId);
        dest.writeString(this.RealName);
        dest.writeString(this.NickName);
        dest.writeString(this.HeadIcon);
        dest.writeString(this.OrganizeId);
        dest.writeString(this.DepartmentId);
    }

    public LoginResult() {
    }

    protected LoginResult(Parcel in) {
        this.UserId = in.readString();
        this.RealName = in.readString();
        this.NickName = in.readString();
        this.HeadIcon = in.readString();
        this.OrganizeId = in.readString();
        this.DepartmentId = in.readString();
    }

    public static final Creator<LoginResult> CREATOR = new Creator<LoginResult>() {
        @Override
        public LoginResult createFromParcel(Parcel source) {
            return new LoginResult(source);
        }

        @Override
        public LoginResult[] newArray(int size) {
            return new LoginResult[size];
        }
    };
}


