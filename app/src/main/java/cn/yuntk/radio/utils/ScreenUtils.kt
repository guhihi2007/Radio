package cn.yuntk.radio.utils

import android.content.Context


/**
 * Author : Gupingping
 * Date : 2018/7/26
 * Mail : gu12pp@163.com
 */
class ScreenUtils {
    companion object {
        @JvmStatic
        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }
        @JvmStatic
        fun getScreenWidth(context: Context): Int {
            var screenWith = -1
            try {
                screenWith = context.resources.displayMetrics.widthPixels
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return screenWith
        }
        @JvmStatic
        fun getScreenHeight(context: Context): Int {
            var screenHeight = -1
            try {
                screenHeight = context.resources.displayMetrics.heightPixels
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return screenHeight
        }
    }
}