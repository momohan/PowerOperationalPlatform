package com.handu.poweroperational.ui.widget.imageloader;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.handu.poweroperational.main.application.PowerOperationalApplication;
import com.handu.poweroperational.utils.AppUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 柳梦 on 2017/1/9.
 */

public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy {

    @Override
    public void loadImage(Context ctx, ImageLoader loader) {

        int strategy = loader.getStrategy();
        if (strategy == ImageLoaderUtil.LOAD_STRATEGY_ONLY_WIFI) {
            int netType = AppUtils.getNetWorkType(PowerOperationalApplication.getContext());
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

    /**
     * load image with Glide
     */
    private void loadNormal(Context ctx, ImageLoader loader) {
        Glide.with(ctx).load(loader.getUrl())
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
                .load(loader.getUrl())
                .dontAnimate()
                .placeholder(loader.getPlaceHolder())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(loader.getImgView());
    }
}
