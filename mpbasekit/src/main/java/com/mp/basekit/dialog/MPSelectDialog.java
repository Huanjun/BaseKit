package com.mp.basekit.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mp.basekit.R;
import com.mp.basekit.view.spinnerwheel.WheelVerticalView;
import com.mp.basekit.view.spinnerwheel.adapters.AbstractWheelTextAdapter;

import java.util.List;

public class MPSelectDialog extends MPDialog implements View.OnClickListener {

    WheelVerticalView wheelSelector;
    RelativeLayout loginDialog;
    TextView tvTitle;

    private List<String> mDatas;
    private int mOriginDayIndex = 0;
    private OnCheckedListener mOnCheckedListener;

    public MPSelectDialog(@NonNull Activity activity, List<String> datas) {
        super(activity);
        mDatas = datas;
    }

    @Override
    public int initLayout() {
        return R.layout.mp_dialog_select;
    }

    @Override
    public void initEvent() {
        wheelSelector = findViewById(R.id.wheelSelector);
        loginDialog = findViewById(R.id.login_dialog);
        tvTitle = findViewById(R.id.tvTitle);
//        wheelSelector.setVisibleItems(3);
        wheelSelector.setCurrentItem(mOriginDayIndex);
        wheelSelector.setViewAdapter(new DaySelectorAdapter(activity));
        findViewById(R.id.tvCancel).setOnClickListener(this);
        findViewById(R.id.tvSure).setOnClickListener(this);
    }

    @Override
    protected void initDialogWindow(float width) {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        try {
            d.getSize(mScreenPoint);
        } catch (NoSuchMethodError ignore) { // Older device
            mScreenPoint.x = d.getWidth();
            mScreenPoint.y = d.getHeight();
        }

        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();// 获取对话框当前的参数值
        layoutParams.width = mScreenPoint.x;
        dialogWindow.setWindowAnimations(R.style.MyDialogBottomUp);//设置动画效果
        dialogWindow.setAttributes(layoutParams);
    }

    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        mOnCheckedListener = listener;
    }

    public void setCurrentItem(String item) {
        if (wheelSelector == null) {
            return;
        }
        if (TextUtils.isEmpty(item)) {
            mOriginDayIndex = 0;
        } else {
            if (mDatas != null) {
                for (int i = 0; i < mDatas.size(); i++) {
                    String data = mDatas.get(i);
                    if (item.equals(data)) {
                        mOriginDayIndex = i;
                        break;
                    }
                }
            }
        }
        wheelSelector.setCurrentItem(mOriginDayIndex);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tvCancel) {
            hideDialog();
        } else if (i == R.id.tvSure) {
            if (mOnCheckedListener != null) {
                mOnCheckedListener.onChecked(wheelSelector.getCurrentItem());
            }
        }
    }

    public interface OnCheckedListener {
        void onChecked(int pos);
    }

    private class DaySelectorAdapter extends AbstractWheelTextAdapter {
        /**
         * Constructor
         */
        protected DaySelectorAdapter(Context context) {
            super(context, R.layout.item_list_select, NO_RESOURCE);
            setItemTextResource(R.id.name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return mDatas.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return mDatas.get(index);
        }
    }
}
