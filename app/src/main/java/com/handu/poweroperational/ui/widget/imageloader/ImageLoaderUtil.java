package com.handu.poweroperational.ui.widget.imageloader;

import android.content.Context;

/**
 * Created by 柳梦 on 2017/1/9.
 */

public class ImageLoaderUtil {

    public static final int PIC_LARGE = 0;
    public static final int PIC_MEDIUM = 1;
    public static final int PIC_SMALL = 2;
    //默认加载模式，任何网络情况下都可以加载
    public static final int LOAD_STRATEGY_NORMAL = 0;
    //只有wifi情况下才可以加载
    public static final int LOAD_STRATEGY_ONLY_WIFI = 1;

    private static ImageLoaderUtil mInstance;
    private BaseImageLoaderStrategy mStrategy;

    private ImageLoaderUtil() {
        mStrategy = new GlideImageLoaderStrategy();
    }

    //single instance
    public static ImageLoaderUtil getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoaderUtil.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderUtil();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    public void loadImage(Context context, ImageLoader loader) {
        mStrategy.loadImage(context, loader);
    }

    public void setLoadImgStrategy(BaseImageLoaderStrategy strategy) {
        mStrategy = strategy;
    }
}
