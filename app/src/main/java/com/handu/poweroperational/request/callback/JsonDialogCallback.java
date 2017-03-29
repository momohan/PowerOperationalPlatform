package com.handu.poweroperational.request.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.view.Window;

import com.handu.poweroperational.R;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttputils.request.BaseRequest;

import java.lang.reflect.Type;

/**
 * ================================================
 * 作    者：柳梦
 * 版    本：1.0
 * 创建日期：2016/9/14
 * 描    述：对于网络请求是否需要弹出进度对话框
 * 修订历史：
 * ================================================
 */
public abstract class JsonDialogCallback<T> extends JsonCallback<T> {

    private ProgressDialog dialog;
    //是否需要显示dialog
    private boolean showDialog = true;

    private void initDialog(Activity activity) {
        initDialog(activity, activity.getResources().getString(R.string.requesting), true);
    }

    private void initDialog(Activity activity, boolean showDialog) {
        initDialog(activity, activity.getResources().getString(R.string.requesting), showDialog);
    }

    private void initDialog(Activity activity, String message, boolean showDialog) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);
        this.showDialog = showDialog;
    }

    public JsonDialogCallback(Activity activity, Class<T> clazz) {
        super(clazz);
        initDialog(activity);
    }

    public JsonDialogCallback(Activity activity, Class<T> clazz, boolean showDialog) {
        super(clazz);
        initDialog(activity, showDialog);
    }

    public JsonDialogCallback(Activity activity, Class<T> clazz, String str) {
        super(clazz);
        initDialog(activity, str, true);
    }

    public JsonDialogCallback(Activity activity, Type type) {
        super(type);
        initDialog(activity);
    }

    public JsonDialogCallback(Activity activity, Type type, boolean showDialog) {
        super(type);
        initDialog(activity, showDialog);
    }

    public JsonDialogCallback(Activity activity, Type type, String str) {
        super(type);
        initDialog(activity, str, true);
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        if (!Tools.isNetworkAvailable()) return;
        if (showDialog) {
            //网络请求前显示对话框
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    @Override
    public void onAfter(@Nullable T t, @Nullable Exception e) {
        super.onAfter(t, e);
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
