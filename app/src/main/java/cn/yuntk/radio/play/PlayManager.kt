package cn.yuntk.radio.play

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.IntentFilter
import android.databinding.ObservableArrayList
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import cn.yuntk.radio.Constants
import cn.yuntk.radio.Constants.HEADSET_PLUG
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.manager.AudioFocusManagerJava
import cn.yuntk.radio.notification.Notifier
import cn.yuntk.radio.receiver.StatusBarReceiver
import cn.yuntk.radio.service.MyPlayServiceBinder
import cn.yuntk.radio.service.PlayService
import cn.yuntk.radio.utils.*
import cn.yuntk.radio.viewmodel.HistoryViewMode
import cn.yuntk.radio.viewmodel.Injection
import cn.yuntk.radio.viewmodel.PageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Author : Gupingping
 * Date : 2018/12/7
 * QQ : 464955343
 */
class PlayManager private constructor() {

    val RETRY = 999 //播放完成重试
    private val mNoisyReceiver = NoisyAudioStreamReceiver()
    private val mNoisyFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private lateinit var mPlayer: io.vov.vitamio.MediaPlayer
    private lateinit var mAudioFocusManager: AudioFocusManagerJava
    private val mListenerList = CopyOnWriteArrayList<OnPlayerEventListener>()
    private var mPlayState = Constants.STATE_IDLE
    private lateinit var myBinder: MyPlayServiceBinder
    private lateinit var receiver: StatusBarReceiver

    private val disposable = CompositeDisposable()
    private var list = ObservableArrayList<FMBean>()
    private var currentIndex = -1
    private var lastPage = ""
    private var currentFMBean: FMBean? = null
    var handler: Handler? = null
    private var context: PlayService? = null

    private object PlayManagerHolder {
        @SuppressLint("StaticFieldLeak")
        val instance = PlayManager()
    }

    fun init(audioFocusManager: AudioFocusManagerJava, service: PlayService) {
        this.context = service
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            MediaSessionManager.get().init(service)
        } else {
            LogUtils.e("SDK_INT==${Build.VERSION.SDK_INT} < 21")
        }

        mAudioFocusManager = audioFocusManager
        mPlayer = io.vov.vitamio.MediaPlayer(context, true)
        mPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        mNoisyFilter.addAction(HEADSET_PLUG)//耳机拔插监听
        context!!.registerReceiver(mNoisyReceiver, mNoisyFilter)
        //注册状态栏监听
        receiver = StatusBarReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(StatusBarReceiver.ACTION_STATUS_BAR)
        context!!.registerReceiver(receiver, intentFilter)
        Notifier.get().init(service)
    }

    private fun queryPageList() {
        //查库，当前页面的所有FMBean-------start
        val page = SPUtil.getInstance().getString(Constants.CURRENT_PAGE)
        if (lastPage != page) {
            list.clear()//若不清除，则list会累加；
            lastPage = page//lastPage为上一个播放的fm所在的页面；page为当前播放的fm所在的页面
            if (page != null) {
                val pageViewModel = PageViewModel(Injection.getPageDao())
                disposable.add(pageViewModel.getListByPage(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            //                            log("PlayService queryPageList 异步查库getList==${it.size}")
                            list.addAll(it.map { it.fmBean })
                        })
            }
        }
        //查库，当前页面的所有FMBean-------end
    }

    fun play(fmBean: FMBean, context: Activity?) {
        //TODO 检查网络
        if (context != null && !NetworkUtils.isAvailable(context)) {
            context.toast("网络异常")
            return
        }
        //自动结束了 延迟播放handler
        handler = @SuppressLint("HandlerLeak")
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
        mPlayState = Constants.STATE_PREPARING
        mPlayer.setOnBufferingUpdateListener { mp, percent ->
            //缓冲中
            //            log("setOnBufferingUpdateListener percent=$percent")
        }
        mPlayer.setOnCompletionListener {
            //            log("setOnCompletionListener")
            postEvent(ListenEvent(Constants.STATE_IDLE))
            //自动结束了要重新播放
            (handler as Handler).postDelayed(
                    {
                        (handler as Handler).sendEmptyMessage(RETRY)
//                        log("OnCompletion postDelayed 延迟播放")
                    }, 2 * 1000
            )
        }
        mPlayer.setOnPreparedListener {
            //            log("setOnPreparedListener")//缓冲完成
            mPlayer.start()
            mPlayState = Constants.STATE_PLAYING
            postEvent(ListenEvent(Constants.STATE_PLAYING, fmBean))
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            //改变通知栏按钮状态
            Notifier.get().showPlay(currentFMBean)
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
                    //                    log("saveFMBean to History Success==${fmBean.name}")
                })

    }

    fun playPause() {
//        "playOrPause STATE==$mPlayState".logE(LT.RadioNet)
        when (mPlayState) {
            Constants.STATE_PREPARING -> stop()
            Constants.STATE_PLAYING -> pause()
            Constants.STATE_PAUSE -> start()
            Constants.STATE_IDLE -> {
                if (currentFMBean != null)
                    play(currentFMBean!!, null)
            }
        }
    }

    fun start() {
        if (!isPreparing() && !isPausing()) {
            return
        }
        if (mAudioFocusManager.requestAudioFocus()) {
            mPlayer.start()
            //改变通知栏按钮状态
            Notifier.get().showPlay(currentFMBean)
            mPlayState = Constants.STATE_PLAYING
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                MediaSessionManager.get().updatePlaybackState(mPlayState)
            }
            postEvent(ListenEvent(Constants.STATE_PLAYING))
        }


    }

    fun pause() {
        if (!isPlaying()) {
            return
        }
        mPlayer.pause()
        mPlayState = Constants.STATE_PAUSE
        //改变通知栏按钮状态
        Notifier.get().showPause(currentFMBean)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            MediaSessionManager.get().updatePlaybackState(mPlayState)
        }
        postEvent(ListenEvent(Constants.STATE_PAUSE))

    }

    fun stop() {
        if (isIdle()) {
            return
        }
        mPlayer.reset()
        mPlayState = Constants.STATE_IDLE
        //改变通知栏按钮状态
        Notifier.get().showPause(currentFMBean)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            MediaSessionManager.get().updatePlaybackState(mPlayState)
        }
        postEvent(ListenEvent(Constants.STATE_IDLE))

    }

    fun next(activity: Activity?): FMBean? {
        return play(currentIndex + 1, activity)
    }

    fun pre(activity: Activity?): FMBean? {
        return play(currentIndex - 1, activity)
    }

    fun isPlaying(): Boolean {
        return mPlayState == Constants.STATE_PLAYING
    }

    private fun isPausing(): Boolean {
        return mPlayState == Constants.STATE_PAUSE
    }

    fun isPreparing(): Boolean {
        return mPlayState == Constants.STATE_PREPARING
    }

    fun isIdle(): Boolean {
        return mPlayState == Constants.STATE_IDLE
    }

    fun quit() {
        stop()
        QuitTimer.stop()
        mAudioFocusManager.abandonAudioFocus()
        context?.unregisterReceiver(receiver)
        context?.unregisterReceiver(mNoisyReceiver)
        disposable.clear()
        System.exit(0)
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

    fun getMediaPlayer(): io.vov.vitamio.MediaPlayer {
        return mPlayer
    }

    companion object {

        val instance: PlayManager
            get() = PlayManagerHolder.instance
    }

}
