package com.handu.poweroperational.request.callback;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.handu.poweroperational.R;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONObject;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * @创建人 柳梦
 * @时间 2016/9/17 13:04
 * @说明 ================================================
 * 作    者：柳梦
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成需要的Bean,可以是 BaseBean，String，List，Map...
 * 修订历史：
 * ================================================
 */

public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Class<T> clazz;
    private Type type;

    /**
     * 传class,直接返回解析生成的对象
     */
    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 对于需要返回集合类型的,可以传type
     * type = new TypeToken<List<你的数据类型>>(){}.getState()
     */
    public JsonCallback(Type type) {
        this.type = type;
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //主要用于在所有请求之前添加公共的请求头或请求参数，例如登录授权的 token,
        // 使用的设备信息等,可以随意添加,也可以什么都不传
        //request.headers("header", "")/**/.params("params", "")/**/.params("token", "");
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T parseNetworkResponse(Response response) throws Exception {
        int code = response.code();
        switch (code) {
            case 200:
                /**
                 * code = 200 代表成功，success表示有数据 !success表示无数据
                 * 这里默认实现了Gson解析,可以自己替换成fastjson等
                 * clazz类型就是解析javaBean
                 * type类型就是解析List<javaBean>
                 */
                String responseData = response.body().string();
                if (TextUtils.isEmpty(responseData)) {
                    throw new IllegalStateException(PowerOperationalApplicationLike.getContext().getResources().getString(R.string.data_null));
                }
                /**
                 * 一般来说，服务器返回的响应码都包含 success，msg，data 几部分，在此根据自己的业务需要完成相应的逻辑判断
                 * 具体业务具体实现
                 */
                JSONObject jsonObject = new JSONObject(responseData);
                boolean success = jsonObject.optBoolean("success", false);
                String msg = jsonObject.optString("msg", "");
                String data = jsonObject.optString("data", "");
                if (success) {
                    if (clazz == String.class) {
                        return (T) data;
                    }
                    if (clazz != null) {
                        return new Gson().fromJson(data, clazz);
                    }
                    if (type != null) {
                        return new Gson().fromJson(data, type);
                    }
                } else {
                    throw new IllegalStateException(msg);
                }
                break;
            default:
                throw new IllegalStateException(code + "，" + response.message());
        }
        throw new IllegalStateException(
                PowerOperationalApplicationLike.getContext().getResources().getString(R.string.data_parse_error));
    }
}