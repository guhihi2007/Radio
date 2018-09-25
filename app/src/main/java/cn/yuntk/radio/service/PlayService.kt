package cn.yuntk.radio.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.arch.lifecycle.ViewModelProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.ObservableArrayList
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import cn.yuntk.radio.Constants
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.manager.AudioFocusManager
import cn.yuntk.radio.play.*
import cn.yuntk.radio.utils.*
import kotlinx.coroutines.experimental.*
import java.util.concurrent.CopyOnWriteArrayList
import cn.yuntk.radio.Constants.STATE_IDLE
import cn.yuntk.radio.Constants.STATE_PREPARING
import cn.yuntk.radio.Constants.STATE_PLAYING
import cn.yuntk.radio.Constants.STATE_PAUSE
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.viewmodel.*
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
    private val RETRY = 999 //播放完成重试

    private val mNoisyReceiver = NoisyAudioStreamReceiver()
    private val mNoisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val mHandler = Handler()
    private lateinit var mPlayer: io.vov.vitamio.MediaPlayer
    private var mAudioFocusManager: AudioFocusManager? = null
    private val mListenerList = CopyOnWriteArrayList<OnPlayerEventListener>()
    private var mPlayState = STATE_IDLE
    private lateinit var myBinder: MyPlayServiceBinder
    private val disposable = CompositeDisposable()
    private var list = ObservableArrayList<FMBean>()
    private var currentIndex = -1
    private var lastPage = ""
    override fun onCreate() {
        super.onCreate()
        "PlayService onCreate=${javaClass.simpleName}".logE(LT.RadioNet)
        myBinder = MyPlayServiceBinder(this)
        mAudioFocusManager = AudioFocusManager(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            MediaSessionManager.get().init(this)
        } else {
            log("SDK_INT==${Build.VERSION.SDK_INT} < 21")
        }
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

    private fun queryPageList() {
        //查库，当前页面的所有FMBean-------start
        val page = SPUtil.getInstance().getString(Constants.CURRENT_PAGE)
        if (lastPage != page) {
            lastPage = page
            if (page != null) {
                val pageViewModel = PageViewModel(Injection.getPageDao())
                disposable.add(pageViewModel.getListByPage(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            log("PlayService queryPageList 异步查库getList==${it.size}")
                            list.addAll(it.map { it.fmBean })
                        })
            }
        }
        //查库，当前页面的所有FMBean-------end
    }

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    companion object {
        @JvmStatic
        fun startCommand(context: Context, action: String) {
            if (action == Actions.ACTION_MEDIA_PLAY_PAUSE) {
                PlayServiceManager.pauseContinue()
            }
        }
    }

    private var currentFMBean: FMBean? = null

    fun play(fmBean: FMBean, context: Activity?) {
        //TODO 检查网络
        if (!NetworkUtils.isAvailable(this)) {
            application.toast("网络异常")
            return
        }
        //自动结束了 延迟播放handler
        val handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                if (msg?.what == RETRY) {
                    play(fmBean, context)
                }
            }
        }

        currentFMBean = fmBean
        SPUtil.getInstance().putObject(Constants.LAST_PLAY, fmBean)//记录上次播放
        //保存所有收听记录
        if (context != null) {
            saveFMBean(fmBean, context)
            context.volumeControlStream = AudioManager.STREAM_MUSIC
        }
        queryPageList()
        mPlayer.reset()
        mPlayer.setDataSource(fmBean.radioUrl)
        mPlayer.prepareAsync()
        mPlayState = STATE_PREPARING
        mPlayer.setOnBufferingUpdateListener { mp, percent ->
            //            log("setOnBufferingUpdateListener percent=$percent")
        }
        mPlayer.setOnCompletionListener {
            log("setOnCompletionListener")
            postEvent(ListenEvent(STATE_IDLE))
            //自动结束了要重新播放
            handler.postDelayed(
                    {
                        handler.sendEmptyMessage(RETRY)
                        log("OnCompletion postDelayed 延迟播放")
                    }, 2 * 1000
            )
        }
        mPlayer.setOnPreparedListener {
            log("setOnPreparedListener")
            mPlayer.start()
            mPlayState = STATE_PLAYING
            postEvent(ListenEvent(STATE_PLAYING, fmBean))
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            MediaSessionManager.get().updateMetaData(fmBean)
            MediaSessionManager.get().updatePlaybackState(mPlayState)
        }
        currentIndex = list.indexOf(fmBean)

    }

    fun play(position: Int, context: Activity?): FMBean? {
        if (list.isEmpty()) return null
        var mPlayingPosition = position
        if (position < 0) {
            mPlayingPosition = list.size - 1
        } else if (position >= list.size) {
            mPlayingPosition = 0
        }
        currentIndex = mPlayingPosition
        play(list[mPlayingPosition], context)
        return list[mPlayingPosition]
    }

    private fun saveFMBean(fmBean: FMBean, context: Activity) {
        //收听记录存库
        val viewModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(context.application)
                .create(HistoryViewMode::class.java)

        disposable.add(viewModel.saveHistory(fmBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log("saveFMBean to History Success==${fmBean.name}")
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
                    play(currentFMBean!!, null)
            }
        }
    }

    fun start() {
        if (!isPreparing() && !isPausing()) {
            return
        }
        if (mAudioFocusManager?.requestAudioFocus() == true) {
            mPlayer.start()
            mPlayState = STATE_PLAYING
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                MediaSessionManager.get().updatePlaybackState(mPlayState)
            }
            postEvent(ListenEvent(STATE_PLAYING))
        }


    }

    fun pause() {
        if (!isPlaying()) {
            return
        }
        mPlayer.pause()
        mPlayState = STATE_PAUSE
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            MediaSessionManager.get().updatePlaybackState(mPlayState)
        }
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

    fun next(activity: Activity?): FMBean? {
        return play(currentIndex + 1, activity)
    }

    fun pre(activity: Activity?): FMBean? {
        return play(currentIndex - 1, activity)
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

    fun isIdle(): Boolean {
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

    fun getPageList(): List<FMBean> {
        return list
    }

    fun getCurrentIndex(): Long {
        return currentIndex.toLong()
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Actions.ACTION_PLAY_START) {
                playPause()
            } else if (action == Actions.ACTION_PLAY_NEXT) {
                next(null)
            }
        }
    }

}