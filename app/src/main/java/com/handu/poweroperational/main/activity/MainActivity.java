package com.handu.poweroperational.main.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.MaterialDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.main.activity.map.MapActivity;
import com.handu.poweroperational.main.activity.operation.OperationActivity;
import com.handu.poweroperational.main.activity.workorder.AddWorkOrderActivity;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.main.fragment.AlarmListFragment;
import com.handu.poweroperational.main.fragment.HomeFragment;
import com.handu.poweroperational.main.fragment.workorder.WorkOrderFragment;
import com.handu.poweroperational.main.receiver.RestartLocationUploadReceiver;
import com.handu.poweroperational.main.service.LocationUploadService;
import com.handu.poweroperational.main.service.UpdateService;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.request.callback.JsonDialogCallback;
import com.handu.poweroperational.ui.dialog.MainTopMenuDialog;
import com.handu.poweroperational.ui.widget.imageloader.ImageLoader;
import com.handu.poweroperational.ui.widget.imageloader.ImageLoaderUtil;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.DataCleanManager;
import com.handu.poweroperational.utils.PreferencesUtils;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.igexin.sdk.PushManager;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.widget.CircleImageView;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationBar.OnTabSelectedListener {

    private static final int REQUEST_READ_PHONE_STATE_PERMISSION = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_UPDATE_APP = 4;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int SELECT_IMAGE = 100;
    private static final int REQUEST_ALARM_NUMBER = 6;
    private static final int REQUEST_ALARM_NUMBER_ERROR = -6;
    private static final int REGISTER_CID = 7;
    private static final int REGISTER_CID_ERROR = -7;
    private String newApkUrl = "";
    private CircleImageView circleImageView;
    @Bind(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;
    PackageManager pkgManager;
    FragmentManager fm;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;
    private Intent LocationUploadIntent;
    private AlarmManager locationAlarmManager;
    private PendingIntent locationUploadPi;
    private BadgeItem alarmBadgeItem;
    private BadgeItem workOrderBadgeItem;
    private BadgeItem messageBadgeItem;
    private String currentTag = "HomeFragment";
    //默认显示主页
    private int currentPosition = 0;
    private HomeFragment homeFragment;
    private AlarmListFragment alarmListFragment;
    private WorkOrderFragment workOrderFragment;
    private Fragment currentFragment;
    private MenuItem itemRefresh;
    private MenuItem itemAdd;
    private MsgHandler msgHandler = new MsgHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        registerEvent();
        initView();
        initData();
        initFragment(savedInstanceState);
        initBottomNavigationBar();
        requestPhoneStatePermission();
        //默认显示0让其隐藏
        refreshBottomNavigationBar(0, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestItemNumber();
    }

    @Override
    public void onDestroy() {
        msgHandler.removeCallbacksAndMessages(null);
        OkHttpUtils.getInstance().cancelTag(this);
        stopService();
        super.onDestroy();
    }

    @Override
    protected void initView() {
        //getResources().getString(R.string.app_name)
        initToolBar(toolbar, getResources().getString(R.string.app_name), false, null, true, R.menu.menu_main,
                item -> {
                    switch (item.getItemId()) {
                        case R.id.action_device:
                            gotoActivity(BuildingAndDaActivity.class, false);
                            break;
                        case R.id.action_more:
                            MainTopMenuDialog dialog = new MainTopMenuDialog(mContext, flContainer);
                            dialog.show();
                            break;
                        case R.id.action_refresh:
                            EventBus.getDefault().post(new BaseEvent(EventType.homeRefresh.getType()));
                            break;
                        case R.id.action_add:
                            gotoActivity(AddWorkOrderActivity.class, false);
                            break;
                    }
                    return false;
                });
        itemRefresh = toolbar.getMenu().findItem(R.id.action_refresh);
        itemAdd = toolbar.getMenu().findItem(R.id.action_add);
        itemRefresh.setVisible(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        TextView tv = (TextView) view.findViewById(R.id.tv_user);
        tv.setText(getString(R.string.current_user) + PreferencesUtils.get(mContext, AppConstant.realName, "") + "");
        circleImageView = (CircleImageView) view.findViewById(R.id.iv_app_icon);
        circleImageView.setOnClickListener(v -> {
            initImagePicker(true);
            gotoActivityForResult(ImageGridActivity.class, null, SELECT_IMAGE);
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        initSpeech();
        pkgManager = getPackageManager();
        fm = getSupportFragmentManager();
        setUserImage("http://pics.sc.chinaz.com/Files/pic/faces/3848/00.gif");
    }

    //初始化语音
    private void initSpeech() {
        String app_id = getApplication().getString(R.string.speech_app_id);
        SpeechUtility.createUtility(getApplication(), SpeechConstant.APPID + "=" + app_id);
        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        Setting.setShowLog(true);
    }

    /**
     * 设置默认的fragment
     */
    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction ft = fm.beginTransaction().
                setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (savedInstanceState == null) {
            homeFragment = HomeFragment.newInstance("主页");
            alarmListFragment = AlarmListFragment.newInstance("告警");
            workOrderFragment = WorkOrderFragment.newInstance("工单");
            currentFragment = homeFragment;
            ft.add(R.id.fl_container, homeFragment, currentTag).hide(homeFragment).show(homeFragment).commit();
        } else {
            homeFragment = (HomeFragment) fm.findFragmentByTag("HomeFragment");
            alarmListFragment = (AlarmListFragment) fm.findFragmentByTag("AlarmListFragment");
            workOrderFragment = (WorkOrderFragment) fm.findFragmentByTag("WorkOrderFragment");
            ft.show(homeFragment).hide(alarmListFragment).hide(workOrderFragment).commit();
        }
    }

    /**
     * @创建人 柳梦
     * @时间 2016/10/12 10:33
     * @说明 初始化底部导航栏
     */
    private void initBottomNavigationBar() {
        BottomNavigationItem homeItem = new BottomNavigationItem(R.drawable.ic_navigation_home, getString(R.string.navigation_home_page)).setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        BottomNavigationItem operationItem = new BottomNavigationItem(R.drawable.ic_navigation_maintenance, getString(R.string.navigation_operation)).setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        BottomNavigationItem alarmItem = new BottomNavigationItem(R.drawable.ic_navigation_alarm, getString(R.string.navigation_alarm)).setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        BottomNavigationItem workOrderItem = new BottomNavigationItem(R.drawable.ic_navigation_workorder, getString(R.string.navigation_work_order)).setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        BottomNavigationItem messageItem = new BottomNavigationItem(R.drawable.ic_navigation_message, getString(R.string.navigation_message)).setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        alarmBadgeItem = new BadgeItem().setBorderWidth(1)/*Badge内容和边界的边距 类似于内边距*/.setBackgroundColorResource(R.color.red_light)/*Badge的背景色*/.setText("0")/*设置Badge的文字*/.setTextColor(Color.WHITE).setHideOnSelect(false)/*当点击这个BottomNavigationItem时，隐藏它身上的Badge*/;
        workOrderBadgeItem = new BadgeItem().setBorderWidth(1)/*Badge内容和边界的边距 类似于内边距*/.setBackgroundColorResource(R.color.red_light)/*Badge的背景色*/.setText("0")/*设置Badge的文字*/.setTextColor(Color.WHITE).setHideOnSelect(false)/*当点击这个BottomNavigationItem时，隐藏它身上的Badge*/;
        messageBadgeItem = new BadgeItem().setBorderWidth(1)/*Badge内容和边界的边距 类似于内边距*/.setBackgroundColorResource(R.color.red_light)/*Badge的背景色*/.setText("0")/*设置Badge的文字*/.setTextColor(Color.WHITE).setHideOnSelect(false)/*当点击这个BottomNavigationItem时，隐藏它身上的Badge*/;
        bottomNavigationBar
                .setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBarBackgroundColor(R.color.white)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .addItem(homeItem)
                .addItem(operationItem)
                .addItem(alarmItem.setBadgeItem(alarmBadgeItem))
                .addItem(workOrderItem.setBadgeItem(workOrderBadgeItem))
                .addItem(messageItem.setBadgeItem(messageBadgeItem))
                .setFirstSelectedPosition(currentPosition)
                .initialise();
    }

    /**
     * 读取设备信息相关权限
     */
    private void requestPhoneStatePermission() {
        //获取设备信息
        AndPermission.with(this)
                .requestCode(REQUEST_READ_PHONE_STATE_PERMISSION)
                .permission(
                        Manifest.permission.READ_PHONE_STATE
                ).send();
    }

    /**
     * 申请推送相关权限
     */
    private void requestStoragePermission(int code) {
        //读写sdcard权限非常重要
        AndPermission.with(this)
                .requestCode(code)
                .permission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).send();
    }

    /**
     * 申请定位相关权限
     */
    private void requestLocationPermission() {
        //位置信息权限
        AndPermission.with(MainActivity.this)
                .requestCode(REQUEST_LOCATION_PERMISSION)
                .permission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).send();
    }

    /**
     * 向第三方服务器注册cid 用于和自己的用户id绑定
     *
     * @param cid 个推cid
     */
    private void registerPushCid(String cid) {
        Map<String, String> map = new HashMap<>();
        map.put("ClientId", cid);
        map.put("UserId", PreferencesUtils.get(mContext, AppConstant.userId, "") + "");
        RequestServer.post(this, ServiceUrl.RegisterClientId, map, new JsonDialogCallback<String>(this, String.class, false) {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                msgHandler.sendEmptyMessage(REGISTER_CID);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                msgHandler.sendEmptyMessage(REGISTER_CID_ERROR);
            }
        });
    }

    /**
     * 请求各个角标数量
     */
    private void requestItemNumber() {
        msgHandler.sendEmptyMessageDelayed(REQUEST_ALARM_NUMBER, 1000);
    }

    //检测新版本
    private void checkNewVersion() {
        boolean b = Math.random() > 0.5;
        if (b) {
//            Tools.toastInfo(getString(R.string.current_version_is_new));
            Tools.toastInfo("正在安装更新，请稍后");
        } else {
            newApkUrl = "http://msoftdl.360.cn/mobile/shouji360/360safesis/360MobileSafe_7.7.0.1033.apk";
            final MaterialDialog dialog = new MaterialDialog(mContext);
            dialog.setTitle(R.string.check_new_version);
            dialog.btnNum(2)
                    .content(getString(R.string.has_new_app_version_do_update))
                    .btnText(getString(R.string.bt_cancel), getString(R.string.bt_sure))
                    .showAnim(mBasIn)
                    .dismissAnim(mBasOut)
                    .show();
            dialog.setOnBtnClickL(dialog::dismiss, () -> {
                dialog.dismiss();
                requestStoragePermission(REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_UPDATE_APP);
            });
        }
    }

    //下载apk
    private void startUpdateApk() {
        if (!Tools.isNetworkAvailable()) {
            return;
        }
        if (!Tools.isWiFiNetworkAvailable()) {
            MaterialDialog dialog = new MaterialDialog(mContext);
            dialog.setTitle(R.string.sweet_warn);
            dialog.btnNum(2)
                    .content(getString(R.string.confirm_download_not_in_wifi))
                    .btnText(getString(R.string.bt_cancel), getString(R.string.bt_sure))
                    .showAnim(mBasIn)
                    .dismissAnim(mBasOut)
                    .show();
            dialog.setOnBtnClickL(dialog::dismiss, () -> {
                dialog.dismiss();
                UpdateService.goUpdateService(MainActivity.this, newApkUrl);
            });
        } else {
            UpdateService.goUpdateService(MainActivity.this, newApkUrl);
        }
    }

    private void switchContent(Fragment from, Fragment to) {
        if (from != to) {
            currentFragment = to;
            FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            // 先判断是否被add过
            if (!to.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                ft.hide(from).add(R.id.fl_container, to, currentTag);
                //添加完之后再隐藏再显示，触发onHiddenChanged事件
                ft.hide(to).show(to).commit();
            } else {
                ft.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    /**
     * @创建人 柳梦
     * @时间 2016/10/12 10:33
     * @说明 刷新底部导航栏
     */
    private void refreshBottomNavigationBar(int... number) {
        if (bottomNavigationBar != null) {
            if (number[0] > 0) {
                alarmBadgeItem.setText(number[0] + "");
                if (alarmBadgeItem.isHidden())
                    alarmBadgeItem.show(true);
            } else {
                alarmBadgeItem.hide(true);
            }
            if (number[1] > 0) {
                workOrderBadgeItem.setText(number[1] + "");
                if (workOrderBadgeItem.isHidden())
                    workOrderBadgeItem.show(true);
            } else {
                workOrderBadgeItem.hide(true);
            }
            if (number[2] > 0) {
                messageBadgeItem.setText(number[2] + "");
                if (messageBadgeItem.isHidden())
                    messageBadgeItem.show(true);
            } else {
                messageBadgeItem.hide(true);
            }
        }
    }

    //设置头像
    private void setUserImage(String url) {
        ImageLoader.Builder builder = new ImageLoader.Builder();
        builder.url(url).imgView(circleImageView);
        ImageLoaderUtil.getInstance().loadImage(mContext, builder.build());
    }

    /**
     * 开启上传gps服务
     */
    private void startLocationUpload() {
        //先启动服务
        LocationUploadIntent = new Intent(this, LocationUploadService.class);
        startService(LocationUploadIntent);
        //再启动一个闹钟定时去启动广播
        locationAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int Time = 60 * 1000; // 这一分钟的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + Time;
        Intent i = new Intent(this, RestartLocationUploadReceiver.class);
        locationUploadPi = PendingIntent.getBroadcast(this, 0, i, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, locationUploadPi);
        } else {
            locationAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, locationUploadPi);
        }
    }

    //停止各种服务
    private void stopService() {
        if (LocationUploadIntent != null) {
            locationAlarmManager.cancel(locationUploadPi);//取消位置信息上传
            stopService(LocationUploadIntent);
        }
        //销毁时停用推送服务
        PushManager.getInstance().turnOffPush(this.getApplicationContext());
        PushManager.getInstance().stopService(this.getApplicationContext());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
    public void onEvent(BaseEvent event) {
        if (event.eventType == EventType.registerCid.getType()) {
            String cid = (String) event.data;
            if (!TextUtils.isEmpty(cid)) {
                registerPushCid(cid);
            }
        } else if (event.eventType == EventType.newWorkOrderUpdate.getType() || event.eventType == EventType.alarmNumUpdate.getType()) {
            requestItemNumber();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode) {
                if (requestCode == REQUEST_READ_PHONE_STATE_PERMISSION) {
                    //请求内存卡读写权限
                    requestStoragePermission(REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
                    //启动推送服务
                    PushManager.getInstance().initialize(MainActivity.this, null);
                    PushManager.getInstance().turnOnPush(MainActivity.this);
                    //请求位置权限
                    requestLocationPermission();
                } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
                    //启动位置上传服务
                    startLocationUpload();
                } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_UPDATE_APP) {
                    if (!TextUtils.isEmpty(newApkUrl))
                        startUpdateApk();
                }
            }

            @Override
            public void onFailed(int requestCode) {
                if (requestCode == REQUEST_READ_PHONE_STATE_PERMISSION) {
                    requestLocationPermission();
                    showSnackbar(toolbar, "设备信息权限申请被禁止，推送功能未启动!", getString(R.string.action_close), v -> {
                    }, false);
                } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
                    requestLocationPermission();
                    showSnackbar(toolbar, "内存卡读写权限申请被禁止，推送功能未启动!", getString(R.string.action_close), v -> {
                    }, false);
                } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
                    showSnackbar(toolbar, "位置权限申请被禁止，定位功能未启动!", getString(R.string.action_close), v -> {
                    }, false);
                } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_UPDATE_APP) {
                    showSnackbar(toolbar, "内存卡读写权限申请被禁止，下载失败...", getString(R.string.action_close), v -> {
                    }, false);
                }
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        final MaterialDialog dialog;
        switch (id) {
            case R.id.nav_cache_clear:
                dialog = new MaterialDialog(mContext);
                dialog.setTitle(R.string.cache_clear);
                dialog.btnNum(2)
                        .content("当前缓存：" + DataCleanManager.getTotalCacheSize(mContext) + "，确定要清除吗?")
                        .btnText(getString(R.string.bt_cancel), getString(R.string.bt_sure))
                        .showAnim(mBasIn)
                        .dismissAnim(mBasOut)
                        .show();
                dialog.setOnBtnClickL(dialog::dismiss, () -> {
                    dialog.dismiss();
                    DataCleanManager.clearAllCache(mContext);
                });
                break;
            case R.id.nav_check_version:
                checkNewVersion();
                break;
            case R.id.nav_reset_login:
                dialog = new MaterialDialog(mContext);
                dialog.setTitle(R.string.change_user);
                dialog.btnNum(2)
                        .content(getString(R.string.confirm_exit_current_user))
                        .btnText(getString(R.string.bt_cancel), getString(R.string.bt_sure))
                        .showAnim(mBasIn)
                        .dismissAnim(mBasOut)
                        .show();
                dialog.setOnBtnClickL(dialog::dismiss, () -> {
                    dialog.dismiss();
                    String username = PreferencesUtils.get(mContext, AppConstant.username, "") + "";
                    //清除所有数据
                    PreferencesUtils.clear(mContext);
                    //再保存用户名用于查找数据库最后一个登录的用户
                    PreferencesUtils.put(mContext, AppConstant.username, username);
                    gotoActivity(LoginActivity.class, true);
                });
                break;
            case R.id.nav_qr_code_scan:
                gotoActivityForResult(QRCodeScanActivity.class, null, QRCodeScanActivity.REQUEST_CODE);
                break;
//            case R.id.nav_ndk:
//                gotoActivity(NdkTestActivity.class, false);
//                break;
            case R.id.nav_map:
                Bundle bundle = new Bundle();
                bundle.putString(MapActivity.SEARCH_CONTENT, "盛世嘉苑");
                gotoActivity(MapActivity.class, bundle, false);
                break;
            case R.id.nav_speech:
                SpeechActivity.runSpeechActivityForResult(MainActivity.this);
                break;
            case R.id.nav_setting:
                final String[] stringItems = {"退出App", "修复"};
                final ActionSheetDialog sheetDialog = new ActionSheetDialog(mContext, stringItems, null);
                sheetDialog.isTitleShow(false).show();
                sheetDialog.setOnOperItemClickL((parent, view, position, id1) -> {
                    sheetDialog.dismiss();
                    switch (position) {
                        case 0:
                            PowerOperationalApplicationLike.getInstance().exit();
                            break;
                        case 1:
                            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),
                                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                            break;
                    }
                });
                break;
            case R.id.nav_download_manager:
                DownLoadActivity.runActivity(mContext, null, null);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                currentPosition = 0;
                currentTag = "HomeFragment";
                switchContent(currentFragment, homeFragment);
                itemRefresh.setVisible(true);
                itemAdd.setVisible(false);
                break;
            case 1:
                gotoActivity(OperationActivity.class, false);
                bottomNavigationBar.selectTab(currentPosition);
                break;
            case 2:
                currentPosition = 2;
                currentTag = "AlarmListFragment";
                switchContent(currentFragment, alarmListFragment);
                itemRefresh.setVisible(false);
                itemAdd.setVisible(false);
                break;
            case 3:
                currentPosition = 3;
                currentTag = "WorkOrderFragment";
                switchContent(currentFragment, workOrderFragment);
                itemRefresh.setVisible(false);
                itemAdd.setVisible(true);
                break;
            case 4:
                gotoActivity(PushMessageActivity.class, false);
                bottomNavigationBar.selectTab(currentPosition);
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (requestCode == SELECT_IMAGE && data != null) {
                ArrayList<ImageItem> imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imageItems != null && imageItems.size() > 0) {
                    for (int i = 0; i < imageItems.size(); i++) {
                        setUserImage("file://" + imageItems.get(i).path);
                    }
                }
            }
        } else {
            if (requestCode == QRCodeScanActivity.REQUEST_CODE && data != null) {
                gotoActivity(QRCodeScanResultActivity.class, data.getExtras(), false);
            } else if (requestCode == SpeechActivity.REQUEST_CODE && data != null) {
                Tools.toastInfo(data.getStringExtra(SpeechActivity.REQUEST_RESULT));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // 返回键最小化程序
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    //静态内部类弱引用当前对象防止内存泄漏
    static class MsgHandler extends Handler {

        private WeakReference reference;

        MsgHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = (MainActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case REQUEST_ALARM_NUMBER:
                        int a = (int) (Math.random() * 10);
                        int b = (int) (Math.random() * 10);
                        int c = (int) (Math.random() * 10);
                        activity.refreshBottomNavigationBar(a, b, c);
                        break;
                    case REQUEST_ALARM_NUMBER_ERROR:
                        Exception e = (Exception) msg.obj;
                        AppLogger.e(e.getMessage());
                        break;
                    case REGISTER_CID:
                        AppLogger.e(activity.getString(R.string.cid_register_success));
                        break;
                    case REGISTER_CID_ERROR:
                        Tools.toastError(activity.getString(R.string.cid_register_failure));
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
}
