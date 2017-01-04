package com.handu.poweroperational.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by 柳梦 on 2016/11/17.
 * bitmap工具类
 */

public class BitmapUtils {

    //现在主流手机比较多是1920*1080分辨率，所以高和宽我们设置为
    public static float hh = 1920f;//这里设置高度为1920f
    public static float ww = 1080f;//这里设置宽度为1080f

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * 判断照片角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 添加水印
     *
     * @param src
     * @param kcode
     * @return
     */
    public static Bitmap createWaterMarkBitmap(Bitmap src, String kcode) {
        if (src == null) {
            return src;
        }
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        String code = kcode;
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + calendar.get(Calendar.HOUR_OF_DAY) + "时" + calendar.get(Calendar.MINUTE) + "分" + calendar.get(Calendar.SECOND) + "秒";
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        mCanvas.drawBitmap(src, 0, 0, null);
        // 开始加入文字
        if (null != date) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(18);
            String familyName = "黑体";
            Typeface typeface = Typeface.create(familyName,
                    Typeface.BOLD_ITALIC);
            textPaint.setTypeface(typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
            mCanvas.drawText(date, w - (textPaint.measureText(date) / 2), h - 18, textPaint);

        }
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        if (null != code) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(18);
            String familyName = "黑体";
            Typeface typeface = Typeface.create(familyName,
                    Typeface.BOLD_ITALIC);
            textPaint.setTypeface(typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
            mCanvas.drawText(code, w - (textPaint.measureText(code) / 2), h - 48, textPaint);
        }
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();
        return newBitmap;
    }

    /**
     * 获取压缩后图片地址
     *
     * @param context
     * @param picturePath 地址
     * @param waterMark   水印
     * @return
     */
    public static String getCompressImagePath(Context context, String picturePath, String waterMark) {
        File outDir;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 这个路径，在手机内存下创建一个AppImage的文件夹，把图片存在其中。
            outDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AppImage");
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
        } else {
            outDir = context.getFilesDir();
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
        }
        String absolutePath;
        Bitmap bitmap = compressImageScale(picturePath);
        bitmap = createWaterMarkBitmap(bitmap, waterMark);
        int degree = readPictureDegree(picturePath);//获取图片角度
        bitmap = rotateBitmap(bitmap, degree);
        // 保存图片到pictures文件夹下,上传的时候还要上传file
        File file = new File(outDir, System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            absolutePath = file.getAbsolutePath();//绝对路径
        } catch (FileNotFoundException e) {
            absolutePath = picturePath;
            e.printStackTrace();
        }
        return absolutePath;
    }

    /**
     * 质量压缩方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        int size = baos.toByteArray().length / 1024;
        //获取bitmap大小 是允许最大大小的多少倍
        double maxSize = 400.00;
        while (size > maxSize && options > 0) {
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            double i = size / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
            // 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            size = baos.toByteArray().length / 1024;
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法（根据路径获取图片并压缩）
     *
     * @param srcPath
     * @return
     */
    public static Bitmap compressImageScale(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空
        newOpts.inSampleSize = calculateInSampleSize(newOpts, (int) hh, (int) ww);//设置缩放比例
        newOpts.inJustDecodeBounds = false;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 图片按比例大小压缩方法（根据Bitmap图片压缩）
     *
     * @param image
     * @return
     */
    public static Bitmap compressImageScale(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = calculateInSampleSize(newOpts, (int) hh, (int) ww);//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
}
