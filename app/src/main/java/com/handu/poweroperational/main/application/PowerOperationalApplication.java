package com.handu.poweroperational.main.application;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.handu.poweroperational.R;
import com.handu.poweroperational.db.DBConstants;
import com.handu.poweroperational.db.model.User;
import com.handu.poweroperational.ex.CrashHandler;
import com.handu.poweroperational.main.service.LocationService;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.GlideImageLoader;
import com.igexin.sdk.PushManager;
import com.lzy.ninegrid.NineGridView;
import com.lzy.okhttputils.OkHttpUtils;

import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;

/**
 * 作者：柳梦 2016/9/13 09:33
 * 邮箱：mobyq@qq.com
 * 说明: Application
 */
public class PowerOperationalApplication extends Application {

    private static PowerOperationalApplication powerOperationalApplication;
    public static String pushCid = "";
    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        powerOperationalApplication = this;
        CrashHandler.getInstance().init(getApplicationContext());
        initLocation();
        initOkHttpUtils();
        NineGridView.setImageLoader(new GlideImageLoader());//设置默认图片加载器
        Sprinkles sprinkles = Sprinkles.init(getApplicationContext(), DBConstants.DB_NAME, DBConstants.DB_VERSION);
        sprinkles.addMigration(new Migration() {
            @Override
            protected void doMigration(SQLiteDatabase db) {
                db.execSQL("create table " +
                        DBConstants.USER_TABLE_NAME + " (" +
                        User.ID + " integer primary key autoincrement, " +
                        User.USERNAME + " text, " +
                        User.PASSWORD + " text)");
                AppLogger.e(getApplicationContext().getString(R.string.database_create_success));
            }
        });
    }

    private void initLocation() {
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
    }

    public static Context getContext() {
        return powerOperationalApplication.getApplicationContext();
    }

    //单例模式中获取唯一的SmartGridApplication实例
    public static PowerOperationalApplication getInstance() {
        return powerOperationalApplication;
    }

    //遍历所有Activity并finish
    public void exit() {
        PushManager.getInstance().turnOffPush(getApplicationContext());
        PushManager.getInstance().stopService(getApplicationContext());
        System.exit(0);
    }

    //配置OkHttpUtils
    private void initOkHttpUtils() {
        //必须调用初始化
        OkHttpUtils.init(this);
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        //HttpParams params = new HttpParams();
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要debug,缓存相关,cookie相关的就可以了
            OkHttpUtils.getInstance().debug("柳梦");
            //.addCommonParams(params);//设置公共参数
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}