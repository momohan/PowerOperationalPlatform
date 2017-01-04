package com.handu.poweroperational.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    /**
     * 匹配手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {

        Pattern p = Pattern.compile("^[1][3-8]+\\d{9}");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

    /**
     * 区号-匹配固话-分机
     */

    public static boolean isTelephone(String phoneNum) {

        String regexp = "^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$";

        Pattern p = Pattern.compile(regexp);

        Matcher m = p.matcher(phoneNum);

        boolean b = m.matches();

        return b;
    }

    /**
     * 获取设备名称
     *
     * @param context
     * @return 设备名称
     */

    public static String getDeviceName(Context context) {

        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return manager.getDeviceId();

    }


    /**
     * 获取本机手机号码
     *
     * @param context
     * @return 手机号码
     */

    public static String getDevicePhoneNumber(Context context) {

        TelephonyManager manager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        return manager.getLine1Number();

    }


    /**
     * 毫秒数转化为时间,中文
     *
     * @param Time
     * @return
     */

    public static String ConvertMillisToDateStr(long Time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date(Time);
        String time = sdf.format(currentTime);
        return time;
    }

    /**
     * @param Time
     * @return 通过Date.getTime()方法，将其转化为毫秒数
     */
    public static long ConvertDateStrToMillis(String Time) {
        Date date = ConvertStringToDate(Time);
        return date.getTime();
    }

    /**
     * 把日期转为字符串
     */

    public static String ConvertDateToString(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return df.format(date);
    }

    /**
     * 把日期转为字符串
     */

    public static String ConvertDateToString(Date date, String type) {

        SimpleDateFormat df = new SimpleDateFormat(type);

        return df.format(date);
    }

    /**
     * 把字符串转为日期
     *
     * @param strDate
     * @return
     * @throws Exception
     */
    public static Date ConvertStringToDate(String strDate) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ConvertStringToDate(ConvertMillisToDateStr(System.currentTimeMillis()));
    }

    /**
     * 转化为指定的时间格式
     *
     * @param Time
     * @param format
     * @return
     */
    public static String FormatDateStr(String Time, String format) {
        Date currentTime = ConvertStringToDate(Time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time = sdf.format(currentTime);
        return time;
    }

    /**
     * 时间比较
     *
     * @param
     * @return
     */
    public static final String compareTimeString(String startTime, String endTime) throws ParseException {

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = dfs.parse(startTime);
        Date end = dfs.parse(endTime);
        long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒
        long day = between / (24 * 3600);
        long hour = between % (24 * 3600) / 3600;
        long minute = between % 3600 / 60;
        long second = between % 60;

        return day + "天" + hour + "小时" + minute + "分" + second + "秒";
    }

    /**
     * 时间比较
     *
     * @param
     * @return
     */
    public static final long compareTimeLong(String startTime, String endTime) throws ParseException {

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = dfs.parse(startTime);
        Date end = dfs.parse(endTime);
        long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒

        return between;
    }

    /**
     * 自定义Toast
     */

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    /**
     * 自定义Toast 显示自定义时间和内容 时间为毫秒
     *
     * @param text
     * @param duration
     */
    public static void showToast(String text, Integer duration) {
        showToast(text, duration, null);
    }

    /**
     * @param text
     * @param duration
     * @param position android.view.Gravity
     * @see Gravity
     */
    public static void showToast(String text, Integer duration, Integer position) {
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(PowerOperationalApplication.getContext(), text, Toast.LENGTH_SHORT);
        if (duration != null)
            mHandler.postDelayed(r, duration);
        else
            mHandler.postDelayed(r, 2000);
        if (position != null) {
            mToast.setGravity(position, 0, 0);
        } else {
            DisplayMetrics metrics = getDisplayMetrics();
            int height = metrics.heightPixels;
            //这里给了一个1/4屏幕高度的y轴偏移量
            mToast.setGravity(Gravity.BOTTOM, 0, height / 4);
        }
        mToast.show();
    }

    public static void showToast(String text) {
        showToast(text, null);
    }

    /**
     * 自定义Toast显示时间，内容来自资源String
     *
     * @param resId
     * @param duration
     */
    public static void showToast(int resId, Integer duration, Integer position) {
        showToast(PowerOperationalApplication.getContext().getResources().getString(resId), duration, position);
    }

    public static void showToast(int resId, int duration) {
        showToast(PowerOperationalApplication.getContext().getResources().getString(resId), duration);
    }

    public static void showToast(int resId) {
        showToast(PowerOperationalApplication.getContext().getResources().getString(resId));
    }

    /**
     * @return 获取屏幕分辨率(宽高等)
     */
    public static DisplayMetrics getDisplayMetrics() {
        WindowManager manager = (WindowManager) PowerOperationalApplication.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * @param locationManager
     * @return
     */
    public static boolean isGpsEnabled(LocationManager locationManager) {
        boolean isOpenGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isOpenNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isOpenGPS || isOpenNetwork) {
            return true;
        }
        return false;
    }

    /**
     * 判断GPS是否开启
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGpsOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gps) {
            return true;
        }
        return false;
    }

    /**
     * 判断AGPS是否开启
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isAGPSOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (network) {
            return true;
        }
        return false;
    }

    /**
     * 强制帮用户打开GPS(需要root权限)
     *
     * @param context
     */
    public static final void openGPS(Context context) {

        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取UTC时间
     * <p/>
     * UTC + 时区差 ＝ 本地时间(北京为东八区)
     *
     * @return
     */
    public static long getUTCTime() {
        //取得本地时间
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //取得时间偏移量
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        //取得夏令时差
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        //从本地时间里扣除这些差量，即可以取得UTC时间
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return cal.getTimeInMillis();
    }


    /**
     * 从输入流中读取数据
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }


    /**
     * 强制显示或者关闭系统键盘
     *
     * @param et
     * @param isOpen
     */
    public static void KeyBoard(final EditText et, final boolean isOpen) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager)
                        et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isOpen) {
                    m.showSoftInput(et, InputMethodManager.SHOW_FORCED);
                } else {
                    m.hideSoftInputFromWindow(et.getWindowToken(), 0);
                }
            }
        }, 100);
    }


    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                PowerOperationalApplication.getContext().getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //点亮屏幕
    public static void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
//        wl.release();
    }


    /**
     * API 21 安卓5.0以上
     *
     * @param context
     * @return
     */
    public static String getProcessNew(Context context) {
        String topPackageName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 20, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        }
        return topPackageName;
    }


    /**
     * API 21 安卓5.0以下
     *
     * @param context
     * @return
     */
    public static String getProcessOld(Context context) {
        String topPackageName = null;
        ActivityManager activity = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTask = activity.getRunningTasks(1);
        if (runningTask != null) {
            ActivityManager.RunningTaskInfo taskTop = runningTask.get(0);
            ComponentName componentTop = taskTop.topActivity;
            topPackageName = componentTop.getPackageName();
        }
        return topPackageName;
    }


    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public static void goAppWithPackageName(Context mContext, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = mContext.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Tools.showToast("包名不存在", 2000);
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = mContext.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            mContext.startActivity(intent);
        }
    }

    public static float getMax(Float[] arr) {
        float max = Float.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max)
                max = arr[i];
        }
        return max;
    }

    public static float getMin(Float[] arr) {
        float min = Float.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min)
                min = arr[i];
        }
        return min;
    }

    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        Context context = PowerOperationalApplication.getContext();
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // 去进行判断网络是否连接
        boolean b = info != null && info.isAvailable();
        if (!b) {
            Tools.showToast(PowerOperationalApplication.getContext().getString(R.string.network_not_available), 3000);
        }
        return b;
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }
}
