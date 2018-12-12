package cn.yuntk.radio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.yuntk.radio.play.PlayManager;
import cn.yuntk.radio.service.PlayService;
import cn.yuntk.radio.utils.LogUtils;

import static cn.yuntk.radio.Constants.ACTION_MEDIA_PLAY_PAUSE;
import static cn.yuntk.radio.Constants.ACTION_MEDIA_STOP;


/**
 * 状态栏控制
 */

public class StatusBarReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "radio.STATUS_BAR_ACTIONS";
    public static final String EXTRA = "extra";
    public static final String EXTRA_NEXT = "next";
    public static final String EXTRA_PLAY_PAUSE = "play_pause";
    public static final String EXTRA_PRE = "pre";
    public static final String EXTRA_STOP = "stop";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        String extra = intent.getStringExtra(EXTRA);
//        if (TextUtils.equals(extra, EXTRA_NEXT)) {
////            LogUtils.e("StatusBarReceiver next");
////            PlayService.startCommand(context, ACTION_MEDIA_NEXT);
//        }else
        if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)) {
            LogUtils.e("StatusBarReceiver play_pause");
            PlayManager.Companion.getInstance().playPause();
//            PlayService.startCommand(context, ACTION_MEDIA_PLAY_PAUSE);
//        }else if (TextUtils.equals(extra, EXTRA_PRE)) {
//            LogUtils.e("StatusBarReceiver pre");
//            PlayService.startCommand(context, ACTION_MEDIA_PRE);
        }
        else if (TextUtils.equals(extra, EXTRA_STOP)) {
            LogUtils.e("StatusBarReceiver stop");
//            PlayService.startCommand(context, ACTION_MEDIA_STOP);
            PlayManager.Companion.getInstance().quit();
        }

    }
}
