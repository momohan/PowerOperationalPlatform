package com.handu.poweroperational.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.ZoomEnter.ZoomInEnter;
import com.flyco.animation.ZoomExit.ZoomOutBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplication;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.okhttputils.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * @创建人 柳梦
 * @时间 2016/10/10 10:30
 * @说明 BaseFragment
 * 若把初始化内容放到initData实现,就是采用Lazy方式加载的Fragment
 * 若不需要Lazy加载则initData方法内留空,初始化内容放到initViews即可
 * -注1: 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 * 可以调用mViewPager.setOffscreenPageLimit(size),若设置了该属性 则viewpager会缓存指定数量的Fragment
 * -注2: 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * -注3: 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 */

public abstract class BaseFragment extends Fragment {

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    protected boolean isVisible;                  //是否可见状态
    protected boolean isFirstLoad = true;         //是否第一次加载
    protected boolean isPrepared;                 //标志位，View已经初始化完成。
    protected Context mContext;
    protected BaseAnimatorSet mBasIn = new ZoomInEnter();
    protected BaseAnimatorSet mBasOut = new ZoomOutBottomExit();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(findView(), container, false);
        isFirstLoad = true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void onDestroyView() {
        //根据 Tag 取消请求
        OkHttpUtils.getInstance().cancelTag(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    protected abstract int findView();

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     * 这个时候 isCreate = false 表示不会重新创建实例
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     * 这个时候 isCreate = false 表示不会重新创建实例
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {
    }

    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            return;
        }
        initView();
        initData();
        isFirstLoad = false;
    }

    /**
     * @创建人 柳梦
     * @时间 2016/10/18 17:57
     * @说明 订阅后处理事件 如果需要请重载(注意是重载)
     * @Subscribe(threadMode = ThreadMode.MAIN)//在ui线程执行 操作ui必须在此线程
     * @Subscribe(threadMode = ThreadMode.BACKGROUND)//在后台线程执行
     * @Subscribe(threadMode = ThreadMode.ASYNC)//强制在后台执行
     * @Subscribe(threadMode = ThreadMode.POSTING)//默认方式, 在发送线程执行
     */
    public void onEvent(BaseEvent event) {
    }

    /**
     * @创建人 柳梦
     * @时间 2016/10/18 16:56
     * @说明 注册eventbus
     */
    protected void registerEvent() {
        EventBus.getDefault().register(this);
    }

    protected void gotoActivity(Class<? extends Activity> clazz, boolean finish) {
        Intent intent = new Intent(getActivity(), clazz);
        this.startActivity(intent);
        if (finish) {
            getActivity().finish();
        }
    }

    protected void gotoActivity(Class<? extends Activity> clazz, Bundle bundle, boolean finish) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        this.startActivity(intent);
        if (finish) {
            getActivity().finish();
        }
    }

    protected void gotoActivity(Class<? extends Activity> clazz, String action, Bundle bundle, boolean finish) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) intent.putExtras(bundle);
        if (!TextUtils.isEmpty(action)) intent.setAction(action);
        getActivity().startActivity(intent);
        if (finish) getActivity().finish();
    }

    protected void gotoActivityForResult(Class<? extends Activity> clazz, Bundle bundle, int code) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        getActivity().startActivityForResult(intent, code);
    }


    protected void gotoActivityForResult(Class<? extends Activity> clazz, String action, Bundle bundle, int code) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) intent.putExtras(bundle);
        if (!TextUtils.isEmpty(action)) intent.setAction(action);
        getActivity().startActivityForResult(intent, code);
    }

    /**
     * @param selectLimit
     */
    protected void initImagePicker(int selectLimit) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(true); //允许多选
        imagePicker.setCrop(false); //允许裁剪
        imagePicker.setSelectLimit(selectLimit);    //选中数量限制
    }

    /**
     * @param crop
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