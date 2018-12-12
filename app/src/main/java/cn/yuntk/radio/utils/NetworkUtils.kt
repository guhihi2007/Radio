package cn.yuntk.radio.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Author : Gupingping
 * Date : 2018/8/13
 * Mail : gu12pp@163.com
 */
class NetworkUtils {
    companion object {
        @JvmStatic
        fun isAvailable(context: Context): Boolean {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            return info != null && info.isAvailable
        }
        @JvmStatic
        private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }
        @JvmStatic
        fun isConnected(context: Context): Boolean {
            val info = getActiveNetworkInfo(context)
            return info != null && info.isConnected
        }
    }
}