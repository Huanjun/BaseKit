package com.mp.basekit.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class BigNumUtils {

    /**
     * 获取小单位String
     *
     * @param bigDecimal
     * @param decimal
     * @return
     */
    public static String getUseUnit(String bigDecimal, int decimal) {
        if (TextUtils.isEmpty(bigDecimal)) {
            bigDecimal = "0";
        }
        return new BigDecimal(bigDecimal).multiply(new BigDecimal(10).pow(decimal)).setScale(0, BigDecimal.ROUND_DOWN).toPlainString();
    }

    /**
     * String 转 long
     *
     * @param bigDecimal
     * @param decimal    进制
     * @return
     */
    public static long getUseLong(String bigDecimal, int decimal) {
        if (TextUtils.isEmpty(bigDecimal)) {
            bigDecimal = "0";
        }
        return new BigDecimal(bigDecimal).multiply(new BigDecimal(10).pow(decimal))
                .setScale(0, BigDecimal.ROUND_DOWN).longValue();
    }

    /**
     * string 转 BigDecimal
     *
     * @param num
     * @param decimal
     * @return
     */
    public static BigDecimal toBigUnit(String num, int decimal) {
        if (TextUtils.isEmpty(num)) {
            return new BigDecimal("0");
        }
        BigDecimal bigDecimal = new BigDecimal(num);
        return bigDecimal.multiply(new BigDecimal(10).pow(decimal))
                .setScale(0, BigDecimal.ROUND_DOWN);
    }

    /**
     * 进制转换
     *
     * @param bigDecimal
     * @param scale
     * @return
     */
    public static String getDisplay(String bigDecimal, int scale) {
        if (TextUtils.isEmpty(bigDecimal)) {
            bigDecimal = "0";
        }
        return getDisplay(new BigDecimal(bigDecimal.replace(",", "")), scale, false);
    }

    /**
     * 进制转换
     *
     * @param bigDecimal
     * @param scale
     * @return
     */
    public static String getDisplay(BigDecimal bigDecimal, int scale) {
        return getDisplay(bigDecimal, scale, false);
    }

    /**
     * 进制转换
     *
     * @param bigDecimal
     * @param scale
     * @return
     */
    public static String getDisplay(String bigDecimal, int scale, boolean isFormat) {
        if (TextUtils.isEmpty(bigDecimal)) {
            bigDecimal = "0";
        }
        return getDisplay(new BigDecimal(bigDecimal), scale, isFormat);
    }

    /**
     * 进制转换
     *
     * @param bigDecimal
     * @param scale
     * @return
     */
    public static String getDisplay(BigDecimal bigDecimal, int scale, boolean isFormat) {
        if (bigDecimal == null) {
            bigDecimal = new BigDecimal("0");
        }
        BigDecimal result = bigDecimal.setScale(scale, BigDecimal.ROUND_DOWN);
        if (isFormat) {
            return num2thousand(result.toPlainString());
        }
        return result.toPlainString();
    }

    /**
     * 进制转换
     *
     * @param bigDecimal
     * @param decimal
     * @param scale
     * @return
     */
    public static String getDisplay(String bigDecimal, int decimal, int scale) {
        return getDisplay(new BigDecimal(bigDecimal), decimal, scale, false);
    }

    /**
     * 进制转换
     *
     * @param bigDecimal
     * @param decimal
     * @param scale
     * @return
     */
    public static String getDisplay(BigDecimal bigDecimal, int decimal, int scale) {
        return getDisplay(bigDecimal, decimal, scale, false);
    }

    /**
     * 进制转换
     *
     * @param bigDecimal
     * @param decimal
     * @param scale
     * @return
     */
    public static String getDisplay(BigDecimal bigDecimal, int decimal, int scale, boolean isFormat) {
        if (bigDecimal == null) {
            bigDecimal = new BigDecimal("0");
        }
        BigDecimal result = bigDecimal.divide(new BigDecimal(10).pow(decimal), decimal, BigDecimal.ROUND_HALF_DOWN)
                .setScale(scale, BigDecimal.ROUND_DOWN);
        if (isFormat) {
            return num2thousand(result.toPlainString());
        }
        return result.toPlainString();
    }

    /**
     * 进制转换，去掉末尾的0
     *
     * @param num
     * @param scale
     * @return
     */
    public static String getNoZeros(String num, int scale, boolean isFormat) {
        if (TextUtils.isEmpty(num)) {
            return "0";
        }
        num = num.replace(",", "");
        if (compareTo(num) == 0) {
            return "0";
        }
        BigDecimal result = new BigDecimal(num).setScale(scale, BigDecimal.ROUND_DOWN);
        if (isFormat) {
            return num2thousand(result.stripTrailingZeros().toPlainString());
        }
        return result.stripTrailingZeros().toPlainString();
    }

    /**
     * 进制转换，去掉末尾的0
     *
     * @param num
     * @param scale
     * @return
     */
    public static String getNoZeros(String num, int scale) {

        return getNoZeros(num, scale, false);
    }

    /**
     * 千位单位格式化
     *
     * @param num
     * @param scale
     * @return
     */
    public static String getKFormat(String num, int scale) {
        if (TextUtils.isEmpty(num)) {
            num = "0";
        }
        num = num.replace(",", "");
        String result;
        if (compare(num, "1000000") >= 0 || compare(num, "-1000000") <= 0) {
            result = new BigDecimal(num).divide(new BigDecimal("1000000"), 2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "m";
        } else if (compare(num, "10000") >= 0 || compare(num, "-10000") <= 0) {
            result = new BigDecimal(num).divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + "k";
        } else {
            result = getNoZeros(num, scale);
        }
        return result;
    }

    /**
     * 字符串 千位符
     *
     * @param num
     * @return
     */
    private static String num2thousand(String num) {
        if (TextUtils.isEmpty(num)) {
            return "0";
        }

        int index = num.indexOf(".");
        String decimal = "";
        String temp = num;
        if (index > 0) {
            decimal = num.substring(index);
            temp = num.substring(0, index);
        }
        String numStr = "0";
        NumberFormat nf = NumberFormat.getInstance();
        try {
            DecimalFormat df = new DecimalFormat("#,###");
            df.setRoundingMode(RoundingMode.FLOOR);
            numStr = df.format(nf.parse(temp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numStr + decimal;
    }

    /**
     * 数字字符串大小比较
     *
     * @param data
     * @return
     */
    public static int compareTo(String data) {
        if (data == null) {
            data = "0";
        }
        try {
            return new BigDecimal(data).compareTo(new BigDecimal("0"));
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    /**
     * 比较两个数的大小
     *
     * @param data1
     * @param data2
     * @return data1 > data2 -> 1;data1 < data2 -> -1;data1 = data2 -> 0;
     */
    public static int compare(String data1, String data2) {
        if (data1 == null) {
            data1 = "0";
        }
        if (data2 == null) {
            data2 = "0";
        }
        try {
            return new BigDecimal(data1).compareTo(new BigDecimal(data2));
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
