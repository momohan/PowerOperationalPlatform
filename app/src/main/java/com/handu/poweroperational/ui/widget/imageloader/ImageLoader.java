package com.handu.poweroperational.ui.widget.imageloader;

import android.widget.ImageView;

import com.handu.poweroperational.R;

/**
 * Created by 柳梦 on 2017/1/9.
 */

public class ImageLoader {

    private int type;  //类型 (大图，中图，小图)
    /**
     * 需要解析的url可以为以下类型
     * String string：       string可以为一个文件路径、uri或者url
     * Uri uri：             uri类型
     * File file：           文件
     * Integer resourceId：  资源Id,R.drawable.xxx或者R.mipmap.xxx
     * byte[] model：        byte[]类型
     * T model：             自定义类型
     */
    private Object url;
    private int placeHolder; //当没有成功加载或加载出错的时候显示的图片
    private ImageView imgView; //ImageView的实例
    private int strategy;//加载策略，是否在wifi下才加载

    private ImageLoader(Builder builder) {
        this.type = builder.type;
        this.url = builder.url;
        this.placeHolder = builder.placeHolder;
        this.imgView = builder.imgView;
        this.strategy = builder.strategy;
    }

    public int getType() {
        return type;
    }

    public Object getUrl() {
        return url;
    }

    public int getPlaceHolder() {
        return placeHolder;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public int getStrategy() {
        return strategy;
    }

    public static class Builder {
        private int type;
        private Object url;
        private int placeHolder;
        private ImageView imgView;
        private int strategy;

        public Builder() {
            this.type = ImageLoaderUtil.PIC_SMALL;
            this.url = null;
            this.placeHolder = R.drawable.ic_vector_normal_image;
            this.imgView = null;
            this.strategy = ImageLoaderUtil.LOAD_STRATEGY_NORMAL;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder url(Object url) {
            this.url = url;
            return this;
        }

        public Builder placeHolder(int placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        public Builder imgView(ImageView imgView) {
            this.imgView = imgView;
            return this;
        }

        public Builder strategy(int strategy) {
            this.strategy = strategy;
            return this;
        }

        public ImageLoader build() {
            return new ImageLoader(this);
        }

    }
}
