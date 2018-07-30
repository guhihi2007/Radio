package cn.yuntk.radio.play

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.yuntk.radio.service.PlayService

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
class NoisyAudioStreamReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PlayService.startCommand(context, Actions.ACTION_MEDIA_PLAY_PAUSE)
    }
}