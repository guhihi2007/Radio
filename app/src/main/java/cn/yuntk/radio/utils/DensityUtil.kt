package cn.yuntk.radio.utils

import android.content.Context
import android.util.TypedValue

/**
 * Author : Gupingping
 * Date : 2018/7/23
 * Mail : gu12pp@163.com
 */
class DensityUtil {
    companion object {
        /**
         * dp转px
         *
         * @param context
         * @param dpVal
         * @return
         */
        @JvmStatic
        fun dp2px(context: Context, dpVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                    context.resources.displayMetrics).toInt()
        }
    }
}