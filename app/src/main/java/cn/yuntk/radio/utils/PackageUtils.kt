package cn.yuntk.radio.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * Author : Gupingping
 * Date : 2018/9/28
 * QQ : 464955343
 */
class PackageUtils {
    companion object {
        /**
         * 获取app版本信息
         *
         * @param context 上下文
         * @return 当前版本信息
         */
        @JvmStatic
        fun getVersionName(context: Context): String {
            // 获取版本
            // 获取包的管理者
            var versionName = "未知版本"
            val manager = context.packageManager
            try {
                val packageInfo = manager.getPackageInfo(
                        context.packageName, 0)
                versionName = packageInfo.versionName
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            return versionName
        }

        /**
         * 获取app版本号
         *
         * @param context 上下文
         * @return 当前版本Code
         */
        @JvmStatic
        fun getVersionCode(context: Context): Int {
            // 获取版本
            // 获取包的管理者
            var VersionCode = 0
            val manager = context.packageManager
            try {
                val packageInfo = manager.getPackageInfo(
                        context.packageName, 0)
                VersionCode = packageInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return VersionCode
        }
    }
}