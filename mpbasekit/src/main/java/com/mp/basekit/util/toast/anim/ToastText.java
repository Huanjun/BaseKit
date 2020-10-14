package com.mp.basekit.util.toast.anim;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by zia on 2018/5/14.
 */
public class ToastText extends AppCompatTextView implements IAnim {
    public ToastText(Context context) {
        super(context);
    }

    public ToastText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToastText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setDuration(int duration) {

    }

    @Override
    public void setColor(int color) {

    }
}
