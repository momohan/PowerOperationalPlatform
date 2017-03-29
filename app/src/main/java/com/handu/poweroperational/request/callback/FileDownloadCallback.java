package com.handu.poweroperational.request.callback;

import android.os.Environment;
import android.text.TextUtils;

import com.handu.poweroperational.utils.AppLogger;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.utils.HttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * 作者：柳梦 2016/9/20 08:27
 * 邮箱：mobyq@qq.com
 * 说明:
 */
public abstract class FileDownloadCallback extends AbsCallback<File> {

    private static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //默认下载目标文件夹

    private String destFileDir;     //目标文件存储的文件夹路径
    private String destFileName;    //目标文件存储的文件名

    protected FileDownloadCallback() {
        this(null);
    }

    protected FileDownloadCallback(String destFileName) {
        this(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER, destFileName);
    }

    protected FileDownloadCallback(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public File parseNetworkResponse(Response response) throws Exception {
        return saveFile(response);
    }

    private File saveFile(Response response) throws IOException {
        if (TextUtils.isEmpty(destFileDir))
            destFileDir = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        if (TextUtils.isEmpty(destFileName))
            destFileName = HttpUtils.getNetFileName(response, response.request().url().toString());

        File dir = new File(destFileDir);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, destFileName);
        if (file.exists()) file.delete();

        long lastRefreshUiTime = 0;  //最后一次刷新的时间
        long lastWriteBytes = 0;     //最后一次写入字节数据

        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            int len;
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;

                long curTime = System.currentTimeMillis();
                //每200毫秒刷新一次数据
                if (curTime - lastRefreshUiTime >= 200 || finalSum == total) {
                    //计算下载速度
                    long diffTime = (curTime - lastRefreshUiTime) / 1000;
                    if (diffTime == 0) diffTime += 1;
                    long diffBytes = finalSum - lastWriteBytes;
                    final long networkSpeed = diffBytes / diffTime;
                    OkHttpUtils.getInstance().getDelivery().post(() -> {
                        downloadProgress(finalSum, total, finalSum * 1.0f / total, networkSpeed);   //进度回调的方法
                    });

                    lastRefreshUiTime = System.currentTimeMillis();
                    lastWriteBytes = finalSum;
                }
            }
            fos.flush();
            return file;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                AppLogger.e(e.getMessage());
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                AppLogger.e(e.getMessage());
            }
        }
    }
}
