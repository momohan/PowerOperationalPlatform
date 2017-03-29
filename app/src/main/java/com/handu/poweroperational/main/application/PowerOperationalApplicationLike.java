package com.handu.poweroperational.main.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.multidex.MultiDex;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.handu.poweroperational.BuildConfig;
import com.handu.poweroperational.db.manager.GreenDaoManager;
import com.handu.poweroperational.ex.CrashHandler;
import com.handu.poweroperational.main.service.LocationService;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.GlideImageLoader;
import com.handu.poweroperational.utils.thinker.TinkerManager;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.ninegrid.NineGridView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * 作者：柳梦 on 2017/3/13 10:28
 * 邮箱：mobyq@qq.com
 * 公司：成都汉度科技
 * 描述：热修复ApplicationLike
 */

//这里的application是manifest里面的 不需要实际写出类
@DefaultLifeCycle(application = "com.handu.poweroperational.main.application.PowerOperationalApplication",
        //loaderClassName, 我们这里使用默认即可!
        loaderClass = "com.tencent.tinker.loader.TinkerLoader",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false
)
public class PowerOperationalApplicationLike extends ApplicationLike {

    @SuppressLint("StaticFieldLeak")//这里牺牲内存泄漏，来获取当前ApplicationLike实例
    private static PowerOperationalApplicationLike applicationLike;
    public static LocationService locationService;
    public static String pushCid = null;
    public static BDLocation location = null;
    //存放所有的Activity
    private Set<Activity> list;

    public PowerOperationalApplicationLike(Application applicationLike, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(applicationLike, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    public static Context getContext() {
        return applicationLike.getApplication().getApplicationContext();
    }

    public Set<Activity> getActivityList() {
        return list;
    }

    //单例模式中获取唯一的applicationLike实例
    public static PowerOperationalApplicationLike getInstance() {
        return applicationLike;
    }

    //以前application写在onCreate的东西搬到这里来初始化
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        applicationLike = this;
        MultiDex.install(base);
        TinkerManager.setTinkerApplicationLike(this);
        TinkerManager.setUpgradeRetryEnable(true);
        //TinkerInstaller.setLogIml(new MyLogImp());
        TinkerManager.installTinker(this);
        list = new HashSet<>();
        initImageLoader();
        initGreenDao();
        initLocation();
        initOkHttpUtils();
        initCash();
    }

    //初始化GreenDao数据库框架
    private void initGreenDao() {
        //greenDao全局配置,只希望有一个数据库操作对象
        GreenDaoManager.getInstance();
    }

    //配置图片加载
    private void initImageLoader() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(true); //允许多选
        imagePicker.setCrop(false); //允许裁剪
        NineGridView.setImageLoader(new GlideImageLoader());//设置默认图片加载器
    }

    //配置报错信息
    private void initCash() {
        CrashHandler.getInstance().init();
    }

    //配置位置信息
    private void initLocation() {
        /***
         * 初始化定位sdk
         */
        locationService = new LocationService(getApplication());
        Vibrator mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplication());
    }

    //配置OkHttpUtils
    private void initOkHttpUtils() {
        //必须调用初始化
        OkHttpUtils.init(getApplication());
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        //HttpParams params = new HttpParams();
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要debug,缓存相关,cookie相关的就可以了
            if (BuildConfig.DEBUG)
                OkHttpUtils.getInstance().debug(AppConstant.TAG);
            OkHttpUtils.getInstance().setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE);
            //.addCommonParams(params);//设置公共参数
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : list) {
            activity.finish();
        }
    }
}
