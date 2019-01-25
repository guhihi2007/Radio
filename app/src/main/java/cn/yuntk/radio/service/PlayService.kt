package cn.yuntk.radio.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import cn.yuntk.radio.manager.AudioFocusManagerJava
import cn.yuntk.radio.play.*
import cn.yuntk.radio.utils.log
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
class PlayService : Service() {

    private val mHandler = Handler()
    private val mListenerList = CopyOnWriteArrayList<OnPlayerEventListener>()
    private lateinit var myBinder: MyPlayServiceBinder
    private lateinit var mAudioFocusManager: AudioFocusManagerJava

    override fun onCreate() {
        super.onCreate()
        log("PlayService onCreate")
        myBinder = MyPlayServiceBinder(this)
        mAudioFocusManager = AudioFocusManagerJava(this)
        PlayManager.instance.init(mAudioFocusManager, this)
        QuitTimer.init(this, mHandler, object : EventCallback<Long> {
            override fun onEvent(t: Long) {
                for (mListener in mListenerList) {
                    mListener.onTimer(t)
                }
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY

    }

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

}