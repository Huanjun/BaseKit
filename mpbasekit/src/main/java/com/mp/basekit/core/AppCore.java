package com.mp.basekit.core;

import com.mp.basekit.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class AppCore {
    private Map<String, BaseManager> mName2Manager = new HashMap<String, BaseManager>();

    public void init(String ...managers) {
        LogUtils.isDebug = !MPApplication.IS_PUBLISH_VERSION;
        for (String manager: managers) {
            getManager(manager).onCreate();
        }
    }

    public void unInit() {
        for (BaseManager baseManager : mName2Manager.values()) {
            baseManager.onDestroy();
        }
        mName2Manager.clear();
    }

    public BaseManager getManager(String name) {
        BaseManager manager = mName2Manager.get(name);
        if (manager != null) {
            return manager;
        }

        Class<?> clazz = null;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (clazz == null) {
            return null;
        }

        synchronized (clazz) {
            manager = mName2Manager.get(name);
            if (manager != null) {
                return manager;
            }
            try {
                manager = (BaseManager) clazz.newInstance();
                mName2Manager.put(name, manager);
                return manager;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
