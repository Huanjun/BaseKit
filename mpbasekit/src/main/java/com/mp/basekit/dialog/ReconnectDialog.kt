package com.mp.basekit.dialog

import android.app.Activity
import com.mp.basekit.R
import com.zyao89.view.zloading.Z_TYPE
import kotlinx.android.synthetic.main.dialog_reconnect.*

class ReconnectDialog(activity: Activity) : MPDialog(activity) {

    override fun initLayout(): Int {
        return R.layout.dialog_reconnect
    }

    override fun initEvent() {
        z_loading.setLoadingBuilder(Z_TYPE.ROTATE_CIRCLE)
    }

    public fun setLoadText(text: String) {
        loadText?.text = text
    }
}