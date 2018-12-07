package cn.yuntk.radio.manager

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.ibook.service.Actions
import cn.yuntk.radio.ibook.service.AudioPlayer
import cn.yuntk.radio.ibook.service.FloatViewService
import cn.yuntk.radio.service.MyPlayServiceBinder
import cn.yuntk.radio.service.PlayService
import cn.yuntk.radio.utils.LT
import cn.yuntk.radio.utils.logE

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
    private fun bind(activity: Activity) {
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

    @JvmStatic

    fun play(fmBean: FMBean, context: Activity) {
        //收听频道前，如果小说不是空闲状态就停止
        if (!AudioPlayer.get().isIdle) {
            AudioPlayer.get().stopPlayer()
        }
        if (FloatViewService.isAddToWindow())
            FloatViewService.startCommand(context, Actions.SERVICE_GONE_WINDOW)
        playService?.handler?.removeMessages(playService!!.RETRY)
        if (NeedReplaceURLContent.replaceContent.containsKey(fmBean.radioId) )
            fmBean.radioUrl=NeedReplaceURLContent.replaceContent[fmBean.radioId]
        playService?.play(fmBean, context)
    }

    fun stop() {
        playService?.stop()
    }

    @JvmStatic
    fun pauseContinue() {
        //收听频道前，如果小说不是空闲状态就停止
        if (!AudioPlayer.get().isIdle) {
            AudioPlayer.get().stopPlayer()
        }
        playService?.playPause()
    }

    @JvmStatic
    fun getListenerFMBean(): FMBean? {
        return playService?.getCurrentFMBean()
    }

    @JvmStatic
    fun getListenerState(): Int {
        return playService?.getListenerState() ?: 0
    }

    @JvmStatic
    fun isListenerFMBean(): Boolean {
        return playService?.isPlaying() ?: false
    }

    fun isListenerIdle(): Boolean {
        return playService?.isIdle() ?: false
    }

    fun getPageList(): List<FMBean> {
        return playService?.getPageList() ?: ArrayList<FMBean>()
    }

    fun next(activity: Activity): FMBean? {
        return playService?.next(activity)
    }

    fun pre(activity: Activity): FMBean? {
        return playService?.pre(activity)
    }

    class PlayServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            "PlayService onServiceDisconnected".logE(LT.RadioNet)
        }

        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder) {
            val myBinder = iBinder as MyPlayServiceBinder
            val playService: PlayService = myBinder.mService
            PlayServiceManager.playService = playService
            "PlayService onServiceConnected playService==$playService".logE(LT.RadioNet)
        }

    }

}