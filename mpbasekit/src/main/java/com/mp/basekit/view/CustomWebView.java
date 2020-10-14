package com.mp.basekit.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.mp.basekit.R;
import com.mp.basekit.util.DensityUtil;

import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/7/14.
 */
public class CustomWebView extends WebView {

    private static final int CUSTOM_PROGRESS = 67;
    private ProgressBar progressbar;
    private int mProgress = 0;
    private Timer mTimer;

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(2), 0, 0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.mp_layer_progress_bg);
        progressbar.setProgressDrawable(drawable);
        addView(progressbar);
        // setWebViewClient(new WebViewClient(){});
        setWebChromeClient(new CustomWebChromeClient());
        //是否可以缩放
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        initTimeTask();
    }

    private void initTimeTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                progressbar.setProgress(mProgress);
                mProgress = mProgress + new SecureRandom().nextInt(10);
                if (mProgress >= CUSTOM_PROGRESS) {
                    mTimer.cancel();
                }
            }
        };
        timerTask.run();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 100, 100);
    }

    public void setProgress(int progress) {
        if (progress > CUSTOM_PROGRESS) {
            mTimer.cancel();
            progressbar.setProgress(progress);
            if (progress == 100) {
                postDelayed(() -> progressbar.setVisibility(GONE), 100);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
            }
        }
    }

    public class CustomWebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > CUSTOM_PROGRESS) {
                mTimer.cancel();
                progressbar.setProgress(newProgress);
                if (newProgress == 100) {
                    postDelayed(() -> progressbar.setVisibility(GONE), 100);
                } else {
                    if (progressbar.getVisibility() == GONE)
                        progressbar.setVisibility(VISIBLE);
                }
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.getScrollY() <= 0)
                    this.scrollTo(0, 1);
                break;
        }
        return super.onTouchEvent(event);
    }
}
