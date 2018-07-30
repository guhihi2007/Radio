package cn.yuntk.radio.ui.activity

import android.content.BroadcastReceiver
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import cn.yuntk.radio.Constants
import cn.yuntk.radio.R
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.Time
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.databinding.ActivityLockScreenBinding
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * Author : Gupingping
 * Date : 2018/7/27
 * Mail : gu12pp@163.com
 */
class LockScreenActivity : BaseActivity<ActivityLockScreenBinding>() {

    override fun isFullScreen(): Boolean = true

    override fun getLayoutId(): Int = R.layout.activity_lock_screen

    private var playList = ArrayList<FMBean>()

    private var mTime: Time = Time()
    private lateinit var timeReceiver: BroadcastReceiver
    override fun initView() {
        getDate()
        mBinding.run {
            fmBean = PlayServiceManager.getListenerFMBean()
            presenter = this@LockScreenActivity
            lockScreenDrag.setOnReleasedListener {
                finish()
            }

            time = mTime

            lockScreenPlay.isSelected = PlayServiceManager.isListenerFMBean()

            anim(lockScreenUnlockIv)
        }

        disposable.add(viewModel.getList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log("异步查库getList==$it")
                    playList.addAll(it)
                })
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
    }

    override fun onClick(view: View?) {
        val index = playList.indexOf(PlayServiceManager.getListenerFMBean())
        log("list=${playList.size},index=$index")
        mBinding.run {
            when (view?.id) {
            //上一曲
                lockScreenPre.id -> {
                    if (playList.size > 1 && index != 0) {
                        fmBean = playList[index - 1]
                        listenerRadio(playList[index - 1], this@LockScreenActivity)

                        //暂停时切换，按钮变化
                        if (!lockScreenPlay.isSelected) {
                            lockScreenPlay.isSelected = !lockScreenPlay.isSelected
                        }
                    } else {
                        toast("收藏里没有了")
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
                    if (playList.size > 1 && index < playList.size - 1) {
                        fmBean = playList[index + 1]
                        listenerRadio(playList[index + 1], this@LockScreenActivity)

                        //暂停时切换，按钮变化
                        if (!lockScreenPlay.isSelected) {
                            lockScreenPlay.isSelected = !lockScreenPlay.isSelected
                        }
                    } else {
                        toast("收藏里没有了")
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

    private fun anim(imageView: ImageView): AnimationDrawable {
        val mAnimation = AnimationDrawable()
        mAnimation.addFrame(resources.getDrawable(R.mipmap.arrow_1), 200)
        mAnimation.addFrame(resources.getDrawable(R.mipmap.arrow_2), 200)
        mAnimation.addFrame(resources.getDrawable(R.mipmap.arrow_3), 400)
        imageView.setImageDrawable(mAnimation)
        mAnimation.start()
        return mAnimation
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterEventBus()
        unRegisterTimeListener(timeReceiver)
    }
}