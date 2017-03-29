package com.handu.poweroperational.main.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.Tools;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerServiceInternals;

import java.io.File;

/**
 * 作者：柳梦 on 2017/3/14 11:11
 * 邮箱：mobyq@qq.com
 * 公司：成都汉度科技
 * 描述：修复结果回调服务
 */
public class PatchResultService extends DefaultTinkerResultService {

    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            AppLogger.e("修复结果为空");
            return;
        }
        AppLogger.e("修复结果: " + result.toString());

        //first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());
        Handler handler = new Handler(Looper.getMainLooper());
        // is success and newPatch, it is nice to delete the raw file, and restart at once
        // for old patch, you can't delete the patch file
        if (result.isSuccess) {
            deleteRawPatchFile(new File(result.rawPatchFilePath));
            //not like TinkerResultService, I want to restart just when I am at background!
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            if (checkIfNeedKill(result)) {
                handler.postDelayed(this::restartProcess, 1000);
                handler.post(() -> Tools.toastInfo("修复成功，3秒后将重启App..."));
            } else {
                AppLogger.i("已经修复过了");
            }
        } else {
            handler.post(() -> Tools.toastError("修复失败"));
        }
    }

    /**
     * you can restart your process through service or broadcast
     */
    private void restartProcess() {
        //you can send service or broadcast intent to restart your process
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }

    static class ScreenState {

        interface IOnScreenOff {
            void onScreenOff();
        }

        ScreenState(Context context, final IOnScreenOff onScreenOffInterface) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent in) {
                    String action = in == null ? "" : in.getAction();
                    AppLogger.i("ScreenReceiver action：" + action);
                    if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        context.unregisterReceiver(this);
                        if (onScreenOffInterface != null) {
                            onScreenOffInterface.onScreenOff();
                        }
                    }
                }
            }, filter);
        }
    }
}
