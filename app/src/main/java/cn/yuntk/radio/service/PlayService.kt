package cn.yuntk.radio.service

import android.app.Activity
import android.app.Service
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import cn.yuntk.radio.Constants
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.manager.AudioFocusManager
import cn.yuntk.radio.manager.MediaSessionManager
import cn.yuntk.radio.play.*
import cn.yuntk.radio.utils.*
import kotlinx.coroutines.experimental.*
import java.util.concurrent.CopyOnWriteArrayList
import cn.yuntk.radio.Constants.STATE_IDLE
import cn.yuntk.radio.Constants.STATE_PREPARING
import cn.yuntk.radio.Constants.STATE_PLAYING
import cn.yuntk.radio.Constants.STATE_PAUSE
import cn.yuntk.radio.viewmodel.CollectionViewModel
import cn.yuntk.radio.viewmodel.HistoryViewMode
import cn.yuntk.radio.viewmodel.Injection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
class PlayService : Service() {

    private val TAG = "Service"
    private val TIME_UPDATE = 300L

    private val mNoisyReceiver = NoisyAudioStreamReceiver()
    private val mNoisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val mHandler = Handler()
    //主播放器
    private lateinit var mPlayer: io.vov.vitamio.MediaPlayer
    private var mAudioFocusManager: AudioFocusManager? = null
    private var mMediaSessionManager: MediaSessionManager? = null
    private val mListenerList = CopyOnWriteArrayList<OnPlayerEventListener>()
    private var mPlayState = STATE_IDLE
    private lateinit var myBinder: MyBinder
    private val disposable = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        "PlayService onCreate=${javaClass.simpleName}".logE(LT.RadioNet)
        myBinder = MyBinder(this)
        mAudioFocusManager = AudioFocusManager(this)
        mMediaSessionManager = MediaSessionManager(this)
        mPlayer = io.vov.vitamio.MediaPlayer(this, true)
        registerReceiver(mNoisyReceiver, mNoisyFilter)
//        MusicNotification.init(this)
        QuitTimer.init(this, mHandler, object : EventCallback<Long> {
            override fun onEvent(t: Long) {
                for (mListener in mListenerList) {
                    mListener.onTimer(t)
                }
            }
        })
    }

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    companion object {
        @JvmStatic
        fun startCommand(context: Context, action: String) {
            val intent = Intent(context, PlayService::class.java)
            intent.action = action
            context.startService(intent)
        }
    }

    private fun sendNotification(intent: Intent) {
//        var notification: PushNotification? = null
//        val bundle = intent.extras
//        val bean = bundle!!.getSerializable(PUSH_DATA) as PushBean
//        if (bean != null) {
//            notification = PushNotification(this, bean)
//            notification!!.setJumpActivity(MusicSearchActivity::class.java)
//            //            notification.setHangActivity(MusicSearchActivity.class);
//            //            pushManager.sendNotification(notification);
//        }
    }


    private var currentFMBean: FMBean? = null

    fun play(fmBean: FMBean, context: Activity) {
        currentFMBean = fmBean
        saveFMBean(fmBean, context)
        mPlayer.reset()
        mPlayer.setDataSource(fmBean.radioUrl)
        mPlayer.prepareAsync()
        mPlayState = STATE_PREPARING
        mPlayer.setOnBufferingUpdateListener { mp, percent ->
            log("setOnBufferingUpdateListener percent=$percent")

        }
        mPlayer.setOnCompletionListener {
            log("setOnCompletionListener")
            postEvent(ListenEvent(STATE_IDLE))
        }
        mPlayer.setOnPreparedListener {
            log("setOnPreparedListener")
            mPlayer.start()
            mPlayState = STATE_PLAYING
            postEvent(ListenEvent(STATE_PLAYING, fmBean))
        }
        context.volumeControlStream = AudioManager.STREAM_MUSIC
    }

    private fun saveFMBean(fmBean: FMBean, context: Activity) {
        SPUtil.getInstance().putObject(Constants.LAST_PLAY, fmBean)//记录上次播放
        //收听记录存库
        val viewModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(context.application)
                .create(HistoryViewMode::class.java)

        disposable.add(viewModel.saveHistory(fmBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log("saveFMBean to History Success")
                })

    }

    fun playPause() {
        "playOrPause STATE==$mPlayState".logE(LT.RadioNet)
        when (mPlayState) {
            STATE_PREPARING -> stop()
            STATE_PLAYING -> pause()
            STATE_PAUSE -> start()
            STATE_IDLE -> {
                if (currentFMBean != null)
                    play(currentFMBean!!, applicationContext as Activity)
            }
        }
    }

    fun start() {
        if (!isPreparing() && !isPausing()) {
            return
        }
        mPlayer.start()
        mPlayState = STATE_PLAYING
        postEvent(ListenEvent(STATE_PLAYING))

    }

    fun pause() {
        if (!isPlaying()) {
            return
        }
        mPlayer.pause()
        mPlayState = STATE_PAUSE
        postEvent(ListenEvent(STATE_PAUSE))

    }

    fun stop() {
        if (isIdle()) {
            return
        }
        mPlayer.reset()
        mPlayState = STATE_IDLE
        postEvent(ListenEvent(STATE_IDLE))

    }

    fun next() {
    }


    fun isPlaying(): Boolean {
        return mPlayState == STATE_PLAYING
    }

    private fun isPausing(): Boolean {
        return mPlayState == STATE_PAUSE
    }

    fun isPreparing(): Boolean {
        return mPlayState == STATE_PREPARING
    }

    private fun isIdle(): Boolean {
        return mPlayState == STATE_IDLE
    }


    private val mPublishRunnable = object : Runnable {
        override fun run() {
            if (isPlaying()) {
                for (mListener in mListenerList) {
//                    mListener.onPublish(mPlayer!!.currentPosition)
                }
            }
            mHandler.postDelayed(this, TIME_UPDATE)
        }
    }

    override fun onDestroy() {
        mPlayer.reset()
        mPlayer.release()
        if (mAudioFocusManager != null) {
            mAudioFocusManager!!.abandonAudioFocus()
        }

        if (mMediaSessionManager != null) {
            mMediaSessionManager!!.release()
        }
        unregisterReceiver(mNoisyReceiver)
        disposable.clear()
        //        unregisterReceiver(mNotificationReceiver);
//        MusicNotification.cancelAll()
        super.onDestroy()
        Log.i(TAG, "PlayService: " + javaClass.simpleName)
    }

    fun quit() {
        stop()
        QuitTimer.stop()
        stopSelf()
    }

    fun getCurrentFMBean(): FMBean? {
        return currentFMBean
    }

    fun getListenerState(): Int {
        return mPlayState
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Actions.ACTION_PLAY_START) {
                playPause()
            } else if (action == Actions.ACTION_PLAY_NEXT) {
                next()
            }
        }
    }

}