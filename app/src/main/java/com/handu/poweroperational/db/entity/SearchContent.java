package com.handu.poweroperational.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 柳梦 on 2017/3/2.
 * 搜索内容
 */
@Entity
public class SearchContent {

    @Id(autoincrement = true)
    public Long id;

    public String content;

    public String time;

    @Generated(hash = 1333288723)
    public SearchContent(Long id, String content, String time) {
        this.id = id;
        this.content = content;
        this.time = time;
    }

    @Generated(hash = 206336392)
    public SearchContent() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
