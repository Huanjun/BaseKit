package com.mp.basekit.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mp.basekit.R;
import com.mp.basekit.view.CustomLoadingView;

public class MPLoadingDialog extends MPDialog {

    private TextView mMessageView;
    private TextView mTextView;
    private CustomLoadingView loadingView;
    private ImageView mIcon;

    String mMessage = "";

    public MPLoadingDialog(@NonNull Activity activity) {
        super(activity);
    }

    public void setMessage(String message) {
        setMessage(0, message, getContext().getResources().getString(R.string.mp_dialog_loading));
    }

    public void setMessage(int imgRes, String message, String loadStr) {
        mMessage = message;
        if (mIcon == null)
            return;
        if (imgRes != 0) {
            mIcon.setImageResource(imgRes);
            mIcon.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        } else {
            loadingView.setVisibility(View.VISIBLE);
            mIcon.setVisibility(View.GONE);
        }
        if (mMessageView != null) {
            if (TextUtils.isEmpty(message)) {
                mMessageView.setVisibility(View.GONE);
            } else {
                mMessageView.setVisibility(View.VISIBLE);
                mMessageView.setText(message);
            }
        }
        if (mTextView != null) {
            if (TextUtils.isEmpty(loadStr)) {
                mTextView.setVisibility(View.GONE);
            } else {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(loadStr);
            }
        }
    }

    public boolean isShowing() {
        return super.isShowing();
    }

    /**
     * 使用完关闭掉
     *
     * @return
     */
    public void isFinish() {
        loadingView.setFinish();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public int initLayout() {
        return R.layout.mp_dialog_loading;
    }

    @Override
    public void initEvent() {
        mMessageView = findViewById(R.id.text);
        mTextView = findViewById(R.id.text1);
        mIcon = findViewById(R.id.icon);
        loadingView = findViewById(R.id.loadingView);
        if (TextUtils.isEmpty(mMessage)) {
            mMessageView.setVisibility(View.GONE);
        } else {
            mMessageView.setVisibility(View.VISIBLE);
            mMessageView.setText(mMessage);
        }
    }

}
