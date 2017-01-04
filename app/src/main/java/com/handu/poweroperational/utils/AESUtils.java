package com.handu.poweroperational.utils;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 作者：柳梦 2016/9/20 14:38
 * 邮箱：mobyq@qq.com
 * 说明: md5加密
 */
public class AESUtils {

    public static final String key = MD5("123456");
    public static final String iv = "0000000000000000";
    public static IvParameterSpec ivSpec = new IvParameterSpec(getIvBytes());
    public static SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");

    private static byte[] getIvBytes() {
        byte[] bytes = iv.getBytes();
        for (int i = 0; i < 16; i++) {
            bytes[i] = 0;
        }
        return bytes;
    }

    public static String MD5(String str) {
        String hexDigit[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF8"));
            StringBuffer hex = new StringBuffer(hash.length * 2);
            for (byte b : hash) {
                hex.append(hexDigit[b >> 4 & 0xf]);
                hex.append(hexDigit[b & 0xf]);
            }
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @创建人 柳梦
     * @时间 2016/9/20 14:45
     * @说明 数据加密
     */
    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryptedData = cipher.doFinal(plainText.getBytes());
            String encryptedText = new String(Base64.encode(encryptedData, Base64.DEFAULT));
            return encryptedText;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * @创建人 柳梦
     * @时间 2016/9/20 14:45
     * @说明 数据解密
     */
    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte decryptedData[] = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            String plainText = new String(decryptedData);
            return plainText;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
