package com.handu.poweroperational.utils.speech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.animation.SlideExit.SlideTopExit;
import com.flyco.dialog.widget.MaterialDialog;
import com.handu.poweroperational.R;
import com.iflytek.cloud.SpeechUtility;


/**
 * 弹出提示框，下载服务组件
 */
public class SpeechApkInstaller {

    private BaseAnimatorSet mBasIn = new SlideBottomEnter();
    private BaseAnimatorSet mBasOut = new SlideTopExit();
    private Activity mActivity;

    public SpeechApkInstaller(Activity activity) {
        mActivity = activity;
    }

    public void install() {
        MaterialDialog dialog = new MaterialDialog(mActivity);
        dialog.setTitle("下载提示");
        dialog.btnNum(2)
                .content("检测到您未安装语记！\n是否前往下载语记？")
                .btnText(mActivity.getString(R.string.bt_cancel), mActivity.getString(R.string.bt_sure))
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
        dialog.setOnBtnClickL(dialog::dismiss, () -> {
            dialog.dismiss();
            String url = SpeechUtility.getUtility().getComponentUrl();
            String assetsApk = "SpeechService.apk";
            processInstall(mActivity, url, assetsApk);
        });
    }

    /**
     * 如果服务组件没有安装打开语音服务组件下载页面，进行下载后安装。
     */
    private boolean processInstall(Context context, String url, String assetsApk) {
        //直接下载方式
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
        return true;
    }
}
