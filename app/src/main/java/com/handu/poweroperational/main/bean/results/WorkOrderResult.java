package com.handu.poweroperational.main.bean.results;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 柳梦 on 2016/11/18.
 * 工单信息
 */

public class WorkOrderResult implements Parcelable {

    private String DetailId;
    private String Number;
    private String Source;
    private int Type;
    private String BeginTime;
    private String EndTime;
    private String Address;
    private String Content;
    private String ResponseUserId;
    private String ResponseUserName;
    private String ParticipantIds;
    private String ParticipantNames;
    private String FinishContent;
    private int CurrentState;
    private String CurrentStateRecordTime;
    private String Score;
    private String ScoreNote;
    private String SortCode;
    private String DeleteMark;
    private String EnabledMark;
    private String Description;
    private String CreateDate;
    private String CreateUserId;
    private String CreateUserName;
    private String ModifyDate;
    private String ModifyUserId;
    private String ModifyUserName;
    private String FaultType;

    public String getFaultType() {
        return FaultType;
    }

    public void setFaultType(String faultType) {
        FaultType = faultType;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    private String ClientName;

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    private int Priority;

    public WorkOrderResult(String number) {
        Number = number;
    }

    public WorkOrderResult() {
    }

    public String getDetailId() {
        return DetailId;
    }

    public void setDetailId(String detailId) {
        DetailId = detailId;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getResponseUserId() {
        return ResponseUserId;
    }

    public void setResponseUserId(String responseUserId) {
        ResponseUserId = responseUserId;
    }

    public String getResponseUserName() {
        return ResponseUserName;
    }

    public void setResponseUserName(String responseUserName) {
        ResponseUserName = responseUserName;
    }

    public String getParticipantIds() {
        return ParticipantIds;
    }

    public void setParticipantIds(String participantIds) {
        ParticipantIds = participantIds;
    }

    public String getParticipantNames() {
        return ParticipantNames;
    }

    public void setParticipantNames(String participantNames) {
        ParticipantNames = participantNames;
    }

    public String getFinishContent() {
        return FinishContent;
    }

    public void setFinishContent(String finishContent) {
        FinishContent = finishContent;
    }

    public int getCurrentState() {
        return CurrentState;
    }

    public void setCurrentState(int currentState) {
        CurrentState = currentState;
    }

    public String getCurrentStateRecordTime() {
        return CurrentStateRecordTime;
    }

    public void setCurrentStateRecordTime(String currentStateRecordTime) {
        CurrentStateRecordTime = currentStateRecordTime;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getScoreNote() {
        return ScoreNote;
    }

    public void setScoreNote(String scoreNote) {
        ScoreNote = scoreNote;
    }

    public String getSortCode() {
        return SortCode;
    }

    public void setSortCode(String sortCode) {
        SortCode = sortCode;
    }

    public String getDeleteMark() {
        return DeleteMark;
    }

    public void setDeleteMark(String deleteMark) {
        DeleteMark = deleteMark;
    }

    public String getEnabledMark() {
        return EnabledMark;
    }

    public void setEnabledMark(String enabledMark) {
        EnabledMark = enabledMark;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getCreateUserId() {
        return CreateUserId;
    }

    public void setCreateUserId(String createUserId) {
        CreateUserId = createUserId;
    }

    public String getCreateUserName() {
        return CreateUserName;
    }

    public void setCreateUserName(String createUserName) {
        CreateUserName = createUserName;
    }

    public String getModifyDate() {
        return ModifyDate;
    }

    public void setModifyDate(String modifyDate) {
        ModifyDate = modifyDate;
    }

    public String getModifyUserId() {
        return ModifyUserId;
    }

    public void setModifyUserId(String modifyUserId) {
        ModifyUserId = modifyUserId;
    }

    public String getModifyUserName() {
        return ModifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        ModifyUserName = modifyUserName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.DetailId);
        dest.writeString(this.Number);
        dest.writeString(this.Source);
        dest.writeInt(this.Type);
        dest.writeString(this.BeginTime);
        dest.writeString(this.EndTime);
        dest.writeString(this.Address);
        dest.writeString(this.Content);
        dest.writeString(this.ResponseUserId);
        dest.writeString(this.ResponseUserName);
        dest.writeString(this.ParticipantIds);
        dest.writeString(this.ParticipantNames);
        dest.writeString(this.FinishContent);
        dest.writeInt(this.CurrentState);
        dest.writeString(this.CurrentStateRecordTime);
        dest.writeString(this.Score);
        dest.writeString(this.ScoreNote);
        dest.writeString(this.SortCode);
        dest.writeString(this.DeleteMark);
        dest.writeString(this.EnabledMark);
        dest.writeString(this.Description);
        dest.writeString(this.CreateDate);
        dest.writeString(this.CreateUserId);
        dest.writeString(this.CreateUserName);
        dest.writeString(this.ModifyDate);
        dest.writeString(this.ModifyUserId);
        dest.writeString(this.ModifyUserName);
        dest.writeString(this.FaultType);
        dest.writeString(this.ClientName);
        dest.writeInt(this.Priority);
    }

    protected WorkOrderResult(Parcel in) {
        this.DetailId = in.readString();
        this.Number = in.readString();
        this.Source = in.readString();
        this.Type = in.readInt();
        this.BeginTime = in.readString();
        this.EndTime = in.readString();
        this.Address = in.readString();
        this.Content = in.readString();
        this.ResponseUserId = in.readString();
        this.ResponseUserName = in.readString();
        this.ParticipantIds = in.readString();
        this.ParticipantNames = in.readString();
        this.FinishContent = in.readString();
        this.CurrentState = in.readInt();
        this.CurrentStateRecordTime = in.readString();
        this.Score = in.readString();
        this.ScoreNote = in.readString();
        this.SortCode = in.readString();
        this.DeleteMark = in.readString();
        this.EnabledMark = in.readString();
        this.Description = in.readString();
        this.CreateDate = in.readString();
        this.CreateUserId = in.readString();
        this.CreateUserName = in.readString();
        this.ModifyDate = in.readString();
        this.ModifyUserId = in.readString();
        this.ModifyUserName = in.readString();
        this.FaultType = in.readString();
        this.ClientName = in.readString();
        this.Priority = in.readInt();
    }

    public static final Creator<WorkOrderResult> CREATOR = new Creator<WorkOrderResult>() {
        @Override
        public WorkOrderResult createFromParcel(Parcel source) {
            return new WorkOrderResult(source);
        }

        @Override
        public WorkOrderResult[] newArray(int size) {
            return new WorkOrderResult[size];
        }
    };
}
