package com.mp.basekit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.mp.basekit.R;

/**
 * Created by Administrator on 2017/5/9.
 */
public class CheckView extends AppCompatImageView {

    private boolean isCheck, isClickable;
    private OnCheckChangeListener onCheckChangeListener;

    private int img, imgCheck;

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CheckView);
        img = array.getResourceId(R.styleable.CheckView_imgNormal, R.drawable.radio_off);
        imgCheck = array.getResourceId(R.styleable.CheckView_imgCheck, R.drawable.radio_on);
        isCheck = array.getBoolean(R.styleable.CheckView_checked, false);
        isClickable = array.getBoolean(R.styleable.CheckView_isClickable, true);
        array.recycle();//回收

        setImageResource(img);
        setCheck(isCheck);
        if (isClickable) {
            setOnClickListener(v -> {
                setCheck(!isCheck);
                if (onCheckChangeListener != null) {
                    onCheckChangeListener.onChange(isCheck);
                }
            });
        }
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
        int imgId = check ? imgCheck : img;
        setImageResource(imgId);
    }


    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        this.onCheckChangeListener = onCheckChangeListener;
    }

    public interface OnCheckChangeListener {
        void onChange(boolean bool);
    }
}
