package cn.yuntk.radio.play

import android.os.Handler
import android.text.format.DateUtils
import cn.yuntk.radio.service.PlayService

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
object QuitTimer {

    private var mPlayService: PlayService? = null
    private var mTimerCallback: EventCallback<Long>? = null
    private var mHandler: Handler? = null
    private var mTimerRemain: Long = 0

    fun init(playService: PlayService, handler: Handler, timerCallback: EventCallback<Long>) {
        mPlayService = playService
        mHandler = handler
        mTimerCallback = timerCallback
    }

    fun start(milli: Long) {
        stop()
        if (milli > 0) {
            mTimerRemain = milli + DateUtils.SECOND_IN_MILLIS
            mHandler!!.post(mQuitRunnable)
        } else {
            mTimerRemain = 0
            mTimerCallback!!.onEvent(mTimerRemain)
        }
    }

    fun stop() {
        mHandler!!.removeCallbacks(mQuitRunnable)
    }

    private val mQuitRunnable = object : Runnable {
        override fun run() {
            mTimerRemain -= DateUtils.SECOND_IN_MILLIS
            if (mTimerRemain > 0) {
                mTimerCallback!!.onEvent(mTimerRemain)
                mHandler!!.postDelayed(this, DateUtils.SECOND_IN_MILLIS)
            } else {
                mPlayService!!.quit()
            }
        }
    }

}