package com.handu.poweroperational.db.model;


import com.handu.poweroperational.db.DBConstants;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * 作者：柳梦 2016/10/17 14:23
 * 邮箱：mobyq@qq.com
 * 说明:
 */
@Table(DBConstants.USER_TABLE_NAME)
public class User extends Model {

    public static final String ID = "_id";//主键
    public static final String USERNAME = "username";//用户名
    public static final String PASSWORD = "password";//用户密码

    @Key
    @AutoIncrement
    @Column(ID)
    public long _id;
    @Column(USERNAME)
    public String username;
    @Column(PASSWORD)
    public String password;
}
