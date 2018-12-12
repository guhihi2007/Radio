package cn.yuntk.radio

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import cn.yuntk.radio.ibook.base.ActivityManager
import cn.yuntk.radio.ibook.service.Actions
import cn.yuntk.radio.ibook.service.FloatViewService
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil
import cn.yuntk.radio.ui.activity.SplashActivityAD
import cn.yuntk.radio.utils.*
import cn.yuntk.radio.view.widget.ExitDialog

class ReadActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
        if (XApplication.sInstance.isBackGround) {
            XApplication.sInstance.isBackGround = false
            LogUtils.e("BaseActivity 后台返回")
            if (SPUtil.getInstance().getBoolean(Constants.AD_SPLASH_STATUS) && needSplashAD()) {
                val intent = Intent(activity, SplashActivityAD::class.java)
                activity?.startActivity(intent)
            } else {
                LogUtils.e("BaseActivity 后台返回 广告开关==" + SPUtil.getInstance().getBoolean(Constants.AD_SPLASH_STATUS) + "" +
                        ",时间到了没有==" + needSplashAD())
            }
        }
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {
        if (!DeviceUtils.isForeground(activity) && !ExitDialog.is_exit_app) {
            XApplication.sInstance.isBackGround = true
            FloatViewService.startCommand(activity, Actions.SERVICE_GONE_WINDOW)
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        ActivityManager.getInstance().currentActivity = activity

    }

    fun needSplashAD(): Boolean {
        val current = System.currentTimeMillis()
        val background = SharedPreferencesUtil.getInstance().getLong(Constants.AD_APP_BACKGROUND_TIME, 0)
        val gapTime = (current - background) / 1000
        val serverTime = SharedPreferencesUtil.getInstance().getLong(Constants.AD_SPREAD_PERIOD, 5)
        return gapTime >= serverTime && serverTime != 0L && NetworkUtils.isConnected(XApplication.getInstance())
    }
}