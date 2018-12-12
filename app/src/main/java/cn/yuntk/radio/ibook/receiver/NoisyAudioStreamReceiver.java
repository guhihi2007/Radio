package cn.yuntk.radio.ibook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import cn.yuntk.radio.ibook.service.AudioPlayer;

/**
 * 来电/耳机拔出时暂停播放
 * Created by wcy on 2016/1/23.
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AudioPlayer.get().playPause();
    }
}
