package com.handu.poweroperational.main.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;
import com.handu.poweroperational.request.RequestServer;
import com.handu.poweroperational.request.callback.FileDownloadCallback;
import com.handu.poweroperational.utils.ApkUtils;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttputils.request.BaseRequest;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public class UpdateService extends Service {

    public static final String DOWNLOAD_URL = "url";
    private Context mContext;
    private final int NOTIFY_ID = 9898;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Notification notification;
    private String url = null;
    private String appName = PowerOperationalApplicationLike.getContext().getString(R.string.app_name) + ".apk";

    public UpdateService() {
    }

    /**
     * @param activity 上下文
     * @param url      地址
     */
    public static void goUpdateService(@NonNull Activity activity, @NonNull String url) {
        Intent intent = new Intent(activity, UpdateService.class);
        intent.putExtra(UpdateService.DOWNLOAD_URL, url);
        activity.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra(DOWNLOAD_URL);
        if (!TextUtils.isEmpty(url)) {
            if (notification == null) {
                setNotification();
                downloadApk();
            } else {
                Tools.toastInfo(getString(R.string.downloading));
            }
        } else {
            Tools.toastError("地址错误，下载失败...");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //创建通知
    private void setNotification() {
        mBuilder = new NotificationCompat.Builder(mContext);
        int icon = R.drawable.ic_vector_download;
        long when = System.currentTimeMillis();
        String content = getString(R.string.downloading);
        String title = mContext.getString(R.string.app_name);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content) //设置通知栏显示内容
                .setTicker(title) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(when)//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知自动取消
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,
                //.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                .setNumber(1); //设置通知集合的数量
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setSmallIcon(icon)//设置通知小ICON
                .setLights(0xff0000ff, 300, 0);
        notification = mBuilder.build();
        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    //改变默认通知栏的通知内容
    private void updateNotification(String content) {
        mBuilder.setContentText(content);
        notification = mBuilder.build();
        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    //下载完成后清除通知栏并停止服务
    private void clearNotification() {
        mNotificationManager.cancel(NOTIFY_ID);
        this.stopSelf();
    }

    //开始下载
    private void downloadApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(AppConstant.APK_DOWNLOAD_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        RequestServer.download(this, url, new FileDownloadCallback(AppConstant.APK_DOWNLOAD_PATH, appName) {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                updateNotification(getString(R.string.download_finish));
                clearNotification();
                if (file != null)
                    ApkUtils.install(getApplicationContext(), file);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                updateNotification(getString(R.string.downloading) + (int) (progress * 100) + "%");

            }

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                Tools.toastInfo(getString(R.string.start_download));
            }


            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                AppLogger.e(e.getMessage());
                Tools.toastError(e.getMessage());
            }
        });
    }

}
