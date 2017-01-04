package com.handu.poweroperational.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class FileUtils {

    private static String SDPATH;

    public FileUtils() {
        // 得到外部设备当前的SD卡目录
        SDPATH = Environment.getExternalStorageDirectory() + "/";
    }

    public static String getSDPATH() {
        return SDPATH;
    }

    public static boolean isFileExist(String filename) {
        File file = new File(SDPATH + filename);
        return file.exists();
    }

    /*
     * 在SD上创建文件;
     */
    public static File createSDFile(String filename) throws IOException {

        File file = new File(SDPATH + filename);
        file.createNewFile();
        return file;

    }

    /*
     * 在SD上创建目录
     */
    public static File createSDDir(String dirname) {
        File dir = new File(SDPATH + dirname);
        dir.mkdir();
        return dir;
    }

    /*
     * 将一个InputStream数据写入到SD卡中
     */
    public static File write2SDFromInput(String path, String filename, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            file = new File(path + filename);
            file.createNewFile();
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1204];
            int length = 0;
            while (true) {
                length = input.read(buffer);
                if (length < 0) {
                    break;
                }
                output.write(buffer, 0, length);
            }
            output.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }


    /**
     * 拷贝文件夹
     *
     * @param sourcePath
     * @param desPath
     */
    public static boolean copyFiles(String sourcePath, String desPath) {
        File sourceFiles = new File(sourcePath);
        if (!sourceFiles.exists()) {
            return false;
        }
        File[] files = sourceFiles.listFiles();

        if (null == files) {
            return false;
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            desPath = Environment.getExternalStorageDirectory() + File.separator + desPath;
            File desDir = new File(desPath);
            if (desDir == null || !desDir.exists()) {
                desDir.mkdir();
            }

            for (File f : files) {
                copyFile(f, desDir.getPath() + f.getName());
            }
        } else {
            return false;
        }


        return true;
    }

    public static void copyFile(InputStream in, String desPath, String desFile) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            desPath = Environment.getExternalStorageDirectory() + File.separator + desPath;
            File desDir = new File(desPath);
            if (desDir == null || !desDir.exists()) {
                desDir.mkdir();
            }

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(desPath + File.separator + desFile);

                byte[] buffer = new byte[1024];
                int length = -1;
                while (-1 != (length = in.read(buffer))) {
                    out.write(buffer, 0, length);
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (null != out) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    public static void copyFile(InputStream in, String desFile) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {


            FileOutputStream out = null;
            try {
                out = new FileOutputStream(desFile);

                byte[] buffer = new byte[1024];
                int length = -1;
                while (-1 != (length = in.read(buffer))) {
                    out.write(buffer, 0, length);
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (null != out) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * 拷贝文件
     */
    public static void copyFile(File sourceFile, String desFile) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(desFile);

            byte[] buffer = new byte[1024];
            int length = -1;
            while (-1 != (length = in.read(buffer))) {
                out.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }




    /*
     * RW File
     */

    /**
     * Use getFilesDir()
     *
     * @param context
     * @param fileName
     * @param byteArr
     * @throws IOException
     */
    public static void writeFileInInternalStorage(Context context,
                                                  String fileName, byte[] byteArr) throws IOException {
        writeFile(context, context.getFilesDir(), fileName, byteArr);
    }

    /**
     * Use getFilesDir()
     *
     * @param context
     * @param fileName
     * @param byteArr
     * @throws IOException
     */
    public static void writeFileInCacheStorage(Context context,
                                               String fileName, byte[] byteArr) throws IOException {
        writeFile(context, context.getCacheDir(), fileName, byteArr);
    }

    private static void writeFile(Context context, File fileDir,
                                  String fileName, byte[] byteArr) throws IOException {
        File file = new File(fileDir, fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(byteArr);
        outputStream.close();
    }

    public static byte[] readFileFromInternalStorage(Context context,
                                                     String fileName) throws IOException {
        return readFile(context, context.getFilesDir(), fileName);
    }

    public static byte[] readFileFromCacheStorage(Context context,
                                                  String fileName) throws IOException {
        return readFile(context, context.getCacheDir(), fileName);
    }

    private static byte[] readFile(Context context, File dirName,
                                   String fileName) throws IOException {
        File file = new File(dirName, fileName);
        int size = (int) file.length();
        byte byteArr[] = new byte[size];

        FileInputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                inputStream);
        bufferedInputStream.read(byteArr, 0, byteArr.length);
        bufferedInputStream.close();

        return byteArr;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            File externalDirectory = Environment.getExternalStorageDirectory();
            file = new File(externalDirectory + "/" + filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    public static void writeFileInExternalStorage(String fileName, byte byteArr[])
            throws Exception, IOException {
        if (isExternalStorageWritable()) {
            File externalDirectory = Environment.getExternalStorageDirectory();
            File file = new File(externalDirectory + "/" + fileName);
            if (!file.exists()) {
                boolean b = file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(byteArr);
            fileOutputStream.close();
        } else {
            throw new Exception(
                    "External storage is not writable!");
        }
    }

    public static byte[] readFileFromExternalStorage(String fileName)
            throws IOException, Exception {
        byte[] byteArr;
        if (isExternalStorageReadable()) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/" + fileName);
            int size = (int) file.length();
            byteArr = new byte[size];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    fileInputStream);
            bufferedInputStream.read(byteArr, 0, byteArr.length);
            bufferedInputStream.close();
        } else {
            throw new Exception();
        }

        return byteArr;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)
                || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @ 作者:柳梦
     * @ 创建时间:2016/6/13 14:52
     * @ 说明:打开文件
     * @ 返回:
     * @ 参数:
     */
    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Tools.showToast("没有可打开该文件的App，请下载相关App后再试...");
        }
    }

    /**
     * @ 作者:柳梦
     * @ 创建时间:2016/6/13 14:52
     * @ 说明:根据文件后缀名获得对应的MIME类型。
     * @ 返回:
     * @ 参数:
     */
    private static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /*获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * @ 作者:柳梦
     * @ 创建时间:2016/6/13 13:51
     * @ 说明:{后缀名，MIME类型}
     * @ 返回:
     * @ 参数:
     */
    private static final String[][] MIME_MapTable = {
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static long getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }

        return blockSize;
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 根据文件路径获取文件名称
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {

        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return path.substring(start + 1, end);
        } else {
            return null;
        }
    }
}
