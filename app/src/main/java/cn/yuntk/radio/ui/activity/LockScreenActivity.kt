package cn.yuntk.radio.ui.activity

import android.content.*
import android.graphics.drawable.AnimationDrawable
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import cn.yuntk.radio.Constants
import cn.yuntk.radio.R
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.bean.Time
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.databinding.ActivityLockScreenBinding
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.service.LockService
import cn.yuntk.radio.service.MyLockServiceBinder
import cn.yuntk.radio.utils.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * Author : Gupingping
 * Date : 2018/7/27
 * Mail : gu12pp@163.com
 */
open class LockScreenActivity : BaseActivity<ActivityLockScreenBinding>() {

    override fun isFullScreen(): Boolean = true

    override fun getLayoutId(): Int = R.layout.activity_lock_screen

    private var mTime: Time = Time()
    private lateinit var timeReceiver: BroadcastReceiver
    private lateinit var conn: LockServiceConnection
    private var service: LockService? = null
    override fun initView() {
        getDate()
        mBinding.run {
            fmBean = PlayServiceManager.getListenerFMBean()
            presenter = this@LockScreenActivity
            lockScreenDrag.setOnReleasedListener {
                log("lockScreenDrag finish")
                finish()
            }

            time = mTime

            lockScreenPlay.isSelected = PlayServiceManager.isListenerFMBean()
        }


        timeReceiver = timeReceiver { getDate() }
        registerTimeListener(timeReceiver)
        registerEventBus()
    }

    private fun getDate() {
        log("getDate")
        val calender = Calendar.getInstance()
        mTime.hours.set("${calender.get(Calendar.HOUR_OF_DAY)}:${if (calender.get(Calendar.MINUTE) < 10) "0" else ""}${calender.get(Calendar.MINUTE)}")
        mTime.dates.set("${calender.get(Calendar.MONTH) + 1}月${if (calender.get(Calendar.DAY_OF_MONTH) < 10) "0" else ""}${calender.get(Calendar.DAY_OF_MONTH)}日")
        when (calender.get(Calendar.DAY_OF_WEEK)) {
            1 -> mTime.today.set("星期日")
            2 -> mTime.today.set("星期一")
            3 -> mTime.today.set("星期二")
            4 -> mTime.today.set("星期三")
            5 -> mTime.today.set("星期四")
            6 -> mTime.today.set("星期五")
            7 -> mTime.today.set("星期六")
        }
    }

    override fun loadData() {
        conn = LockServiceConnection()
        val intent = Intent()
        intent.setClass(this, LockService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)

    }

    override fun onClick(view: View?) {
        mBinding.run {
            when (view?.id) {
                //上一曲
                lockScreenPre.id -> {
                    lockScreenPre.clickWithTrigger {
                        val playFMBean = PlayServiceManager.next(this@LockScreenActivity)
                        if (playFMBean == null) {
                            toast("已经到顶了")
                        } else {
                            //暂停时切换，按钮变化
                            mBinding.fmBean = playFMBean
                            if (!lockScreenPlay.isSelected) {
                                lockScreenPlay.isSelected = !lockScreenPlay.isSelected
                            }
                        }
                    }
                }
                //播放暂停
                lockScreenPlay.id -> {
                    log("lockScreen pauseContinue")
                    lockScreenPlay.isSelected = !lockScreenPlay.isSelected
                    PlayServiceManager.pauseContinue()
                }
                //下一曲
                lockScreenNext.id -> {
                    lockScreenNext.clickWithTrigger {
                        val preFMBean = PlayServiceManager.pre(this@LockScreenActivity)
                        if (preFMBean == null) {
                            toast("已经到底了")
                        } else {
                            //暂停时切换，按钮变化
                            mBinding.fmBean = preFMBean
                            if (!lockScreenPlay.isSelected) {
                                lockScreenPlay.isSelected = !lockScreenPlay.isSelected
                            }
                        }
                    }
                }

            }
        }
    }

    //锁屏时，后台播放状态改变，接收广播
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isListening(event: ListenEvent) {
        mBinding.run {
            when (event.status) {
                Constants.STATE_PAUSE -> {
                    lockScreenPlay.isSelected = false
                }
                Constants.STATE_IDLE -> {
                    lockScreenPlay.isSelected = false
                }
                Constants.STATE_PLAYING -> {
                    lockScreenPlay.isSelected = true
                }
            }
        }
    }

    override fun onBackPressed() {
        //不响应Back按键
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mBinding.run {
            anim(lockScreenUnlockIv)
        }

    }

    private fun anim(imageView: ImageView) {
//        val mAnimation = AnimationDrawable()
//        mAnimation.addFrame(resources.getDrawable(R.drawable.arrow_1), 500)
//        mAnimation.addFrame(resources.getDrawable(R.drawable.arrow_2), 400)
//        mAnimation.addFrame(resources.getDrawable(R.drawable.arrow_3), 300)
//        mAnimation.addFrame(resources.getDrawable(R.drawable.arrow_4), 200)
//        mAnimation.addFrame(resources.getDrawable(R.drawable.arrow_5), 400)
//        imageView.setImageDrawable(mAnimation)
//        mAnimation.start()
        imageView.setBackgroundResource(R.drawable.unlock_frame)
        val anim = imageView.background as AnimationDrawable
        anim.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        service?.setJump(false)
        unbindService(conn)
        unRegisterEventBus()
        unRegisterTimeListener(timeReceiver)
    }


    inner class LockServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            "LockService onServiceDisconnected".logE(LT.RadioNet)
        }


        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder) {
            val myBinder = iBinder as MyLockServiceBinder
            service = myBinder.mService
            service?.setJump(true)
            "LockService onServiceConnected".logE(LT.RadioNet)
        }

    }
}