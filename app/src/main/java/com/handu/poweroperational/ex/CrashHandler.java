package com.handu.poweroperational.ex;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;

import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;
import com.handu.poweroperational.utils.thinker.PopTinkerReport;
import com.handu.poweroperational.utils.AppConstant;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.thinker.TinkerManager;
import com.handu.poweroperational.utils.thinker.Utils;
import com.tencent.tinker.lib.tinker.TinkerApplicationHelper;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @创建人 柳梦
 * @时间 2016/09/14 17:19
 * @说明 异常捕获
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "柳梦";
    private static CrashHandler crashHandler;
    private UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> info = new HashMap<>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static final long QUICK_CRASH_ELAPSE = 10 * 1000;
    public static final int MAX_CRASH_COUNT = 3;
    private static final String DALVIK_XPOSED_CRASH = "Class ref in pre-verified class resolved to unexpected implementation";

    private CrashHandler() {
    }

    /**
     * 获取单例
     *
     * @return crashHandler
     */
    public static CrashHandler getInstance() {
        if (crashHandler == null) {
            synchronized (CrashHandler.class) {
                if (crashHandler == null) {
                    crashHandler = new CrashHandler();
                }
            }
        }
        return crashHandler;
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     *
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        AppLogger.e("error : ", ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            tinkerFastCrashProtect();
            tinkerPreVerifiedCrashHandler(ex);
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                AppLogger.e("error : ", e);
            }
            PowerOperationalApplicationLike.getInstance().exit();
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(PowerOperationalApplicationLike.getContext());
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * @param ctx
     */
    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            AppLogger.e("an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.toString());
                AppLogger.e(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                AppLogger.e("an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误日志到sd卡
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(AppConstant.LOG_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(AppConstant.LOG_PATH + File.separator + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }

            return fileName;
        } catch (Exception e) {
            AppLogger.e("读写文件失败，可能是未授予内存卡读写权限", e);
        }

        return null;
    }

    /**
     * Such as Xposed, if it try to load some class before we load from patch files.
     * With dalvik, it will crash with "Class ref in pre-verified class resolved to unexpected implementation".
     * With art, it may crash at some times. But we can't know the actual crash type.
     * If it use Xposed, we can just clean patch or mention user to uninstall it.
     */
    private void tinkerPreVerifiedCrashHandler(Throwable ex) {
        Throwable throwable = ex;
        boolean isXposed = false;
        while (throwable != null) {
            if (!isXposed) {
                isXposed = Utils.isXposedExists(throwable);
            }
            if (isXposed) {
                //method 1
                ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();
                if (applicationLike == null || applicationLike.getApplication() == null) {
                    return;
                }

                if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
                    return;
                }
                boolean isCausedByXposed = false;
                //for art, we can't know the actually crash type
                //just ignore art
                if (throwable instanceof IllegalAccessError && throwable.getMessage().contains(DALVIK_XPOSED_CRASH)) {
                    //for dalvik, we know the actual crash type
                    isCausedByXposed = true;
                }

                if (isCausedByXposed) {
                    PopTinkerReport.onXposedCrash();
                    TinkerLog.e(TAG, "have xposed: just clean tinker");
                    //kill all other process to ensure that all process's code is the same.
                    ShareTinkerInternals.killAllOtherProcess(applicationLike.getApplication());

                    TinkerApplicationHelper.cleanPatch(applicationLike);
                    ShareTinkerInternals.setTinkerDisableWithSharedPreferences(applicationLike.getApplication());
                    return;
                }
            }
            throwable = throwable.getCause();
        }
    }

    /**
     * if tinker is load, and it crash more than MAX_CRASH_COUNT, then we just clean patch.
     */
    private boolean tinkerFastCrashProtect() {
        ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();

        if (applicationLike == null || applicationLike.getApplication() == null) {
            return false;
        }
        if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
            return false;
        }

        final long elapsedTime = SystemClock.elapsedRealtime() - applicationLike.getApplicationStartElapsedTime();
        //this process may not install tinker, so we use TinkerApplicationHelper api
        if (elapsedTime < QUICK_CRASH_ELAPSE) {
            String currentVersion = TinkerApplicationHelper.getCurrentVersion(applicationLike);
            if (ShareTinkerInternals.isNullOrNil(currentVersion)) {
                return false;
            }

            SharedPreferences sp = applicationLike.getApplication().getSharedPreferences(ShareConstants.TINKER_SHARE_PREFERENCE_CONFIG, Context.MODE_MULTI_PROCESS);
            int fastCrashCount = sp.getInt(currentVersion, 0) + 1;
            if (fastCrashCount >= MAX_CRASH_COUNT) {
                PopTinkerReport.onFastCrashProtect();
                TinkerApplicationHelper.cleanPatch(applicationLike);
                TinkerLog.e(TAG, "tinker has fast crash more than %d, we just clean patch!", fastCrashCount);
                return true;
            } else {
                sp.edit().putInt(currentVersion, fastCrashCount).apply();
                TinkerLog.e(TAG, "tinker has fast crash %d times", fastCrashCount);
            }
        }

        return false;
    }
}