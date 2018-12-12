package cn.yuntk.radio.play

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.yuntk.radio.Constants
import cn.yuntk.radio.utils.LogUtils

/**
 *  来电/耳机拔出时暂停播放
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
class NoisyAudioStreamReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
//        PlayService.startCommand(context, ACTION_MEDIA_PLAY_PAUSE)
        if (intent.action == Constants.HEADSET_PLUG) {
            if (intent.hasExtra("state")) {
                when (intent.getIntExtra("state", 0)) {
                    0 -> {
                        LogUtils.e("headset not connected")
                        PlayManager.instance.playPause()
                    }
                    1 -> {
                        if (!PlayManager.instance.isPlaying()) {
                            PlayManager.instance.playPause()
                            LogUtils.e(" headset  connected")
                        }
                    }
                }
            }
        }
    }
}