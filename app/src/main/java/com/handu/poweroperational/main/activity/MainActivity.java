package com.handu.poweroperational.main.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.base.BaseEvent;
import com.handu.poweroperational.callback.JsonDialogCallback;
import com.handu.poweroperational.main.bean.constants.EventType;
import com.handu.poweroperational.main.fragment.AlarmListFragment;
import com.handu.poweroperational.main.fragment.HomeFragment;
import com.handu.poweroperational.main.fragment.workorder.WorkOrderFragment;
import com.handu.poweroperational.main.receiver.RestartLocationUploadReceiver;
import com.handu.poweroperational.main.service.LocationUploadService;
import com.handu.poweroperational.request.OkHttpRequest;
import com.handu.poweroperational.ui.dialog.MainTopMenuDialog;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.DataCleanManager;
import com.handu.poweroperational.utils.PreferencesUtils;
import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.igexin.sdk.PushManager;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.BaseRequest;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private static final int REQUEST_LOCATION_PERMISSION = 2;
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
    private BottomNavigationItem homeItem;
    private BottomNavigationItem operationItem;
    private BottomNavigationItem alarmItem;
    private BottomNavigationItem workOrderItem;
    private BadgeItem alarmBadgeItem;
    private BadgeItem workOrderBadgeItem;
    private String currentTag = "HomeFragment";
    private int currentPosition = 0;
    private HomeFragment homeFragment;
    private AlarmListFragment alarmListFragment;
    private WorkOrderFragment workOrderFragment;
    //    private OperationFragment operationFragment;
    private Fragment currentFragment;
    private MenuItem menuItem;

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
    }

    @Override
    public void onDestroy() {
        exit();
        OkHttpUtils.getInstance().cancelTag(this);
        super.onDestroy();
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
    private void requestStoragePermission() {
        //读写sdcard权限非常重要
        AndPermission.with(this)
                .requestCode(REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode) {
                if (requestCode == REQUEST_READ_PHONE_STATE_PERMISSION) {
                    //请求内存卡读写权限
                    requestStoragePermission();
                } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
                    //请求位置权限
                    requestLocationPermission();
                    //启动推送服务
                    PushManager.getInstance().initialize(MainActivity.this, null);
                    PushManager.getInstance().turnOnPush(MainActivity.this);
                } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
                    //启动位置上传服务
                    startLocationUpload();
                }
            }

            @Override
            public void onFailed(int requestCode) {
                if (requestCode == REQUEST_READ_PHONE_STATE_PERMISSION) {
                    requestLocationPermission();
                    showSnackbar(toolbar, "设备信息权限申请被禁止，推送功能未启动!", "关闭", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, false);
                } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
                    requestLocationPermission();
                    showSnackbar(toolbar, "内存卡读写权限申请被禁止，推送功能未启动!", "关闭", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, false);
                } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
                    showSnackbar(toolbar, "位置权限申请被禁止，定位功能未启动!", "关闭", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, false);
                }
            }
        });
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
    protected void initView() {
        initToolBar(toolbar, getResources().getString(R.string.app_name), false, null, true, R.menu.main,
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_device:
                                gotoActivity(SearchActivity.class, false);
                                break;
                            case R.id.action_more:
                                MainTopMenuDialog dialog = new MainTopMenuDialog(mContext, flContainer);
                                dialog.show();
                                break;
                            case R.id.action_refresh:
                                EventBus.getDefault().post(new BaseEvent(EventType.homeRefresh.getType()));
                                break;
                        }
                        return false;
                    }
                });
        menuItem = toolbar.getMenu().findItem(R.id.action_refresh);
        menuItem.setVisible(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        TextView tv = (TextView) view.findViewById(R.id.tv_user);
        tv.setText(getString(R.string.current_user) + PreferencesUtils.get(mContext, AppConstant.realName, "") + "");
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        pkgManager = getPackageManager();
        fm = getSupportFragmentManager();
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
        OkHttpRequest.post(this, ServiceUrl.RegisterClientId, map, new JsonDialogCallback<String>(this, String.class) {
            @Override
            public void onBefore(BaseRequest request) {

            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Tools.showToast(getString(R.string.cid_register_failure));
            }
        });

    }

    /**
     * 请求告警数量，工单数量
     */
    private void requestItemNumber() {
        refreshBottomNavigationBar(2, 2);
    }

    /**
     * 设置默认的fragment
     */
    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction ft = fm.beginTransaction().
                setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (savedInstanceState == null) {
            homeFragment = HomeFragment.newInstance("主页");
//            operationFragment = OperationFragment.newInstance("运维");
            alarmListFragment = AlarmListFragment.newInstance("告警");
            workOrderFragment = WorkOrderFragment.newInstance("工单");
            currentFragment = homeFragment;
            ft.add(R.id.fl_container, homeFragment, currentTag).hide(homeFragment).show(homeFragment).commit();
        } else {
            homeFragment = (HomeFragment) fm.findFragmentByTag("HomeFragment");
//            operationFragment = (OperationFragment) fm.findFragmentByTag("OperationFragment");
            alarmListFragment = (AlarmListFragment) fm.findFragmentByTag("AlarmListFragment");
            workOrderFragment = (WorkOrderFragment) fm.findFragmentByTag("WorkOrderFragment");
            ft.show(homeFragment)
//                    .hide(operationFragment)
                    .hide(alarmListFragment).hide(workOrderFragment).commit();
        }
    }

    /**
     * @创建人 柳梦
     * @时间 2016/10/12 10:33
     * @说明 初始化底部导航栏
     */
    private void initBottomNavigationBar() {
        homeItem = new BottomNavigationItem(R.drawable.ic_navigation_home, "主页").setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        operationItem = new BottomNavigationItem(R.drawable.ic_navigation_maintenance, "运维").setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        alarmItem = new BottomNavigationItem(R.drawable.ic_navigation_alarm, "告警").setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        workOrderItem = new BottomNavigationItem(R.drawable.ic_navigation_workorder, "工单").setInActiveColor(R.color.black).setActiveColorResource(R.color.red_light);
        alarmBadgeItem = new BadgeItem().setBorderWidth(1)/*Badge内容和边界的边距 类似于内边距*/.hide().setBackgroundColorResource(R.color.red_light)/*Badge的背景色*/.setText("0")/*设置Badge的文字*/.setTextColor(Color.WHITE).setHideOnSelect(false)/*当点击这个BottomNavigationItem时，隐藏它身上的Badge*/;
        workOrderBadgeItem = new BadgeItem().setBorderWidth(1)/*Badge内容和边界的边距 类似于内边距*/.hide().setBackgroundColorResource(R.color.red_light)/*Badge的背景色*/.setText("0")/*设置Badge的文字*/.setTextColor(Color.WHITE).setHideOnSelect(false)/*当点击这个BottomNavigationItem时，隐藏它身上的Badge*/;
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBarBackgroundColor(R.color.white);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.addItem(homeItem).addItem(operationItem).addItem(alarmItem.setBadgeItem(alarmBadgeItem)).addItem(workOrderItem.setBadgeItem(workOrderBadgeItem)).setFirstSelectedPosition(currentPosition);
        bottomNavigationBar.initialise();
        bottomNavigationBar.setTabSelectedListener(this);
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
                alarmBadgeItem.show(true);
            } else {
                alarmBadgeItem.hide(true);
            }
            if (number[1] > 0) {
                workOrderBadgeItem.setText(number[1] + "");
                workOrderBadgeItem.show(true);
            } else {
                workOrderBadgeItem.hide(true);
            }
            bottomNavigationBar.removeItem(homeItem);
            bottomNavigationBar.removeItem(alarmItem);
            bottomNavigationBar.removeItem(operationItem);
            bottomNavigationBar.removeItem(workOrderItem);
            bottomNavigationBar.addItem(homeItem).addItem(operationItem).addItem(alarmItem.setBadgeItem(alarmBadgeItem)).addItem(workOrderItem.setBadgeItem(workOrderBadgeItem)).setFirstSelectedPosition(currentPosition);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
                dialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        DataCleanManager.clearAllCache(mContext);
                    }
                });
                break;
            case R.id.nav_check_version:
                showAlertDialog(getString(R.string.current_version_is_new));
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
                dialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        String username = PreferencesUtils.get(mContext, AppConstant.username, "") + "";
                        //清除所有数据
                        PreferencesUtils.clear(mContext);
                        //再保存用户名用于查找数据库最后一个登录的用户
                        PreferencesUtils.put(mContext, AppConstant.username, username);
                        gotoActivity(LoginActivity.class, true);
                    }
                });
                break;
            case R.id.nav_qr_code_scan:
                gotoActivityForResult(QRCodeScanActivity.class, null, QRCodeScanActivity.REQUEST_CODE);
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
                menuItem.setVisible(true);
                break;
            case 1:
//                currentPosition = 1;
//                currentTag = "OperationFragment";
//                switchContent(currentFragment, operationFragment);
                gotoActivity(OperationActivity.class, false);
                bottomNavigationBar.selectTab(currentPosition);
                break;
            case 2:
                currentPosition = 2;
                currentTag = "AlarmListFragment";
                switchContent(currentFragment, alarmListFragment);
                menuItem.setVisible(false);
                break;
            case 3:
                currentPosition = 3;
                currentTag = "WorkOrderFragment";
                switchContent(currentFragment, workOrderFragment);
                menuItem.setVisible(false);
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    /**
     * 开启上传gps服务
     */
    private void startLocationUpload() {
        //先启动服务
        LocationUploadIntent = new Intent(this, LocationUploadService.class);
        startService(LocationUploadIntent);

        //再启动一个闹钟定时去启动服务
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

    private void exit() {
        if (LocationUploadIntent != null) {
            locationAlarmManager.cancel(locationUploadPi);//取消位置信息上传
            stopService(LocationUploadIntent);
        }
        //销毁时停用推送服务
        PushManager.getInstance().stopService(this.getApplicationContext());
        PushManager.getInstance().turnOffPush(this.getApplicationContext());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QRCodeScanActivity.REQUEST_CODE) {
            if (data != null) {
                gotoActivity(QrCodeScanResultActivity.class, data.getExtras(), false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
