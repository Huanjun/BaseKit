package com.mp.basekit.ext

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.mp.basekit.R
import com.mp.basekit.core.MPApplication
import com.mp.basekit.util.AppUtils
import com.mp.basekit.util.FileUtils
import com.mp.basekit.util.ImageUtils
import com.mp.basekit.util.toast.ToastUtils
import com.mp.basekit.view.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 * 描述　:项目中自定义类的拓展函数
 */

//绑定普通的Recyclerview
fun RecyclerView.init(
    layoutManger: RecyclerView.LayoutManager,
    bindAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): RecyclerView {
    layoutManager = layoutManger
    setHasFixedSize(true)
    adapter = bindAdapter
    isNestedScrollingEnabled = isScroll
    return this
}

private fun getLoadColorStateList(context: Context): ColorStateList {
    val colors = intArrayOf(ContextCompat.getColor(context, R.color.mp_color_refresh))
    val states = arrayOfNulls<IntArray>(1)
    states[0] = intArrayOf()
    return ColorStateList(states, colors)
}

//初始化 SwipeRefreshLayout
fun SwipeRefreshLayout.init(color: Int = R.color.mp_color_refresh, onRefreshListener: () -> Unit) {
    this.run {
        setOnRefreshListener {
            onRefreshListener.invoke()
        }
        //设置主题颜色
        setColorSchemeColors(ContextCompat.getColor(context, color))
    }
}

//设置适配器的列表动画
fun BaseQuickAdapter<*, *>.setAdapterAnim(mode: Int) {
    //等于0，关闭列表动画 否则开启
    if (mode == 0) {
        this.animationEnable = false
    } else {
        this.animationEnable = true
        this.setAnimationWithDefault(BaseQuickAdapter.AnimationType.values()[mode - 1])
    }
}

/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */
var lastClickTime = 0L
fun View.clickNoRepeatLogin(interval: Long = 500, action: (view: View) -> Unit) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
    }
}

/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param view 触发的view集合
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */
fun clickNoRepeatLogin(vararg view: View?, interval: Long = 500, action: (view: View) -> Unit) {
    view.forEach { view1 ->
        view1?.setOnClickListener { view2 ->
            val currentTime = System.currentTimeMillis()
            if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
                return@setOnClickListener
            }
            lastClickTime = currentTime
        }
    }
}

fun View.saveViewAsBitmap() {
    try {
        val composePosterImg = ImageUtils.compressBitmap(ImageUtils.view2Bitmap(this), 400)
        if (composePosterImg != null) {
            FileUtils.saveSignImage(
                AppUtils.getAppName(MPApplication.getAppContext()),
                System.currentTimeMillis().toString() + ".jpg",
                composePosterImg
            )
            ToastUtils.showSuc(context.getString(R.string.pic_save_succ))

        } else {
            ToastUtils.show(context.getString(R.string.pic_save_fail))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        ToastUtils.show(context.getString(R.string.pic_save_fail))
    }
}

fun TextView.setDigitTypeface() {
    //将字体文件保存在assets/fonts/目录下，创建Typeface对象
    val typeface = Typeface.createFromAsset(context.assets, "digit.TTF")
    //使用字体成仿宋体
    setTypeface(typeface)
}


fun SimplePagerTitleView.setTextColorByAttr(attrId: Int) {
    val colorStateList: ColorStateList?
    val theme: Theme = context.theme
    val typedValue = TypedValue()
    try {
        theme.resolveAttribute(attrId, typedValue, true)
        colorStateList = ResourcesCompat.getColorStateList(
            context.resources,
            typedValue.resourceId,
            context.theme
        )
        setTextColor(colorStateList)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun TextView.setTextGradient(startColor: Int, endColor: Int) {
    post {
        val mLinearGradient =
            LinearGradient(
                0f,
                0f,
                width.toFloat(),
                0f,
                startColor, endColor,
                Shader.TileMode.CLAMP
            )
        paint.shader = mLinearGradient
        invalidate()
    }
}
