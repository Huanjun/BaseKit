package com.mp.basekit.util;

import android.annotation.SuppressLint;

import com.mp.basekit.R;
import com.mp.basekit.core.MPApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 说明：日期格式化工具
 * 作者：liuwan1992
 * 添加时间：2018/12/17
 * 修改人：liuwan1992
 * 修改时间：2018/12/18
 */
public class DateFormatUtils {

    private static final String DATE_FORMAT_PATTERN_YMD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_PATTERN_YMD_HM = "yyyy-MM-dd HH:mm";
    private static final String DATE_FORMAT_PATTERN_HM_DMY = "HH:mm dd-MM-yyyy";


    /**
     * 时间戳转字符串
     *
     * @param timestamp     时间戳
     * @param isPreciseTime 是否包含时分
     * @return 格式化的日期字符串
     */
    public static String long2Str(long timestamp, boolean isPreciseTime) {
        return long2Str(timestamp, getFormatPattern(isPreciseTime));
    }

    public static String long2Str(long timestamp, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(timestamp));
    }

    public static String long2HMDMY(long timestamp) {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN_HM_DMY, Locale.getDefault()).format(new Date(timestamp));
    }

    /**
     * 字符串转时间戳
     *
     * @param dateStr       日期字符串
     * @param isPreciseTime 是否包含时分
     * @return 时间戳
     */
    public static long str2Long(String dateStr, boolean isPreciseTime) {
        return str2Long(dateStr, getFormatPattern(isPreciseTime));
    }

    public static long str2Long(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.getDefault()).parse(dateStr).getTime();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static String getFormatPattern(boolean showSpecificTime) {
        if (showSpecificTime) {
            return DATE_FORMAT_PATTERN_YMD_HM;
        } else {
            return DATE_FORMAT_PATTERN_YMD;
        }
    }

    public static String millisToStringShort(long millis) {
        return millisToStringShort(millis, false);
    }

    /**
     * 把毫秒数转换成时分秒
     *
     * @param millis
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String millisToStringShort(long millis, boolean isText) {
        StringBuilder strBuilder = new StringBuilder();
        long temp = millis;
        long hper = 60 * 60 * 1000;
        long mper = 60 * 1000;
        long sper = 1000;
        strBuilder.append(String.format("%02d", temp / hper));
        if (isText) {
            strBuilder.append(MPApplication.getAppContext().getString(R.string.hour_)).append(" ");
        } else {
            strBuilder.append(":");
        }
        temp = temp % hper;
        strBuilder.append(String.format("%02d", temp / mper));
        if (isText) {
            strBuilder.append(MPApplication.getAppContext().getString(R.string.minute)).append(" ");
        } else {
            strBuilder.append(":");
        }
        temp = temp % mper;
        strBuilder.append(String.format("%02d", temp / sper));
        if (isText) {
            strBuilder.append(MPApplication.getAppContext().getString(R.string.second)).append(" ");
        }
        return strBuilder.toString();
    }

    /**
     * 把毫秒数转换成时分秒
     *
     * @param millis
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String millisToMMSS(long millis) {
        StringBuilder strBuilder = new StringBuilder();
        long temp = millis;
        long mper = 60 * 1000;
        long sper = 1000;
        strBuilder.append(String.format("%02d", temp / mper)).append(":");
        temp = temp % mper;
        strBuilder.append(String.format("%02d", temp / sper));
        return strBuilder.toString();
    }

}