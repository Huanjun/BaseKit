package com.mp.basekit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mp.basekit.R;

/**
 * Created by  on 2016/8/30.
 * <p>
 */

public class MPEmptyView extends RelativeLayout {

    private TextView tvText;
    private ImageView ivImage;
    private TextView tvError;

    private OnClickListener mListener;

    public MPEmptyView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.MPEmptyView);

        View view = View.inflate(context, R.layout.mp_view_empty, this);


        //普通的无数据
        ivImage = view.findViewById(R.id.ivImage);
        tvText = view.findViewById(R.id.tvText);
        String textEmpty = a.getString(R.styleable.MPEmptyView_textEmpty);
        int imageEmpty = a.getResourceId(R.styleable.MPEmptyView_imgEmpty, R.drawable.hint_none);
        int textColor = a.getResourceId(R.styleable.MPEmptyView_textColor, R.color.mp_color_gray);


        //访问错误
        tvError = view.findViewById(R.id.tvError);
        String textError = a.getString(R.styleable.MPEmptyView_textError);
        int imageError = a.getResourceId(R.styleable.MPEmptyView_imgError, R.drawable.ic_empty);
        a.recycle();

        //普通无数据
        ivImage.setImageResource(imageEmpty);

        if (TextUtils.isEmpty(textEmpty)) {
            textEmpty = getContext().getString(R.string.mp_no_data);
        }
        tvText.setText(textEmpty);
        tvText.setTextColor(getResources().getColor(textColor));
        tvError.setTextColor(getResources().getColor(textColor));

        //访问错误
        if (TextUtils.isEmpty(textError)) {
            textError = getContext().getString(R.string.mp_error_data);
        }

        tvError.setText(textError);
        ivImage.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(v);
            }
        });
    }

    public void setText(String titleText) {
        if (tvText != null && !TextUtils.isEmpty(titleText)) {
            tvText.setText(titleText);
        }
    }

    public void setImageRes(int res) {
        if (ivImage != null && res != 0) {
            ivImage.setImageResource(res);
        }
    }

    /**
     * 请求错误  不传是默认的
     *
     * @param errorText
     */
    public void setErrorText(String errorText) {
        if (tvError != null && !TextUtils.isEmpty(errorText)) {
            tvError.setText(errorText);
        }
    }

    /**
     * 显示空数据视图
     */
    public void showEmpty() {
        this.setVisibility(View.VISIBLE);
        if (tvText != null && ivImage != null) {
            tvText.setVisibility(View.VISIBLE);
            ivImage.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
        } else {
            hide();
        }
    }

    /**
     * 显示请求错误视图
     */
    public void showError() {
        this.setVisibility(View.VISIBLE);
        if (tvError != null) {
            tvText.setVisibility(View.GONE);
            ivImage.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
        } else {
            hide();
        }
    }

    /**
     * 全部隐藏
     */
    public void hide() {
        if (tvError != null) {
            tvText.setVisibility(View.GONE);
            ivImage.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        }
        this.setVisibility(View.GONE);
    }

    public void setOnClickListener(OnClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnClickListener {
        void onClick(View v);
    }
}
