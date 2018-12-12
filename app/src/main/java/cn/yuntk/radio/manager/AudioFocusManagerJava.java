package cn.yuntk.radio.manager;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import cn.yuntk.radio.play.PlayManager;
import cn.yuntk.radio.utils.LogUtils;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class AudioFocusManagerJava implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager audioManager;
    private boolean isPausedByFocusLossTransient;

    public AudioFocusManagerJava(Context context) {
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.e("Radio","onAudioFocusChange focusChange=="+focusChange);
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    PlayManager.Companion.getInstance().playPause();
                }

                // 恢复音量
                PlayManager.Companion.getInstance().getMediaPlayer().setVolume(1f, 1f);

                isPausedByFocusLossTransient = false;
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                PlayManager.Companion.getInstance().stop();
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                PlayManager.Companion.getInstance().playPause();
                isPausedByFocusLossTransient = true;
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                PlayManager.Companion.getInstance().getMediaPlayer().setVolume(0.5f, 0.5f);
                break;
        }
    }
}
