package cn.yuntk.radio.ui.activity

import android.app.Dialog
import android.content.Context
import android.databinding.ObservableArrayList
import android.graphics.Point
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import cn.yuntk.radio.Constants
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.databinding.ActivityListenerBinding
import cn.yuntk.radio.ibook.ads.ADConstants
import cn.yuntk.radio.ibook.ads.AdController
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Author : Gupingping
 * Date : 2018/7/19
 * Mail : gu12pp@163.com
 */
class ListenerFMBeanActivity : BaseActivity<ActivityListenerBinding>(), ItemClickPresenter<FMBean> {

    private lateinit var mFmBean: FMBean
    private lateinit var bottomDialog: Dialog
    private lateinit var builder: AdController
    override fun getLayoutId(): Int = R.layout.activity_listener

    override fun initView() {
        mFmBean = intent.getSerializableExtra(Constants.KEY_SERIALIZABLE) as FMBean
        log("ListenerFMBeanActivity  initView mFmBean cityName=${mFmBean.cityName}")
        mFmBean.isPlaying = true
        initBackToolbar(mBinding.toolbar)
        mBinding.run {
            fmBean = mFmBean
            presenter = this@ListenerFMBeanActivity
        }
        bottomDialog = Dialog(this, R.style.BottomDialog)

        registerEventBus()
        //广告展示
        builder = AdController.Builder(this)
                .setContainer(mBinding.adContainerFl)
                .setPage(ADConstants.LISTENING_PAGE)
                .create()
        builder.show()
    }

    override fun loadData() {
        //异步查库并更新收藏按钮
        disposable.add(viewModel.isCollectionFMBean(mFmBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mBinding.favoriteImageView.isSelected = it
                    log("isCollectionFMBean isSelected==$it")
                }, {
                    log("isCollectionFMBean Error==${it.message}")
                }))

        //排除正在播放时点击悬浮窗进来,重新播放
        val current = PlayServiceManager.getListenerFMBean()

        if (current != mFmBean) {
            listenerRadio(mFmBean, this)
            return
        }
        //暂停时，继续播放
        if (!PlayServiceManager.isListenerFMBean()) {
            listenerRadio(current, this)
        }
        mBinding.run {
            roundView.start()
            playImageView.isSelected = true
        }

    }


    override fun onClick(view: View?) {
        mBinding.run {
            when (view) {
                //旋转view
                roundView -> {
                    startOrStop()
                }
                //播放按钮
                playImageView -> {
                    startOrStop()
                }
                //加入收藏
                favoriteImageView -> {
                    favoriteImageView.isSelected = !favoriteImageView.isSelected
                    if (favoriteImageView.isSelected) {
                        disposable.add(viewModel.addFMBeanToCollection(mFmBean)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    toast("添加成功")
                                })
                    } else {
                        disposable.add(viewModel.removeFMBeanFromCollection(mFmBean)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    toast("移除成功")
                                })
                    }
                }
                //展示收藏
                collectionListImageView -> {
                    showCollectionList(this@ListenerFMBeanActivity)
                }
                else -> {

                }
            }
        }
    }

    private fun showCollectionList(context: Context) {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_playlist, null)
        bottomDialog.setContentView(contentView)
        val params = contentView.layoutParams as ViewGroup.MarginLayoutParams
        params.width = context.resources.displayMetrics.widthPixels - DensityUtil.dp2px(context, 16f)
        params.bottomMargin = DensityUtil.dp2px(context, 8f)
        contentView.layoutParams = params
        bottomDialog.window!!.setGravity(Gravity.BOTTOM)
        bottomDialog.setCanceledOnTouchOutside(true)
        bottomDialog.window!!.setWindowAnimations(R.style.BottomDialog_Animation)


        val recycleView = contentView.findViewById<RecyclerView>(R.id.RecyclerView_playList)
        recycleView.layoutManager = LinearLayoutManager(context)
        val point = Point()
        window.windowManager.defaultDisplay.getSize(point)
        recycleView.layoutParams = LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, point.y / 3)
        val list = ObservableArrayList<FMBean>()
        //异步查库
        disposable.add(viewModel.getList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log("异步查库getList==$it")
                    it.forEach {
                        it.isPlaying = it == mFmBean
                        list.add(it)
                    }
                })
        val adapter = BaseDataBindingAdapter(context, R.layout.item_listener_activity_collection_list, this, list)
        recycleView.adapter = adapter

        recycleView.scrollToPosition(list.indexOf(PlayServiceManager.getListenerFMBean()))

        bottomDialog.show()
    }

    override fun onItemClick(view: View?, item: FMBean) {
        toast(item.name)
        mBinding.run {
            mFmBean = item
            toolbar.title = item.name
            favoriteImageView.isSelected = true
            listenerText.text = item.radioFm
        }
        listenerRadio(item, this)
        bottomDialog.dismiss()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isListening(event: ListenEvent) {
        log("收听界面接收广播，更新页面状态==$event")
        mBinding.run {
            if (event.fmBean != null) {
                mFmBean = event.fmBean!!
                toolbar.title = event.fmBean!!.name
                listenerText.text = event.fmBean!!.radioFm
                executePendingBindings()
            }
            when (event.status) {
                Constants.STATE_PLAYING -> {
                    doStart()
                }
                Constants.STATE_PAUSE -> {
                    doPause()
                }
                Constants.STATE_IDLE -> {
                    doPause()
                }
            }
        }
    }

    private fun startOrStop() {
        mBinding.run {
            roundView.apply {
                if (isRunning()) {
                    doPause()
                } else {
                    doStart()
                }
            }
        }

        if (!PlayServiceManager.isListenerFMBean()) {//针对定时停止后点击继续播放
            PlayServiceManager.play(mFmBean, this)
        } else
            PlayServiceManager.pauseContinue()
    }

    private fun ActivityListenerBinding.doStart() {
        mBinding.roundView.start()
        playImageView.isSelected = true
    }

    private fun ActivityListenerBinding.doPause() {
        mBinding.roundView.pause()
        playImageView.isSelected = false

    }


    override fun onDestroy() {
        super.onDestroy()
        unRegisterEventBus()
        builder.destroy()
    }

    override fun isFullScreen(): Boolean = false


}