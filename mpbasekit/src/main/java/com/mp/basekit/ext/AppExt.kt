package com.mp.basekit.ext

import android.content.Context
import com.mp.basekit.core.MPApplication
import com.mp.basekit.util.DensityUtil
import com.mp.basekit.util.ScreenUtils
import java.math.BigDecimal

fun getAppContext(): Context {
    return MPApplication.getAppContext()
}

fun dp2px(dp: Float): Int {
    return DensityUtil.dip2px(dp)
}

fun getScreenW(): Int {
    return ScreenUtils.getScreenWidth(getAppContext())
}

fun getScreenH(): Int {
    return ScreenUtils.getScreenHeight(getAppContext())
}

fun BigDecimal.toNoZero(scale: Int): String {
    return setScale(scale, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString()
}