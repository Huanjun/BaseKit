package com.mp.basekit.util.toast.anim;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by zia on 2018/5/13.
 */
public class ToastImage extends AppCompatImageView implements IAnim {

    public ToastImage(Context context) {
        super(context);
    }

    public ToastImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToastImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDuration(int duration) {

    }

    @Override
    public void setColor(int color) {

    }
}
