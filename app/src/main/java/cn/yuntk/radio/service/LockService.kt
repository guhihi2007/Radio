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
    private var hasJump = false
    private lateinit var myBinder: MyLockServiceBinder

    var lockStatus = Constants.LOCK_SCREEN_ON
    override fun onBind(intent: Intent?): IBinder = myBinder
    override fun onCreate() {
        myBinder = MyLockServiceBinder(this)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                log("LockService onReceive ==${intent?.action}")

//                if (lockStatus == Constants.LOCK_SCREEN_OFF) {
//                    stopSelf()
//                    return
//                }
                if (!getHasJump())
                    if (intent?.action == Intent.ACTION_SCREEN_OFF && PlayServiceManager.isListenerFMBean()) {
                        log(" jumpActivity LockScreenActivity")
                        hasJump = true
                        jumpActivity(LockScreenActivity::class.java)
                    }
            }
        }

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        log(" LockScreenActivity onDestroy")

        unregisterReceiver(receiver)
    }

    fun setJump(boolean: Boolean) {
        hasJump = boolean
    }

    private fun getHasJump(): Boolean {
        return hasJump
    }

}