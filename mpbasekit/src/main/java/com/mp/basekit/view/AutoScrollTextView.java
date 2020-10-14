package com.mp.basekit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.mp.basekit.util.DensityUtil;

public class AutoScrollTextView extends AppCompatTextView {

    private float textWidth = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;//文字的横坐标
    private float y = 0f;//文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
    public boolean isStarting = true;//是否开始滚动
    private Paint paint = null;//绘图样式
    private float speed = DensityUtil.dip2px(1);
    private int textColor = 0xFFFEFEFE;
    private boolean isMeasure = false;

    public AutoScrollTextView(Context context) {
        super(context);
    }

    public AutoScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollText(String text) {
        isMeasure = false;
        setText(text);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public void measureText() {
        paint = getPaint();
        if (paint != null) {
            paint.setColor(getTextColor());
            paint.setTextSize(getTextSize());
            paint.setAntiAlias(true);
            paint.setTextAlign(Paint.Align.CENTER);

            Rect mBounds = new Rect();
            String text = getText().toString();
            paint.getTextBounds(text, 0, getText().length(), mBounds);
            textWidth = mBounds.width();
        }
        viewWidth = getWidth();
        step = 0;
        temp_view_plus_text_length = viewWidth + textWidth / 2;
        y = getBaseline();
    }

    public void startScroll(float speed) {
        isStarting = true;
        this.speed = speed;
        invalidate();
    }


    public void stopScroll() {
        isStarting = false;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (getText() == null)
            return;
        if (isStarting) {
            if (!isMeasure) {// 文字宽度只需获取一次
                isMeasure = true;
                measureText();
            }
            //水平方向由右向左
            CharSequence text = getText().toString();
            if (!TextUtils.isEmpty(text)) {
                canvas.drawText(text, 0, text.length(), temp_view_plus_text_length - step, y, paint);
            }
            if (!isStarting) {
                return;
            }
            step += speed;
            if (step > temp_view_plus_text_length + textWidth / 2)
                step = 0;
            invalidate();
        } else {
            super.onDraw(canvas);
        }
    }

}