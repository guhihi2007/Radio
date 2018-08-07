package cn.yuntk.radio.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

/**
 * Author : Gupingping
 * Date : 2018/8/5
 * Mail : gu12pp@163.com
 */
class BindingUtils {
    companion object {
        @JvmStatic
        @BindingAdapter("bind:image")
        fun setBackground(@org.jetbrains.annotations.NotNull()
                          imageView: ImageView, resId: Int) {
            if (resId == -1)
                imageView.visibility = View.GONE
            else
                imageView.setImageResource(resId)
        }
    }
}