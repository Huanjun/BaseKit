package com.mp.basekit.util.toast.anim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by zia on 2018/5/13.
 * 通过画path制作动画
 */
public class PathAnimView extends ToastImage {

    public static final int WARNING = 0;
    public static final int SUCCESS = 1;

    private Path rightDstPath;
    private PathMeasure mPathMeasure;
    private float percentage = 0;
    private boolean begin = false;
    private int duration = 1000;
    private Paint paint;
    private int color = -23333;

    private int type = WARNING;
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    public PathAnimView(Context context) {
        super(context);
        init();
    }

    public PathAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.STROKE);
        rightDstPath = new Path();
        mPathMeasure = new PathMeasure();
    }

    public void refresh(int type) {
        this.type = type;
        begin = false;
        valueAnimator.cancel();
        percentage = 0f;
        rightDstPath.reset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Path> paths;
        if (type == SUCCESS) {
            paint.setStrokeWidth(getWidth() * 0.06f);
            paint.setColor(Color.parseColor("#FF2BC93E"));
            paths = AnimPath.getSucPaths(getWidth(), getHeight());
        } else {
            paint.setStrokeWidth(getWidth() * 0.05f);
            paint.setColor(Color.parseColor("#FFA900"));
            paths = AnimPath.getWarningPaths(getWidth(), getHeight());
        }
        if (!begin) {
            if (color != -23333) {
                paint.setColor(color);
            }
            valueAnimator.addUpdateListener(animation -> {
                percentage = (float) animation.getAnimatedValue();
                postInvalidate();
            });
            valueAnimator.setDuration(duration).start();
            begin = true;
        }
        for (Path path : paths) {
            mPathMeasure.setPath(path, false);
            mPathMeasure.getSegment(0, mPathMeasure.getLength() * percentage, rightDstPath, true);
            canvas.drawPath(rightDstPath, paint);
        }
    }

}
