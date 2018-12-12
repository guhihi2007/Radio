package cn.yuntk.radio.manager

import android.app.Activity
import android.content.Intent
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.ibook.service.Actions
import cn.yuntk.radio.ibook.service.AudioPlayer
import cn.yuntk.radio.ibook.service.FloatViewService
import cn.yuntk.radio.play.PlayManager
import cn.yuntk.radio.service.PlayService
import cn.yuntk.radio.viewmodel.NeedReplaceURLContent

/**
 * Author : Gupingping
 * Date : 2018/7/17
 * Mail : gu12pp@163.com
 */
object PlayServiceManager {

    fun init(activity: Activity) {
        start(activity)
    }

    private fun start(activity: Activity) {
        val intent = Intent()
        intent.setClass(activity, PlayService::class.java)
        activity.startService(intent)
    }

    @JvmStatic

    fun play(fmBean: FMBean, context: Activity) {
        //收听频道前，如果小说不是空闲状态就停止
        if (!AudioPlayer.get().isIdle) {
            AudioPlayer.get().stopPlayer()
        }
        if (FloatViewService.isShow == "0")
            FloatViewService.startCommand(context, Actions.SERVICE_GONE_WINDOW)
        PlayManager.instance.handler?.removeMessages(PlayManager.instance.RETRY)
        if (NeedReplaceURLContent.replaceContent.containsKey(fmBean.radioId))//更换不能播放的url
            fmBean.radioUrl = NeedReplaceURLContent.replaceContent[fmBean.radioId]
        PlayManager.instance.play(fmBean, context)
    }

    fun stop() {
        PlayManager.instance.stop()
    }

    @JvmStatic
    fun pauseContinue() {
        //收听频道前，如果小说不是空闲状态就停止
        if (!AudioPlayer.get().isIdle) {
            AudioPlayer.get().stopPlayer()
        }
        PlayManager.instance.playPause()
    }

    @JvmStatic
    fun getListenerFMBean(): FMBean? {
        return PlayManager.instance.getCurrentFMBean()
    }

    @JvmStatic
    fun getListenerState(): Int {
        return PlayManager.instance.getListenerState() ?: 0
    }

    @JvmStatic
    fun isListenerFMBean(): Boolean {
        return PlayManager.instance.isPlaying() ?: false
    }

    fun isListenerIdle(): Boolean {
        return PlayManager.instance.isIdle() ?: false
    }

    fun getPageList(): List<FMBean> {
        return PlayManager.instance.getPageList() ?: ArrayList<FMBean>()
    }

    fun next(activity: Activity): FMBean? {
        return PlayManager.instance.next(activity)
    }

    fun pre(activity: Activity): FMBean? {
        return PlayManager.instance.pre(activity)
    }

}