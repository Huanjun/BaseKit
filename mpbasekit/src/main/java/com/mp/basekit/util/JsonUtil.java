package com.mp.basekit.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 1:将JavaBean转换成Map、JSONObject
 * 2:将Map转换成Javabean
 * 3:将JSONObject转换成Map、Javabean
 */
public class JsonUtil {

    /**
     * 将Javabean转换为Map
     *
     * @param javaBean javaBean
     * @return Map对象
     */
    public static Map toMap(Object javaBean) {
        Map result = new HashMap();
        Method[] methods = javaBean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("getCoins")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("getCoins") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    Object value = method.invoke(javaBean, (Object[]) null);
                    result.put(field, null == value ? "" : value.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * 将Json对象转换成Map
     *
     * @param jsonString json对象
     * @return Map对象
     * @throws JSONException
     */
    public static Map toMap(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
        }
        return result;

    }

    /**
     * 将JavaBean转换成JSONObject（通过Map中转）
     *
     * @param bean javaBean
     * @return json对象
     */
    public static JSONObject toJSON(Object bean) {
        return new JSONObject(toMap(bean));
    }

    /**
     * 将Map转换成Javabean
     *
     * @param javabean javaBean
     * @param data     Map数据
     */
    public static Object toJavaBean(Object javabean, Map data) {

        Method[] methods = javabean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("set")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    method.invoke(javabean, data.get(field));
                }
            } catch (Exception e) {
            }

        }

        return javabean;

    }

    /**
     * JSONObject到JavaBean
     *
     * @param javabean javaBean
     * @return json对象
     * @throws ParseException json解析异常
     * @throws JSONException
     */
    public static Object toJavaBean(Object javabean, String jsonString)
            throws ParseException, JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        Map map = toMap(jsonObject.toString());
        return toJavaBean(javabean, map);
    }

    //将Json数据解析成相应的映射对象
    public static <T> T parseJson(String jsonData, Type type) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    /**
     * 将Json数据解析成相应的映射对象
     *
     * @param jsonData
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseJson(String jsonData, Class<T> type) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    /**
     * 将Json数据解析成相应的映射对象
     *
     * @param jsonData
     * @param <T>
     * @return
     */
    public static <T> String parse2String(T jsonData) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(jsonData);
    }

    /**
     * 获取json value值
     *
     * @param json
     * @param key
     * @return
     */
    public static String getJsonValue(String json, String key) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);

            Iterator<String> keyIter = jsonObject.keys();
            while (keyIter.hasNext()) {
                String key1 = keyIter.next();
                String value = jsonObject.getString(key1);
                if (key.equals(value)) {
                    return key1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
