package com.handu.poweroperational.ui.widget.imageloader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;
import com.handu.poweroperational.utils.AppUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 柳梦 on 2017/1/9.
 */

public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy {

    @Override
    public void loadImage(Context ctx, ImageLoader loader) {

        //首先判断图片是不是String类型的地址
        if (loader.getUrl() instanceof String) {
            //如果不是url地址直接加载
            if (!isUrl((String) loader.getUrl())) {
                loadNormal(ctx, loader);
            } else {
                //如果是url地址先判断网络
                Context context = PowerOperationalApplicationLike.getContext();
                // 得到网络连接信息
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = manager.getActiveNetworkInfo();
                // 去进行判断网络是否连接
                boolean b = info != null && info.isAvailable();
                if (!b) {
                    //如果没有网络，加载缓存
                    loadCache(ctx, loader);
                } else {
                    //有网络判断加载策略
                    int strategy = loader.getStrategy();
                    if (strategy == ImageLoaderUtil.LOAD_STRATEGY_ONLY_WIFI) {
                        int netType = AppUtils.getNetWorkType(PowerOperationalApplicationLike.getContext());
                        //如果是在wifi下才加载图片，并且当前网络是wifi,直接加载
                        if (netType == AppUtils.NETWORKTYPE_WIFI) {
                            loadNormal(ctx, loader);
                        } else {
                            //如果是在wifi下才加载图片，但是当前网络不是wifi，加载缓存
                            loadCache(ctx, loader);
                        }
                    } else if (strategy == ImageLoaderUtil.LOAD_STRATEGY_NORMAL) {
                        //如果是任何情况下都可以加载图片，直接加载
                        loadNormal(ctx, loader);
                    }
                }
            }
        } else {
            //如果是其他类型的地址
            loadNormal(ctx, loader);
        }
    }

    /**
     * load image with Glide
     */
    private void loadNormal(Context ctx, ImageLoader loader) {
        Glide.with(ctx)
                .load(loader.getUrl())
                .dontAnimate()
                .placeholder(loader.getPlaceHolder())
                .error(loader.getPlaceHolder())
                .centerCrop()
                .into(loader.getImgView());
    }

    /**
     * load cache image with Glide
     */
    private void loadCache(Context ctx, ImageLoader loader) {
        Glide.with(ctx).using(new StreamModelLoader<String>() {
            @Override
            public DataFetcher<InputStream> getResourceFetcher(final String model, int i, int i1) {
                return new DataFetcher<InputStream>() {
                    @Override
                    public InputStream loadData(Priority priority) throws Exception {
                        throw new IOException();
                    }

                    @Override
                    public void cleanup() {

                    }

                    @Override
                    public String getId() {
                        return model;
                    }

                    @Override
                    public void cancel() {

                    }
                };
            }
        })
                .load((String) loader.getUrl())
                .dontAnimate()
                .placeholder(loader.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(loader.getImgView());
    }

    //判断是不是地址
    private boolean isUrl(String scanStr) {
        boolean b = false;
        Pattern pattern = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");
        Matcher matcher = pattern.matcher(scanStr);
        if (matcher.find()) {
            b = true;
        }
        return b;
    }
}
