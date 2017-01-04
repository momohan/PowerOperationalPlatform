package com.handu.poweroperational.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.handu.poweroperational.main.service.LocationUploadService;
import com.handu.poweroperational.utils.AppLogger;

/**
 * 重启位置上传服务
 */
public class RestartLocationUploadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, LocationUploadService.class);
        context.startService(i);
    }
}
