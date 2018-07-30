package cn.yuntk.radio.manager

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import cn.yuntk.radio.service.PlayService

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
class AudioFocusManager(playService: PlayService) : AudioManager.OnAudioFocusChangeListener {

    private var mPlayService: PlayService = playService
    private var mAudioManager: AudioManager = playService.getSystemService(AUDIO_SERVICE) as AudioManager
    private var isPausedByFocusLossTransient: Boolean = false
    private var mVolumeWhenFocusLossTransientCanDuck: Int = 0

    fun requestAudioFocus(): Boolean {
        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(this)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        val volume: Int
        when (focusChange) {
        // 重新获得焦点
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!willPlay() && isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    mPlayService.playPause()
                }

                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck / 2) {
                    // 恢复音量
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck,
                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                }

                isPausedByFocusLossTransient = false
                mVolumeWhenFocusLossTransientCanDuck = 0
            }
        // 永久丢失焦点，如被其他播放器抢占
            AudioManager.AUDIOFOCUS_LOSS -> if (willPlay()) {
                forceStop()
            }
        // 短暂丢失焦点，如来电
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (willPlay()) {
                forceStop()
                isPausedByFocusLossTransient = true
            }
        // 瞬间丢失焦点，如通知
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // 音量减小为一半
                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                if (willPlay() && volume > 0) {
                    mVolumeWhenFocusLossTransientCanDuck = volume
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck / 2,
                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                }
            }
        }
    }

    private fun willPlay(): Boolean {
        return mPlayService.isPreparing() || mPlayService.isPlaying()
    }

    private fun forceStop() {
        if (mPlayService.isPreparing()) {
            mPlayService.stop()
        } else if (mPlayService.isPlaying()) {
            mPlayService.pause()
        }
    }

}