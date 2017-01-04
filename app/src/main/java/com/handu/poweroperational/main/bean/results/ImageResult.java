package com.handu.poweroperational.main.bean.results;

/**
 * Created by 柳梦 on 2016/11/30.
 * 图片返回结果
 */

public class ImageResult {

    private String Id;
    private String DetailId;
    private String ImgPath;
    private String ThumbnailPath;
    private String RecordTime;
    private int Type;
    private int State;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getDetailId() {
        return DetailId;
    }

    public void setDetailId(String DetailId) {
        this.DetailId = DetailId;
    }

    public String getImgPath() {
        return ImgPath;
    }

    public void setImgPath(String ImgPath) {
        this.ImgPath = ImgPath;
    }

    public String getThumbnailPath() {
        return ThumbnailPath;
    }

    public void setThumbnailPath(String ThumbnailPath) {
        this.ThumbnailPath = ThumbnailPath;
    }

    public String getRecordTime() {
        return RecordTime;
    }

    public void setRecordTime(String RecordTime) {
        this.RecordTime = RecordTime;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }
}

