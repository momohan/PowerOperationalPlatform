package com.handu.poweroperational.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class Tools {

    /**
     * 匹配手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobilePhone(String mobiles) {

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
    public static String getPhoneName(Context context) {

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
    public static String getPhoneNumber(Context context) {

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
    public static String convertMillisToDateStr(long Time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date(Time);
        return sdf.format(currentTime);
    }

    /**
     * @param Time
     * @return 通过Date.getTime()方法，将其转化为毫秒数
     */
    public static long convertDateStrToMillis(String Time) {
        Date date = convertStringToDate(Time);
        return date.getTime();
    }

    /**
     * 把日期转为字符串
     */
    public static String convertDateToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    /**
     * 把日期转为字符串
     */
    public static String convertDateToString(Date date, String type) {
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
    public static Date convertStringToDate(String strDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertStringToDate(convertMillisToDateStr(System.currentTimeMillis()));
    }

    /**
     * 转化为指定的时间格式
     *
     * @param Time
     * @param format
     * @return
     */
    public static String formatDateStr(String Time, String format) {
        Date currentTime = convertStringToDate(Time);
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
    public static String compareTimeString(String startTime, String endTime) throws ParseException {
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
    public static long compareTimeLong(String startTime, String endTime) throws ParseException {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = dfs.parse(startTime);
        Date end = dfs.parse(endTime);
        return (end.getTime() - begin.getTime()) / 1000;
    }

    public static void toastNormal(String text) {
        toastNormal(text, Toast.LENGTH_SHORT);
    }

    public static void toastNormal(String text, int duration) {
        Toasty.normal(PowerOperationalApplicationLike.getContext(), text, duration).show();
    }

    public static void toastError(String text) {
        toastError(text, Toast.LENGTH_LONG);
    }

    public static void toastError(String text, int duration) {
        Toasty.error(PowerOperationalApplicationLike.getContext(), text, duration, true).show();
    }

    public static void toastSuccess(String text) {
        toastSuccess(text, Toast.LENGTH_SHORT);
    }

    public static void toastSuccess(String text, int duration) {
        Toasty.success(PowerOperationalApplicationLike.getContext(), text, duration, true).show();
    }

    public static void toastInfo(String text) {
        toastInfo(text, Toast.LENGTH_LONG);
    }

    public static void toastInfo(String text, int duration) {
        Toasty.info(PowerOperationalApplicationLike.getContext(), text, duration, true).show();
    }

    public static void toastWarning(String text) {
        toastWarning(text, Toast.LENGTH_LONG);
    }

    public static void toastWarning(String text, int duration) {
        Toasty.warning(PowerOperationalApplicationLike.getContext(), text, duration, true).show();
    }

    public static void toastCustom(String text, Drawable iconDrawable) {
        Toasty.normal(PowerOperationalApplicationLike.getContext(), text, iconDrawable).show();
    }

    /**
     * @return 获取屏幕分辨率(宽高等)
     */
    public static DisplayMetrics getDisplayMetrics() {
        WindowManager manager = (WindowManager) PowerOperationalApplicationLike.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * 判断GPS AGPS是否开启
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isLocationInfoOpen(Context context) {
        return (isGPSOpen(context) || isAGPSOpen(context)) && isNetworkAvailable();
    }

    /**
     * 判断GPS是否开启
     */
    public static boolean isGPSOpen(Context context) {
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断A GPS是否开启
     */
    public static boolean isAGPSOpen(Context context) {
        // 通过Wifi或移动网络(4G/3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
        wl.release();
    }

    public static float getMax(Float[] arr) {
        float max = Float.MIN_VALUE;
        for (Float anArr : arr)
            if (anArr > max)
                max = anArr;
        return max;
    }

    public static float getMin(Float[] arr) {
        float min = Float.MAX_VALUE;
        for (Float anArr : arr) {
            if (anArr < min)
                min = anArr;
        }
        return min;
    }

    /**
     * 检测网络是否连接，包括各种网络情况
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        Context context = PowerOperationalApplicationLike.getContext();
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // 去进行判断网络是否连接
        boolean b = info != null && info.isAvailable();
        if (!b) {
            Tools.toastError(PowerOperationalApplicationLike.getContext().getString(R.string.network_not_available));
        }
        return b;
    }

    /**
     * 判断wifi是否开启
     *
     * @return
     */
    public static boolean isWiFiNetworkAvailable() {
        Context context = PowerOperationalApplicationLike.getContext();
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // 去进行判断网络是否连接
        return info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    //版本名
    public static String getVersionName(Context context) {
        PackageInfo info = getPackageInfo(context);
        if (info != null)
            return info.versionName;
        return "";
    }

    //版本号
    public static int getVersionCode(Context context) {
        PackageInfo info = getPackageInfo(context);
        if (info != null)
            return info.versionCode;
        return -1;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
