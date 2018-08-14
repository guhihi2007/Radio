package cn.yuntk.radio.manager

import android.app.Activity
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.utils.toast
import com.iflytek.autoupdate.IFlytekUpdate
import com.iflytek.autoupdate.UpdateConstants
import com.iflytek.autoupdate.UpdateErrorCode
import com.iflytek.autoupdate.UpdateType

/**
 * Author : Gupingping
 * Date : 2018/8/7
 * Mail : gu12pp@163.com
 * 讯飞版本升级SDK接入注意：
 * 1、jar导入
 * 2、AndroidManifest添加相应的配置，activity和appkey
 * 3、添加dialog布局文件 autoupdate_dialog_layout
 */
class UpdateManager {

    companion object {

        @JvmStatic
        fun check(context: Activity, isFromMain: Boolean) {
            val updManager = IFlytekUpdate.getInstance(context.application)
            updManager.setDebugMode(true)
            updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "false")
            updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "false")
            updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG)
            updManager.autoUpdate(context) { code, info ->
                if (code == UpdateErrorCode.OK && info != null) {
                    context.log("UpdateManager 请求更新info==" + "\n" +
                            "downloadUrl=${info.downloadUrl}" + "\n" +
                            "fileMd5=${info.fileMd5}" + "\n" +
                            "updateDetail=${info.updateDetail}" + "\n" +
                            "updateInfo=${info.updateInfo}" + "\n" +
                            "updateType=${info.updateType}" + "\n" +
                            "updateVersion=${info.updateVersion}" + "\n" +
                            "updateVersionCode=${info.updateVersionCode}")
                    if (info.updateType == UpdateType.NoNeed) {
                        if (!isFromMain)
                            context.toast("已经是最新版本")
                        return@autoUpdate
                    }
                    updManager.showUpdateInfo(context, info)
                } else {
                    context.log("UpdateManager 请求更新失败code==$code")
                }
            }

        }


    }


}