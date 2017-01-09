package com.handu.poweroperational.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.animation.SlideExit.SlideTopExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplication;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.SystemBarTintManager;
import com.lzy.okhttputils.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected BaseAnimatorSet mBasIn = new SlideBottomEnter();
    protected BaseAnimatorSet mBasOut = new SlideTopExit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initSystemBarTint();
    }

    @Override
    protected void onDestroy() {
        //根据 Tag 取消请求
        OkHttpUtils.getInstance().cancelTag(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @创建人 柳梦
     * @时间 2016/10/18 16:56
     * @说明 注册eventbus
     */
    protected void registerEvent() {
        EventBus.getDefault().register(this);
    }

    /**
     * @创建人 柳梦
     * @时间 2016/10/18 17:57
     * @说明 订阅后处理事件 如果需要请重载(注意是重载)
     * @Subscribe(threadMode = ThreadMode.MAIN)在ui线程执行 操作ui必须在此线程
     * @Subscribe(threadMode = ThreadMode.BACKGROUND)在后台线程执行
     * @Subscribe(threadMode = ThreadMode.ASYNC)强制在后台执行
     * @Subscribe(threadMode = ThreadMode.POSTING)默认方式, 在发送线程执行
     */
    public void onEvent(BaseEvent event) {
    }

    /**
     * 子类可以重写改变状态栏颜色
     */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }

    /**
     * 子类可以重写决定是否使用透明状态栏
     */
    protected boolean translucentStatusBar() {
        return false;
    }

    /**
     * 设置状态栏颜色
     */
    protected void initSystemBarTint() {
        Window window = getWindow();
        if (translucentStatusBar()) {
            // 设置状态栏全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            return;
        }
        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(setStatusBarColor());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4-5.0使用三方工具类
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(setStatusBarColor());
        }
    }

    /**
     * 获取主题色
     */
    protected int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取深主题色
     */
    protected int getDarkColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    /**
     * @param toolbar
     * @param title                     标题
     * @param left                      是否有返回按钮
     * @param navigationOnClickListener 返回监听
     * @param right                     是否右边菜单
     * @param layoutResID               右边菜单id
     * @param menuItemClickListener     右边菜单监听
     */
    protected void initToolBar(Toolbar toolbar, String title, boolean left, View.OnClickListener navigationOnClickListener, boolean right, Integer layoutResID, Toolbar.OnMenuItemClickListener menuItemClickListener) {
        toolbar.setTitle(title);/*设置主标题 setSupportActionBar(toolbar);*/
        if (left) {
            toolbar.setNavigationIcon(R.mipmap.ic_back);/*设置导航栏图标*/
            if (navigationOnClickListener != null)
                toolbar.setNavigationOnClickListener(navigationOnClickListener);
        }
        if (right) {
            if (menuItemClickListener != null)
                toolbar.setOnMenuItemClickListener(menuItemClickListener);
            if (layoutResID != null)
                toolbar.inflateMenu(layoutResID);/*设置右上角的填充菜单*/
            else
                toolbar.inflateMenu(R.menu.base_toolbar_menu);/*设置右上角的填充菜单*/
        }
    }

    /**
     * @param toolbar
     * @param title
     * @param left
     * @param navigationOnClickListener
     */
    protected void initToolBar(Toolbar toolbar, String title, boolean left, View.OnClickListener navigationOnClickListener) {
        initToolBar(toolbar, title, left, navigationOnClickListener, false, null, null);
    }

    protected abstract void initView();

    protected abstract void initData();

    protected void gotoActivity(Class<? extends Activity> clazz, boolean finish) {
        Intent intent = new Intent(this, clazz);
        this.startActivity(intent);
        if (finish) {
            this.finish();
        }
    }

    protected void gotoActivity(Class<? extends Activity> clazz, Bundle bundle, boolean finish) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        this.startActivity(intent);
        if (finish) {
            this.finish();
        }
    }

    protected void gotoActivity(Class<? extends Activity> clazz, String action, Bundle bundle, boolean finish) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }
        this.startActivity(intent);
        if (finish) {
            this.finish();
        }
    }

    protected void gotoActivityForResult(Class<? extends Activity> clazz, Bundle bundle, int code) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        this.startActivityForResult(intent, code);
    }

    protected void gotoActivityForResult(Class<? extends Activity> clazz, String action, Bundle bundle, int code) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) intent.putExtras(bundle);
        if (!TextUtils.isEmpty(action)) intent.setAction(action);
        this.startActivityForResult(intent, code);
    }


    protected void gotoActivitySetResult(Bundle bundle, int code) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        setResult(code, intent);
        this.finish();
    }

    /**
     * @param selectLimit 选中数量
     */
    protected void initImagePicker(int selectLimit) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(true); //允许多选
        imagePicker.setCrop(false); //允许裁剪
        imagePicker.setSelectLimit(selectLimit);    //选中数量限制
    }

    /**
     * @param crop 允许裁剪
     */
    protected void initImagePicker(boolean crop) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(false); //允许多选
        imagePicker.setCrop(crop); //允许裁剪
    }

    protected void showAlertDialog(String msg) {
        showAlertDialog(getString(R.string.sweet_warn), msg);
    }

    protected void showAlertDialog(String title, String msg) {
        final MaterialDialog dialog = new MaterialDialog(mContext);
        dialog.setTitle(title);
        dialog.btnNum(1)
                .content(msg)
                .btnText(getString(R.string.action_close))
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
    }

    /**
     * @param view
     * @param msg           内容
     * @param action        按钮text
     * @param clickListener 监听器
     * @param duration      显示时间
     */
    protected void showSnackbar(View view, String msg, String action, View.OnClickListener clickListener, int duration) {
        Snackbar snackbar = Snackbar.make(view, msg, duration).setAction(action, clickListener)
                .setActionTextColor(ContextCompat.getColor(PowerOperationalApplication.getContext(), R.color.white));
        Snackbar.SnackbarLayout ve = (Snackbar.SnackbarLayout) snackbar.getView();
        ve.setBackgroundColor(ContextCompat.getColor(PowerOperationalApplication.getContext(), R.color.colorPrimary));
        snackbar.show();
    }

    /**
     * @param view
     * @param msg
     * @param duration
     */
    protected void showSnackbar(View view, String msg, int duration) {
        showSnackbar(view, msg, null, null, duration);
    }

    /**
     * @param view
     * @param msg
     */
    protected void showSnackbar(View view, String msg) {
        showSnackbar(view, msg, null, null, 3000);
    }

    /**
     * @param view
     * @param msg
     * @param dismiss
     */
    protected void showSnackbar(View view, String msg, String action, View.OnClickListener clickListener, boolean dismiss) {
        if (!dismiss)
            showSnackbar(view, msg, action, clickListener, Snackbar.LENGTH_INDEFINITE);
        else
            showSnackbar(view, msg, null, null, 3000);
    }
}