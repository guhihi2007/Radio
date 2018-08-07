package cn.yuntk.radio.manager

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.HistoryFMBean
import cn.yuntk.radio.service.MyBinder
import cn.yuntk.radio.service.PlayService
import cn.yuntk.radio.utils.LT
import cn.yuntk.radio.utils.logE
import cn.yuntk.radio.viewmodel.Injection

/**
 * Author : Gupingping
 * Date : 2018/7/17
 * Mail : gu12pp@163.com
 */
object PlayServiceManager {

    private var playService: PlayService? = null
    private var conn: ServiceConnection

    init {
        conn = PlayServiceConnection()
    }

    fun init(activity: Activity) {
        start(activity)
        bind(activity)
    }

   private fun start(activity: Activity) {
        val intent = Intent()
        intent.setClass(activity, PlayService::class.java)
        activity.startService(intent)
    }

    //绑定service
    private  fun bind(activity: Activity) {
        val intent = Intent()
        intent.setClass(activity, PlayService::class.java)
        val isBind = activity.bindService(intent, conn, Context.BIND_AUTO_CREATE)
        "bind isBind=$isBind".logE(LT.RadioNet)
    }

    //解绑service
    fun unbind(activity: Activity) {
        val isBind = activity.unbindService(conn)
        "bind isBind=$isBind".logE(LT.RadioNet)
    }

    fun play(fmBean: FMBean, context: Activity) {
        playService?.play(fmBean, context)
    }

    fun pauseContinue() {
        playService?.playPause()
    }

    fun getListenerFMBean(): FMBean? {
        return playService?.getCurrentFMBean()
    }

    fun getListenerState(): Int {
        return playService?.getListenerState() ?: 0
    }

    fun isListenerFMBean(): Boolean {
        return playService?.isPlaying() ?: false
    }

    class PlayServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            "PlayService onServiceDisconnected".logE(LT.RadioNet)
        }

        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder) {
            val myBinder = iBinder as MyBinder
            val playService: PlayService = myBinder.mService
            PlayServiceManager.playService = playService
            "PlayService onServiceConnected playService==$playService".logE(LT.RadioNet)
        }

    }

}