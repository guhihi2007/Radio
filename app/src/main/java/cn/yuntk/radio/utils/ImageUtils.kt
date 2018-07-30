package cn.yuntk.radio.utils

import android.content.Context
import android.graphics.*
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat

/**
 * Author : Gupingping
 * Date : 2018/7/20
 * Mail : gu12pp@163.com
 */
class ImageUtils {
    companion object {

        /**
         * 将图片放大或缩小到指定尺寸
         */
        @JvmStatic
        fun resizeImage(source: Bitmap, dstWidth: Int, dstHeight: Int): Bitmap {
          return  Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true)
        }

        /**
         * 将图片剪裁为圆形
         */
        @JvmStatic
        fun createCircleImage(source: Bitmap): Bitmap {
            val length = Math.min(source.width, source.height)
            val paint = Paint()
            paint.isAntiAlias = true
            val target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(target)
            canvas.drawCircle((source.width / 2).toFloat(), (source.height / 2).toFloat(), (length / 2).toFloat(), paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(source, 0f, 0f, paint)
            return target
        }

        /**
         * 获取bitmap
         *
         * @param resId 资源id
         * @return bitmap
         */
        @JvmStatic
        fun getBitmap(context: Context,@DrawableRes resId: Int): Bitmap {
            val drawable = ContextCompat.getDrawable(context.applicationContext, resId)
            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            canvas.setBitmap(bitmap)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.draw(canvas)
            return bitmap
        }
    }
}