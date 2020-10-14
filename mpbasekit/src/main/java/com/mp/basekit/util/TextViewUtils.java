package com.mp.basekit.util;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.widget.TextView;

public class TextViewUtils {

    /**
     * @param tv         控件
     * @param startColor 开始颜色 Color.WHITE
     * @param endColor   结束颜色 Color.parseColor("#5A5A5A")
     */
    public static void setTextColorGradient(TextView tv, int startColor, int endColor) {
        setTextColorGradient(tv, startColor, endColor, 0f, tv.getText().toString().length());
    }

    /**
     * @param tv         控件
     * @param startColor 开始颜色 Color.WHITE
     * @param endColor   结束颜色 Color.parseColor("#5A5A5A")
     * @param start      开始位置 0.4f
     * @param end        结束位置 0.9f
     */
    public static void setTextColorGradient(TextView tv, int startColor, int endColor, float start, float end) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, 0,
                tv.getPaint().getTextSize(),
                new int[]{startColor, endColor},
                new float[]{start, end},
                Shader.TileMode.CLAMP);
        tv.getPaint().setShader(mLinearGradient);
    }

    /**
     * @param tv           控件
     * @param colorList    开始到结束颜色集合 new int[] { sratrColor,endColor}
     * @param locationList 对应上述颜色的坐标点 new float[]{0f,1.0f}
     */
    public static void setTextColorGradient(TextView tv, int[] colorList, float[] locationList) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, 0,
                tv.getPaint().getTextSize(),
                colorList,
                locationList,
                Shader.TileMode.CLAMP);
        tv.getPaint().setShader(mLinearGradient);
    }
}
