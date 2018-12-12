package cn.yuntk.radio.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.telephony.TelephonyManager
import android.text.TextUtils

/**
 * Author : Gupingping
 * Date : 2018/9/28
 * QQ : 464955343
 */
class TelephonyUtils {
    companion object {

        /**
         * 获取手机Android API等级（22、23 ...）
         *
         * @return
         */
        @JvmStatic
        fun getBuildLevel(): Int {
            return Build.VERSION.SDK_INT
        }

        /**
         * 获取设备用户名称
         */
        @JvmStatic
        fun getPhoneUser(): String {
            return Build.USER
        }

        /***
         * 获取可用存储空间
         * 返回单位：MB
         *
         * @return
         */
        @JvmStatic
        fun getAvailableMemorySize(): Long {
            return if (externalMemoryAvailable()) {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSize.toLong()
                val availableBlocks = stat.availableBlocks.toLong()
                availableBlocks * blockSize / (1048 * 1024)
            } else {
                getAvailableInternalMemorySize()
            }
        }

        /**
         * SDCARD是否存
         */
        private fun externalMemoryAvailable(): Boolean {
            return android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
        }

        /**
         * 获取手机内部剩余存储空间
         *
         * @return
         */
        private fun getAvailableInternalMemorySize(): Long {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSize.toLong()
            val availableBlocks = stat.availableBlocks.toLong()
            return availableBlocks * blockSize / (1048 * 1024)
        }

        /***
         * 判断是否有SIM卡
         *
         * @return
         */
        @JvmStatic
        fun hasSIMCard(context: Context): Boolean {
            val mTelephonyManager = context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            val state = mTelephonyManager.simState
            return TelephonyManager.SIM_STATE_READY == state
        }

        /**
         * 获取手机品牌
         *
         * @return
         */
        @JvmStatic
        fun getPhoneBrand(): String {
            return Build.BRAND
        }

        /**
         * 判断当前应用程序处于前台还是后台
         */
        @JvmStatic
        fun isForeground(context: Activity): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            if (null != manager) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (null != manager.runningAppProcesses) {
                        val pis = manager.runningAppProcesses
                        for (app in pis) {
                            if (app.processName == context.packageName) {
                                return app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            }
                        }
//                        if (pis != null && !pis.isEmpty()) {
//                            val topAppProcess = pis[0]
//                            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                                for (activeProcess in topAppProcess.pkgList) {
//                                    if (activeProcess == context.packageName) {
//                                        return true
//                                    }
//                                }
//                            }
//                        }
                    }
                } else {
                    val localList = manager.getRunningTasks(1)
                    if (localList.size > 0) {
                        val taskInfo = localList[0] as ActivityManager.RunningTaskInfo?
                        if (taskInfo != null) {
                            val componentName = taskInfo.topActivity
                            val packageName = componentName.packageName
                            if (!TextUtils.isEmpty(packageName) && packageName == context.packageName) {
                                return true
                            }
                        }
                    }
                }
            }
            return false
        }
    }
}