package com.handu.poweroperational.request.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.view.Window;

import com.handu.poweroperational.R;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

/**
 * ================================================
 * 作    者：柳梦
 * 版    本：1.0
 * 创建日期：2016/4/8
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class StringDialogCallback extends StringCallback {

    private ProgressDialog dialog;
    private boolean showDialog = true;

    public StringDialogCallback(Activity activity) {
        initDialog(activity, activity.getResources().getString(R.string.requesting), true);
    }

    public StringDialogCallback(Activity activity, String message) {
        initDialog(activity, message, true);
    }

    public StringDialogCallback(Activity activity, boolean showDialog) {
        initDialog(activity, activity.getResources().getString(R.string.requesting), showDialog);
    }

    private void initDialog(Activity activity, String message, boolean showDialog) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        this.showDialog = showDialog;
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
    public void onAfter(@Nullable String s, @Nullable Exception e) {
        super.onAfter(s, e);
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
