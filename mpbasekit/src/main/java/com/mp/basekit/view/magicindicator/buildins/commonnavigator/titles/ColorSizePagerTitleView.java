package com.mp.basekit.view.magicindicator.buildins.commonnavigator.titles;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;

import com.mp.basekit.view.magicindicator.buildins.ArgbEvaluatorHolder;
import com.mp.basekit.view.magicindicator.buildins.UIUtil;


/**
 * 两种颜色大小过渡的指示器标题
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class ColorSizePagerTitleView extends SimplePagerTitleView {

    private float mScale = 0.1f;

    public ColorSizePagerTitleView(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        setGravity(Gravity.CENTER);
        int padding = UIUtil.dip2px(context, 12);
        setPadding(padding, 0, padding, 0);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        setTextColor(color);
        float scale = ((1 + mScale) - mScale * leavePercent);
        setScaleX(scale);
        setScaleY(scale);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        setTextColor(color);
        float scale = (1 + mScale * enterPercent);
        setScaleX(scale);
        setScaleY(scale);
    }

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

}
