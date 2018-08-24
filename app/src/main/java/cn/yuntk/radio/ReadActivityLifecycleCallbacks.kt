package cn.yuntk.radio

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.yuntk.radio.ibook.XApplication
import cn.yuntk.radio.ibook.ads.ADConstants.AD_APP_BACKGROUND_TIME
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil
import cn.yuntk.radio.utils.DeviceUtils
import cn.yuntk.radio.view.widget.ExitDialog

class ReadActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {
        if (!DeviceUtils.isForeground(activity) && !ExitDialog.is_exit_app) {
            SharedPreferencesUtil.getInstance().putLong(AD_APP_BACKGROUND_TIME, System.currentTimeMillis())//记录退到后台时间
            XApplication.sInstance.isBackGroud=true
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}