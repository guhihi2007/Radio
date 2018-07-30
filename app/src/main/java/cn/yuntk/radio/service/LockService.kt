package cn.yuntk.radio.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import cn.yuntk.radio.Constants
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.ui.activity.LockScreenActivity
import cn.yuntk.radio.utils.jumpActivity
import cn.yuntk.radio.utils.log

/**
 * Author : Gupingping
 * Date : 2018/7/27
 * Mail : gu12pp@163.com
 */
class LockService : Service() {

    private lateinit var receiver: BroadcastReceiver
    var lockStatus = Constants.LOCK_SCREEN_ON
    override fun onBind(intent: Intent?): IBinder = throw UnsupportedOperationException("Not yet implemented")

    override fun onCreate() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (lockStatus == Constants.LOCK_SCREEN_OFF) {
                    stopSelf()
                    return
                }

                if (intent?.action == Intent.ACTION_SCREEN_OFF && PlayServiceManager.isListenerFMBean()) {
                    log("ACTION_SCREEN_OFF jumpActivity LockScreenActivity")
                    jumpActivity(LockScreenActivity::class.java)
                }
            }
        }

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}