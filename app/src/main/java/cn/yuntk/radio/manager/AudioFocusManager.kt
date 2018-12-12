//package cn.yuntk.radio.manager
//
//import android.content.Context
//import android.content.Context.AUDIO_SERVICE
//import android.media.AudioManager
//import cn.yuntk.radio.play.PlayManager
//import cn.yuntk.radio.utils.LogUtils
//
///**
// * Author : Gupingping
// * Date : 2018/7/16
// * Mail : gu12pp@163.com
// */
//class AudioFocusManager(context: Context) : AudioManager.OnAudioFocusChangeListener {
//
//    private var mAudioManager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
//    private var isPausedByFocusLossTransient: Boolean = false
//    private var mVolumeWhenFocusLossTransientCanDuck: Int = 0
//
//    fun requestAudioFocus(): Boolean {
//        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
//    }
//
//    fun abandonAudioFocus() {
//        mAudioManager.abandonAudioFocus(this)
//    }
//
//    override fun onAudioFocusChange(focusChange: Int) {
//        LogUtils.e("onAudioFocusChange focusChange==$focusChange")
//        val volume: Int
//        when (focusChange) {
//            // 重新获得焦点
//            AudioManager.AUDIOFOCUS_GAIN -> {
//                if (!willPlay() && isPausedByFocusLossTransient) {
//                    // 通话结束，恢复播放
////                    mPlayService.playPause()
//                    PlayManager.instance.playPause()
//                }
//
//                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
//                if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck / 2) {
//                    // 恢复音量
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck,
//                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
//                }
//
//                isPausedByFocusLossTransient = false
//                mVolumeWhenFocusLossTransientCanDuck = 0
//            }
//            // 永久丢失焦点，如被其他播放器抢占
//            AudioManager.AUDIOFOCUS_LOSS -> if (willPlay()) {
//                forceStop()
//                LogUtils.e("永久丢失焦点，如被其他播放器抢占")
//            }
//            // 短暂丢失焦点，如来电
//            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (willPlay()) {
//                forceStop()
//                isPausedByFocusLossTransient = true
//            }
//            // 瞬间丢失焦点，如通知
//            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
//                // 音量减小为一半
//                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
//                if (willPlay() && volume > 0) {
//                    mVolumeWhenFocusLossTransientCanDuck = volume
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck / 2,
//                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
//                }
//            }
//        }
//    }
//
//    private fun willPlay(): Boolean {
//        return PlayManager.instance.isPreparing() || PlayManager.instance.isPlaying()
//    }
//
//    private fun forceStop() {
//        if (PlayManager.instance.isPreparing()) {
//            PlayManager.instance.stop()
//        } else if (PlayManager.instance.isPlaying()) {
//            PlayManager.instance.pause()
//        }
//    }
//
//}