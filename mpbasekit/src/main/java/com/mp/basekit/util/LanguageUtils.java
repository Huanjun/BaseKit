package com.mp.basekit.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.mp.basekit.core.MPApplication;

import java.util.Locale;

public class LanguageUtils {

    public static final int DEFAULT = 0;
    public static final String CHINESE = "ZH";
    public static final String ENGLISH = "ES";

    private static final String LANGUAGE = "LANGUAGE";

    public static Context attachBaseContext(Context context, String language) {
        LogUtils.d("attachBaseContext: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = getLanguage(language);

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    public static void initLanguage() {
        Locale locale = getMyLanguage();
        if (locale.getLanguage().contains(Locale.CHINESE.getLanguage())) {
            setAppLanguage(Locale.CHINESE);
        } else {
            setAppLanguage(Locale.ENGLISH);
        }
    }

    /**
     * 更改应用语言
     *
     * @param type 语言地区
     */
    public static void setAppLanguage(String type) {
        setAppLanguage(getLanguage(type));
        saveLanguageSetting(type);
    }

    /**
     * 更改应用语言
     *
     * @param language 语言地区
     */
    public static void setAppLanguage(Locale language) {
        Resources resources = MPApplication.getAppContext().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(language);
        DisplayMetrics metrics = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, metrics);
    }

    /**
     * 保存语言设置
     *
     * @param locale
     */
    private static void saveLanguageSetting(String locale) {
        SPUtil.put(MPApplication.getAppContext(), LANGUAGE, locale);
    }

    /**
     * 获取语言类型
     *
     * @return
     */
    public static String getLanguageType() {
        return (String) SPUtil.get(MPApplication.getAppContext(), LANGUAGE, CHINESE);
    }

    /**
     * 获取当前语言
     *
     * @return
     */
    public static Locale getMyLanguage() {
//        return Locale.CHINESE;
        String language = getLanguageType();
        return getLanguage(language);
    }

    /**
     * 获取语言类
     *
     * @param type
     * @return
     */
    public static Locale getLanguage(String type) {
//        return Locale.CHINESE;
        Locale locale;
        switch (type) {
            case CHINESE:
                locale = Locale.CHINESE;
                break;
            case ENGLISH:
                locale = Locale.ENGLISH;
                break;
            default:
//                Context context = MPApplication.getAppContext();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    locale = context.getResources().getConfiguration().getLocales().get(0);
//                } else {
//                    locale = context.getResources().getConfiguration().locale;
//                }
                locale = Locale.CHINESE;
                break;
        }
        return locale;
    }

    public static boolean isChinese() {
        return LanguageUtils.getMyLanguage().getLanguage().contains("zh");
    }

    /**
     * 判断是否与设定的语言相同.
     *
     * @return
     */
    public static boolean isSameWithSetting() {
        Locale current = MPApplication.getAppContext().getResources().getConfiguration().locale;
        return current.equals(getMyLanguage());
    }
}
