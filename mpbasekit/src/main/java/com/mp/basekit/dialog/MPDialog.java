package com.mp.basekit.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.mp.basekit.R;
import com.mp.basekit.util.DensityUtil;

import java.util.Objects;

public abstract class MPDialog extends Dialog {

    public MPDialog dialog;
    public Activity activity;
    protected Point mScreenPoint = new Point();

    public MPDialog(@NonNull Activity activity) {
        super(activity);
        this.dialog = this;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(R.color.mp_color_transparent);
        setContentView(initLayout());
        initDialogWindow(getDialogWith());
        setCanceledOnTouchOutside(false);
        initEvent();
    }

    protected float getDialogWith() {
        return 0.85f;
    }

    protected int getAnim() {
        return R.style.MpDialogCommonAnim;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    protected void initDialogWindow(float width) {
        Window dialogWindow = getWindow();
        //WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.CENTER);
        //lp.y = - Utils.dip2px(50, mRootActivity.getResources());
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        try {
            d.getSize(mScreenPoint);
        } catch (NoSuchMethodError ignore) { // Older device
            mScreenPoint.x = d.getWidth();
            mScreenPoint.y = d.getHeight();
        }

        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
            p.width = (int) (mScreenPoint.x * width);
        } else {// 横屏时
            p.width = DensityUtil.dip2px(300);
        }

        dialogWindow.setWindowAnimations(getAnim());
        dialogWindow.setAttributes(p);
    }

    public abstract int initLayout();

    public abstract void initEvent();

    /**
     * 设置dialog其他屏幕地方是否触摸消失
     *
     * @param isSetCanceledOnTouchOutside
     * @return
     */
    public MPDialog setCanceledOnTouchOutside(Boolean isSetCanceledOnTouchOutside) {
        dialog.setCanceledOnTouchOutside(isSetCanceledOnTouchOutside);
        return this;
    }

    /**
     * 设置回退键是否有效
     *
     * @param isCancelable
     * @return
     */
    public MPDialog setCancelable(Boolean isCancelable) {
        dialog.setCancelable(isCancelable);
        return this;
    }

    /**
     * 显示dialog
     *
     * @return
     */
    public MPDialog showDialog() {
        if ((activity != null && !activity.isDestroyed() && !activity.isFinishing()) && !isShowing()) {
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * 关闭dialog
     *
     * @return
     */
    public MPDialog hideDialog() {
        if (activity != null && !activity.isDestroyed() && !activity.isFinishing() && isShowing()) {
            closeKeyboard();
            try {
                dialog.dismiss();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * 设置回调  两个点击事件
     */
    protected CallBack callBack;

    public interface CallBack {
        void onClick(View v);
    }

    public MPDialog setCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }


    /**
     * 设置点击一个条目
     */

    protected ClickSingleCallBack clickSingleCallBack;

    public interface ClickSingleCallBack {
        void click(String item);
    }

    public MPDialog setClickSingleCallBack(ClickSingleCallBack clickSingleCallBack) {
        this.clickSingleCallBack = clickSingleCallBack;
        return this;
    }

    protected void closeKeyboard() {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = getCurrentFocus();
        if (view != null) {
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}