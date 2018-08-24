package cn.yuntk.radio.ibook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import cn.yuntk.radio.ibook.service.AudioPlayer;
import cn.yuntk.radio.ibook.util.LogUtils;

public class MediaButtonReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "MediaButtonReceiver1";
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        try {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
                LogUtils.showLog(LOG_TAG+"ACTION_MEDIA_BUTTON!");
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
                if (keyEvent == null){
                    LogUtils.showLog("keyEvent == null");
                    return;
                }
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_HEADSETHOOK:
//                    AudioPlayer.get().playPause();
                        LogUtils.showLog(LOG_TAG+"KEYCODE_HEADSETHOOK!");
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        AudioPlayer.get().playPause();
                        LogUtils.showLog(LOG_TAG+"PLAY_PAUSE!");
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        AudioPlayer.get().playPause();
                        LogUtils.showLog(LOG_TAG+"KEYCODE_MEDIA_PLAY!");
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        AudioPlayer.get().playPause();
                        LogUtils.showLog(LOG_TAG+"KEYCODE_MEDIA_PAUSE!");
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        AudioPlayer.get().stopPlayer();
                        LogUtils.showLog(LOG_TAG+"STOP!");
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        AudioPlayer.get().next();
                        LogUtils.showLog(LOG_TAG+"NEXT!");
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        AudioPlayer.get().prev();
                        LogUtils.showLog(LOG_TAG+"PREVIOUS!");
                        break;
                }
            }
        }catch (Exception e){

        }
    }

}