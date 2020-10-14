package com.mp.basekit.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mp.basekit.R;
import com.mp.basekit.util.toast.anim.SuccessAnim;
import com.zyao89.view.zloading.ZLoadingView;
import com.zyao89.view.zloading.Z_TYPE;

/**
 * 加载动画
 */

public class DialogLoading extends MPDialog {
    private ZLoadingView zLoadingView;
    private SuccessAnim successAnim;
    private TextView textView;
    private boolean isDialog = false;

    public DialogLoading(@NonNull Activity context) {
        super(context);
    }

    public DialogLoading(@NonNull Activity context, Boolean isDialog) {
        super(context);
        this.isDialog = isDialog;
    }

    @Override
    public int initLayout() {
        return R.layout.dialog_loading;
    }

    @Override
    public void initEvent() {
        init();
    }

    private void init() {
        zLoadingView = findViewById(R.id.z_loading);
        successAnim = findViewById(R.id.sucView);
        textView = findViewById(R.id.loadText);
        zLoadingView.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE);
    }

    @Override
    protected void initDialogWindow(float width) {
        if (!isDialog) {
            Window window = getWindow(); //得到对话框
            if (window != null) {
                window.setDimAmount(0f);//核心代码 解决了无法去除遮罩问题
                window.setWindowAnimations(R.style.MpDialogCommonAnim); //设置窗口弹出动画
            }
        }
    }

    public void showLoading() {
        showLoading(getContext().getString(R.string.mp_dialog_loading));
    }

    public void showLoading(String text) {
        showDialog();
        zLoadingView.setVisibility(View.VISIBLE);
        successAnim.setVisibility(View.GONE);
        if (TextUtils.isEmpty(text)) {
            text = getContext().getString(R.string.mp_dialog_loading);
        }
        textView.setText(text);
    }

    public void loadingSuc(String sucMsg) {
        if (!isShowing()) {
            showLoading();
        }
        if (zLoadingView == null) {
            return;
        }
        zLoadingView.setVisibility(View.GONE);
        successAnim.setVisibility(View.VISIBLE);
        successAnim.refresh();
        if (TextUtils.isEmpty(sucMsg)) {
            textView.setText(getContext().getString(R.string.mp_success));
        } else {
            textView.setText(sucMsg);
        }
        zLoadingView.postDelayed(this::hideDialog, 1000);
    }

}
