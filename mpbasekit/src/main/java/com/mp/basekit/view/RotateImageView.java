package com.mp.basekit.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class RotateImageView extends ImageView {

    public RotateImageView(Context context) {
        super(context);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animator.cancel();
    }

    private ObjectAnimator animator;
    private long duration = 3000;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        start();
    }

    public void setDuration(long duration) {
        this.duration = duration;
        start();
    }

    public void start() {
        start(duration, 0, -360);
    }

    public void start(long duration, float start, float end) {
        stop();
        animator = ObjectAnimator.ofFloat(this, "rotation", start, end)
                .setDuration(duration);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
    }
}