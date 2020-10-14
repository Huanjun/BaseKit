package com.mp.basekit.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具类
 * Created by hujia on 2017/2/28.
 */
public class EncryptUtil {

    private static final String IV_STRING = "rst@123456--java";
    private static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";

    public static byte[] aesEncrypt(byte[] data, String key) {
        return aesEncrypt(data, key, IV_STRING);
    }

    public static byte[] aesEncrypt(byte[] data, String key, String iv) {
        try {
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = iv.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] aesDecrypt(byte[] data, String key) {
        try {
            //key
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
            //iv
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            //加密器
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encode(String data, String key) {
        String realKey = getRealKey(key);
        return Base64.encodeToString(aesEncrypt(data.getBytes(), realKey, IV_STRING), Base64.DEFAULT);
    }

    public static String decode(String data, String key) {
        String realKey = getRealKey(key);
        byte[] dataByte = aesDecrypt(Base64.decode(data.getBytes(), Base64.DEFAULT), realKey);
        if (dataByte != null) {
            return new String(dataByte);
        }
        return "";
    }

    public static String encodeToHex(String data, String key) {
        String realKey = getRealKey(key);
        return HexUtils.toHex(aesEncrypt(data.getBytes(), realKey, realKey));
    }

    private static String getRealKey(String key) {
        int length = key.length();
        String realKey = "";
        if (length != 16 && length < 32) {
            realKey = String.format("%" + 32 + "s", key);
            realKey = realKey.replaceAll("\\s", "0");
        } else if (length > 32) {
            realKey = key.substring(length - 32, length);
        } else {
            realKey = key;
        }
        return realKey;
    }
}

