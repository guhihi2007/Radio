package cn.yuntk.radio.play

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.yuntk.radio.service.PlayService
import cn.yuntk.radio.Constants.ACTION_MEDIA_PLAY_PAUSE

/**
 *  来电/耳机拔出时暂停播放
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
class NoisyAudioStreamReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PlayService.startCommand(context, ACTION_MEDIA_PLAY_PAUSE)
    }
}