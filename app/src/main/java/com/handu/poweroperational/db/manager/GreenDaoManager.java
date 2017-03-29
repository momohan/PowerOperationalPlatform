package com.handu.poweroperational.db.manager;

import com.handu.poweroperational.db.DBConstants;
import com.handu.poweroperational.db.dao.DaoMaster;
import com.handu.poweroperational.db.dao.DaoSession;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;

/**
 * Created by 柳梦  2017/3/1.
 */

public class GreenDaoManager {

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static GreenDaoManager mInstance; //单例

    private GreenDaoManager() {
        initialize();
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {//保证异步处理安全操作
                if (mInstance == null) {
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

    public void initialize() {
        if (mInstance == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new
                    DaoMaster.DevOpenHelper(PowerOperationalApplicationLike.getInstance().getApplication(), DBConstants.DB_NAME, null);//此处为自己需要处理的表
            mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession();
        }
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }
}
