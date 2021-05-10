package com.mahaoyuan.realtime.util

import android.app.Activity
import android.os.Build
import android.view.View



object FullScreen {

    fun hideNavigationBar(activity: Activity) {
        var uiFlags: Int = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //布局隐藏导航
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION )//隐藏导航
        //兼容性判断
        uiFlags = if (Build.VERSION.SDK_INT >= 19) {
            uiFlags or 0x00001000
        } else {
            uiFlags or View.SYSTEM_UI_FLAG_LOW_PROFILE
        }
        activity.window.decorView.systemUiVisibility = uiFlags
    }
}