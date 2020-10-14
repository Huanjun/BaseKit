package com.mp.basekit.dialog

import android.app.Activity
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.mp.basekit.R
import kotlinx.android.synthetic.main.dialog_common.*

class CommonDialog(activity: Activity) : MPDialog(activity) {

    private var mListener: OnCommonListener? = null

    override fun initLayout(): Int {
        return R.layout.dialog_common
    }

    override fun getDialogWith(): Float {
        return 0.7f
    }

    override fun initEvent() {
        ivClose.setOnClickListener {
            hideDialog()
        }
        btnLeft.setOnClickListener {
            if (mListener != null) {
                mListener!!.onLeftListener(this)
            } else {
                hideDialog()
            }
        }
        btnRight.setOnClickListener {
            if (mListener != null) {
                mListener!!.onRightListener(this)
            } else {
                hideDialog()
            }
        }
    }

    private fun setTitleText(text: String?) {
        if (tvTitle != null) {
            if (TextUtils.isEmpty(text)) {
                tvTitle!!.text = ""
                tvTitle!!.visibility = View.GONE
            } else {
                tvTitle!!.visibility = View.VISIBLE
                tvTitle!!.text = text
            }
        }
    }

    public fun setContentText(text: CharSequence?) {
        if (tvContent != null) {
            if (TextUtils.isEmpty(text)) {
                tvContent!!.text = ""
                tvContent!!.visibility = View.GONE
            } else {
                tvContent!!.visibility = View.VISIBLE
                tvContent!!.text = text
            }
        }
    }

    private fun setContentText(text: String?) {
        if (tvContent != null) {
            if (TextUtils.isEmpty(text)) {
                tvContent!!.text = ""
                tvContent!!.visibility = View.GONE
            } else {
                tvContent!!.visibility = View.VISIBLE
                tvContent!!.text = text
            }
        }
    }

    private fun setLeftText(text: String?, color: Int = R.color.mp_font_gray_light) {
        if (btnLeft != null) {
            if (TextUtils.isEmpty(text)) {
                btnLeft!!.text = ""
                btnLeft!!.visibility = View.GONE
                val lp = btnRight.layoutParams
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
                btnRight.layoutParams = lp
//                lineDriver!!.visibility = View.GONE
            } else {
                btnLeft!!.text = text
                btnLeft!!.visibility = View.VISIBLE
//                lineDriver!!.visibility = View.VISIBLE
            }
//            btnLeft!!.setTextColor(activity.resources.getColor(if (color == 0) R.color.mp_font_gray_light else color))
        }
    }

    private fun setRightText(text: String?, color: Int = R.color.mp_color_blue) {
        if (btnRight != null) {
            if (TextUtils.isEmpty(text)) {
                btnRight!!.text = ""
                btnRight!!.visibility = View.GONE
//                lineDriver!!.visibility = View.GONE
            } else {
                btnRight!!.text = text
//                btnRight!!.visibility = View.VISIBLE
            }
//            btnRight!!.setTextColor(activity.resources.getColor(if (color == 0) R.color.mp_color_blue else color))
        }
    }

    fun setBackRes(bgRes: Int, bgTextRes: Int) {
        if (layoutContent != null) {
            layoutContent.setBackgroundResource(bgRes)
            tvContent.setBackgroundResource(bgTextRes)
        }
    }

    fun showClose(showClose: Boolean) {
        if (ivClose != null) {
            ivClose.visibility = if (showClose) View.VISIBLE else View.GONE
        }
    }

    fun setOnListener(listener: OnCommonListener?) {
        mListener = listener
    }

    class Builder {
        private var title: String? = null
        private var content: String? = null
        private var leftText: String? = null
        private var rightText: String? = null
        private var listener: OnCommonListener? = null
        private var bgRes: Int? = null
        private var bgTextRes: Int? = null
        private var isShowClose: Boolean = false

        fun setLiftText(cancelText: String): Builder {
            leftText = cancelText
            return this
        }

        fun setRightText(rightText: String): Builder {
            this.rightText = rightText
            return this
        }

        fun setTitleText(title: String): Builder {
            this.title = title
            return this
        }

        fun setContentText(content: String): Builder {
            this.content = content
            return this
        }

        fun setListener(listener: OnCommonListener): Builder {
            this.listener = listener
            return this
        }

        fun setShowClose(showClose: Boolean): Builder {
            this.isShowClose = showClose
            return this
        }

        fun setBgResource(bgRes: Int, bgTextRes: Int): Builder {
            this.bgRes = bgRes
            this.bgTextRes = bgTextRes
            return this
        }

        fun show(act: Activity): CommonDialog {
            val dialog = CommonDialog(act)
            dialog.show()
            if (leftText != null) {
                dialog.setLeftText(leftText)
            }
            if (rightText != null) {
                dialog.setRightText(rightText)
            }
            if (title != null)
                dialog.setTitleText(title)
            dialog.setContentText(content)
            dialog.showClose(isShowClose)
            if (bgRes != null && bgTextRes != null) {
                dialog.setBackRes(bgRes!!, bgTextRes!!)
            }
            listener?.let {
                dialog.setOnListener(it)
            }

            return dialog
        }
    }

    interface OnCommonListener {
        fun onRightListener(dialog: CommonDialog)

        fun onLeftListener(dialog: CommonDialog)
    }
}
