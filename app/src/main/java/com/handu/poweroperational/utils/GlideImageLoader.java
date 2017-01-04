package com.handu.poweroperational.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handu.poweroperational.R;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.ninegrid.NineGridView;

import java.io.File;

/**
 * ================================================
 * 作    者：柳梦
 * 版    本：1.0
 * 创建日期：16/9/5
 * 描    述：Glide图片加载
 * 修订历史：
 * ================================================
 */
public class GlideImageLoader implements ImageLoader, NineGridView.ImageLoader {

    @Override
    public void onDisplayImage(Context context, ImageView imageView, String url) {
        Glide.with(context).load(url)//
                .dontAnimate()
                .placeholder(R.drawable.ic_default_image)//
                .error(R.drawable.ic_default_image)//
                .diskCacheStrategy(DiskCacheStrategy.NONE)//
                .into(imageView);
    }

    @Override
    public Bitmap getCacheImage(String url) {
        return null;
    }

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity).load(new File(path))//
                .dontAnimate()
                .placeholder(R.drawable.ic_default_image)//
                .error(R.drawable.ic_default_image)//
                .diskCacheStrategy(DiskCacheStrategy.NONE)//
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
