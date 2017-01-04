package com.handu.poweroperational.request;

import android.app.Activity;

import com.handu.poweroperational.utils.ServiceUrl;
import com.handu.poweroperational.utils.Tools;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：柳梦 2016/9/19 16:37
 * 邮箱：mobyq@qq.com
 * 说明:
 */
public class OkHttpRequest {

    public static String Url = ServiceUrl.BaseUrl;

    private static String getAbsoluteUrl(String method) {
        return Url + method;
    }

    private static boolean isUrl(String scanStr) {
        boolean b = false;
        Pattern pattern = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");
        Matcher matcher = pattern.matcher(scanStr);
        if (matcher.find()) {
            b = true;
        }
        return b;
    }

    public static void get(Activity activity, String method, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.get(url).tag(activity).execute(callback);
    }

    public static void get(Activity activity, String method, Map<String, String> map, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.get(url).tag(activity).params(map).execute(callback);
    }

    public static void get(Activity activity, String method, String param, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.get(url).tag(activity).params("", param).execute(callback);
    }

    public static void post(Activity activity, String method, String param, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.post(url).tag(activity).params("", param).execute(callback);
    }

    public static void post(Activity activity, String method, Map<String, String> map, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.post(url).tag(activity).params(map).execute(callback);
    }

    public static void post(Activity activity, String method, JSONObject object, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.post(url).tag(activity).upJson(object.toString()).execute(callback);
    }

    /**
     * @创建人 柳梦
     * @时间 2016/9/19 17:22
     * @说明 多文件一个key
     */
    public static void upload(Activity activity, String method, Map<String, String> map, String key, List<File> files, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.post(url)
                .tag(activity)
                .params(map)
                .addFileParams(key, files)// 这种方式为同一个key，上传多个文件
                .execute(callback);
    }

    /**
     * @创建人 柳梦
     * @时间 2016/9/19 17:22
     * @说明 多文件多个key
     */
    public static void upload(Activity activity, String method, Map<String, String> map, String[] keys, List<File> files, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        if (keys.length != files.size()) {
            Tools.showToast("参数个数与文件个数不一致...");
            return;
        }
        HttpParams params = new HttpParams();
        Iterator var5 = map.keySet().iterator();
        while (var5.hasNext()) {
            String key = (String) var5.next();
            params.put(key, map.get(key));
        }
        for (String key : keys) {
            params.put(key, files.get(0));
        }
        OkHttpUtils.post(url)
                .tag(activity)
                .params(params)// 这种方式为同一个key，上传多个文件
                .execute(callback);
    }

    public static void download(Activity activity, String method, String key, String param, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.get(url)
                .tag(activity)
                .params(key, param)
                .execute(callback);
    }

    public static void download(Activity activity, String method, Map<String, String> map, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.get(url)
                .tag(activity)
                .params(map)
                .execute(callback);
    }

    public static void download(Activity activity, String method, AbsCallback callback) {
        if (!Tools.isNetworkAvailable()) return;
        String url = isUrl(method) ? method : getAbsoluteUrl(method);
        OkHttpUtils.get(url)
                .tag(activity)
                .execute(callback);
    }
}
