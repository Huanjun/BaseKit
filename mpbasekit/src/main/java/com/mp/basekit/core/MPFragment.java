package com.mp.basekit.core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.mp.basekit.dialog.DialogLoading;
import com.mp.basekit.event.LoginExpiredEvent;
import com.mp.basekit.event.LoginOutEvent;
import com.mp.basekit.event.LoginSuccessEvent;
import com.mp.basekit.util.toast.ToastUtils;

public class MPFragment extends Fragment {

    protected Activity mContext;

    protected View rootView;

    protected long mClickTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        closeKeyboard();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private String getName() {
        return this.getClass().getSimpleName();
    }

    public void showToast(final String msg) {
        mContext = getActivity();
        if (isValidContext(mContext) && !TextUtils.isEmpty(msg)) {
            mContext.runOnUiThread(() -> ToastUtils.show(msg));
        }
    }

    protected DialogLoading mLoadingDialog;

    protected void showLoadingDialog() {
        showLoadingDialog("");
    }

    protected void showLoadingDialog(final String text) {
        mContext = getActivity();
        if (isValidContext(mContext)) {
            mContext.runOnUiThread(() -> {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new DialogLoading(mContext);
                }

                mLoadingDialog.showLoading(text);
            });
        }
    }

    protected void hideLoadingDialog() {
        if (isValidContext(mContext)) {
            mContext.runOnUiThread(() -> {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            });
        }
    }

    protected void loadingSuccess() {
        loadingSuccess("");
    }

    protected void loadingSuccess(String sucMsg) {
        mContext = getActivity();
        if (isValidContext(mContext)) {
            mContext.runOnUiThread(() -> {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new DialogLoading(mContext);
                }
                mLoadingDialog.loadingSuc(sucMsg);
            });
        }
    }

    protected boolean isClickSoon() {
        if (mClickTime == 0) {
            mClickTime = System.currentTimeMillis();
            return false;
        } else if (System.currentTimeMillis() - mClickTime < 500) {
            return true;
        } else {
            mClickTime = System.currentTimeMillis();
        }
        return false;
    }

    protected boolean isValidContext(Activity activity) {
        return !(activity == null || activity.isDestroyed() || activity.isFinishing());
    }


    /**
     * 关闭软键盘
     */
    protected void closeKeyboard() {
        if (isValidContext(mContext)) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = mContext.getCurrentFocus();
        if (view != null) {
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void runOnMainThread(Runnable runnable) {
        mContext = getActivity();
        if (isValidContext(mContext)) {
            mContext.runOnUiThread(runnable);
        }
    }

    /**
     * 登录过期
     */
    protected void loginExpired() {

    }

    protected void loginOut() {

    }

    protected void loginSuccess() {

    }
}
