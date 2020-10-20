package com.mp.basekit.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mp.basekit.R;
import com.mp.basekit.dialog.DialogLoading;
import com.mp.basekit.util.LanguageUtils;
import com.mp.basekit.util.LogUtils;
import com.mp.basekit.util.StatusBarUtil;
import com.mp.basekit.util.toast.ToastUtils;

/**
 * Created by Administrator on 2017/2/6.
 */
public class MPActivity extends FragmentActivity {

    public String mSimpleName = getClass().getSimpleName();
    public static final int PERMISSION_CODE = 123;

    public static final String KEY_EXTRA = "extra";

    public static final String KEY_PUB = "wallet_pub";

    public static final String ACTION_UPDATE = "com.rongshutong.beecoin.core.update";

    /**
     * 此方法先于 onCreate()方法执行
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        //attach 对应语言环境下的context
        super.attachBaseContext(LanguageUtils.attachBaseContext(newBase, LanguageUtils.getLanguageType()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MPApplication.addActivity(this);
        LogUtils.d("test", "onCreate:" + mSimpleName);
    }

    public void setWindowStatusBarColor(int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setStatusBar(View view) {
        setStatusBar(view, true);
    }

    protected void setStatusBar(int statusId, boolean isLight) {
        setStatusBar(findViewById(statusId), isLight);
    }

    protected void setStatusBar(boolean isLightMode) {
        setStatusBar(null, isLightMode);
    }

    protected void setStatusBar(View view, boolean isLightMode) {
        StatusBarUtil.setTranslucentForImageView(this, 0, view);
        if (isLightMode) {
            StatusBarUtil.setLightMode(this);
        }
    }

    protected void setStatusHeight(int id) {
        setStatusHeight(findViewById(id));
    }

    protected void setStatusHeight(View view) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = getStatusBarHeight();
        view.setLayoutParams(layoutParams);
    }

    /**
     * 获取当前设备状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置状态栏透明
     *
     * @param on
     */
    protected void setTranslucentStatus(boolean on) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected void initRefreshView(SwipeRefreshLayout refreshLayout, SwipeRefreshLayout.OnRefreshListener listener) {
        refreshLayout.setColorSchemeResources(R.color.mp_color_refresh);
        refreshLayout.setEnabled(true);
        if (listener != null) {
            refreshLayout.setOnRefreshListener(listener);
        }
    }

    /**
     * 切换Fragment
     *
     * @param containerID
     * @param to
     */
    public void showFragment(int containerID, Fragment to) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (to.isAdded()) {
            to.onResume();
        } else {
            transaction.add(containerID, to);
        }
        transaction.show(to).commitAllowingStateLoss();// 显示下一个
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        MPApplication.removeActivity(this);
        super.onDestroy();
    }

    protected DialogLoading mLoadingDialog;

    public void showLoadingDialog() {
        showLoadingDialog("");
    }

    public void showLoadingDialog(final String text) {
        if (isDestroy()) {
            return;
        }
        runOnUiThread(() -> {
            if (mLoadingDialog == null) {
                mLoadingDialog = new DialogLoading(MPActivity.this);
            }

            mLoadingDialog.showLoading(text);
        });
    }

    public void loadingSuccess() {
        loadingSuccess("");
    }

    public void loadingSuccess(String sucMsg) {
        if (isDestroy()) {
            return;
        }
        runOnUiThread(() -> {
            if (mLoadingDialog == null) {
                mLoadingDialog = new DialogLoading(this);
            }
            mLoadingDialog.loadingSuc(sucMsg);
        });
    }

    public void hideLoadingDialog() {
        if (isDestroy()) {
            return;
        }
        runOnUiThread(() -> {
            if (mLoadingDialog != null) {
                mLoadingDialog.hideDialog();
            }
        });
    }

    public void showToast(int id) {
        showToast(getString(id));
    }

    public void showToast(final String msg) {
        if (isDestroy()) {
            return;
        }
        runOnUiThread(() -> ToastUtils.show(msg + ""));
    }

    protected long mClickTime = 0;

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

    protected boolean isDestroy() {
        return this.isFinishing() || this.isDestroyed();
    }

    @Override
    public void onBackPressed() {
        closeKeyboard();
        super.onBackPressed();
    }

    protected void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = getCurrentFocus();
        if (view != null) {
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(ACTION_UPDATE);
        registerReceiver(receiver, filter);
    }

    protected void setEditError(EditText editText, int msgId) {
        setEditError(editText, getString(msgId));
    }

    protected void setEditError(EditText editText, String msg) {
        showToast(msg);
        editText.requestFocus();
    }

    protected void start(Class<?> cls) {
        start(new Intent(this, cls));
    }

    protected void start(Intent intent) {
        startActivity(intent);
    }


    protected Boolean hideInputMethod(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    protected boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
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
