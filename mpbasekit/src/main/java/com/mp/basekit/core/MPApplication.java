package com.mp.basekit.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.mp.basekit.util.FileUtils;
import com.mp.basekit.util.LanguageUtils;
import com.mp.basekit.util.VersionUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPApplication extends Application {

    protected static MPApplication sApp;
    protected AppCore mAppCore;
    public static boolean IS_PUBLISH_VERSION = false;
    public List<Activity> mActivities = new ArrayList<Activity>();
    public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/casino";

    public static String getMyCacheDir() {
        //图片沙盒文件夹
//        File doc = sApp.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String doc = SDCARD_ROOT;
        FileUtils.createOrExistsDir(new File(doc));
        File dir = new File(doc + "/cache/");
        try {
            if (!dir.exists()) {
                dir.mkdir();//如果该文件夹不存在，则新建
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir.getAbsolutePath();
    }

    public static String getMyCrashDir() {
        //图片沙盒文件夹
//        File doc = sApp.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String doc = SDCARD_ROOT;
        FileUtils.createOrExistsDir(new File(doc));
        File dir = new File(doc + "/crash/");
        try {
            if (!dir.exists()) {
                dir.mkdir();//如果该文件夹不存在，则新建
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir.getAbsolutePath();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static MPApplication getApplication() {
        return sApp;
    }

    public static Context getAppContext() {
        return sApp.getApplicationContext();
    }

    private void init() {
        mAppCore = new AppCore();
        Log.e("channel", VersionUtils.getSourceId(this));
        //注册Activity生命周期监听回调
        registerActivityLifecycleCallbacks(callbacks);
    }

    public void unInit() {
        mAppCore.unInit();
    }

    protected ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NotNull Activity activity, Bundle savedInstanceState) {
            //强制修改应用语言
            if (!LanguageUtils.isSameWithSetting()) {
                LanguageUtils.setAppLanguage(LanguageUtils.getMyLanguage());
            }
        }

        @Override
        public void onActivityStarted(@NotNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NotNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NotNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NotNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NotNull Activity activity) {

        }
        //Activity 其它生命周期的回调
    };

    public static AppCore getAppCore() {
        return sApp.mAppCore;
    }

    public static void addActivity(Activity activity) {
        if (sApp != null) {
            sApp.mActivities.add(activity);
        }
    }

    public static Activity getTopActivity() {
        if (sApp != null && sApp.mActivities.size() > 0) {
            return sApp.mActivities.get(sApp.mActivities.size() - 1);
        } else {
            return null;
        }
    }

    public static void removeActivity(Activity activity) {
        if (sApp != null) {
            sApp.mActivities.remove(activity);
        }
    }

    public static void clearActivities() {
        Handler handler = new Handler();
        for (Activity activity : sApp.mActivities) {
            handler.postDelayed(() -> {
                if (activity != null) {
                    activity.finish();
                }
            }, 200);
        }
        sApp.mActivities.clear();
    }

    public static void exit() {
        clearActivities();
        sApp.unInit();
    }

    public void recreateActivity() {
        for (int i = 0; i < mActivities.size(); i++) {
            Activity activity = mActivities.get(i);
            activity.recreate();
        }
    }

    public static List<Activity> getActivities() {
        if (sApp != null) {
            return sApp.mActivities;
        }
        return null;
    }

}