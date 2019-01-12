package cn.yuntk.radio.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Looper
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import cn.yuntk.radio.Constants
import cn.yuntk.radio.R
import cn.yuntk.radio.api.FMService
import cn.yuntk.radio.api.RetrofitFactory
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.base.MyViewTouch
import cn.yuntk.radio.base.Notification
import cn.yuntk.radio.bean.FMActivityBean
import cn.yuntk.radio.databinding.FMBinding
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.utils.*
import cn.yuntk.radio.view.TimingDialog
import cn.yuntk.radio.viewmodel.Injection
import cn.yuntk.radio.viewmodel.PageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception

class FMActivity : BaseActivity<FMBinding>(), MyViewTouch, Notification {
    private var resultFMBean = ArrayList<FMBean>()//所有电台的结果集
    private var current: Int = 0//正在播放第几个/
    private lateinit var currentFMBean: FMBean
    private var cache: Boolean = true//是否本来就有播放的fm
    private var fmActivityBean: FMActivityBean = FMActivityBean()
    private var province: String = "北京"//当前省份
    private var items = ArrayList<String>()//省份集合
    private var isInit: Boolean = true//是否第一次进入
    private var locationUtil: LocationUtil? = null
    private lateinit var pageViewModel: PageViewModel
    override fun isFullScreen(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_fm
    }

    override fun onResume() {
        super.onResume()
        if (SPUtil.getInstance().getString(Constants.CURRENT_PAGE) == Constants.FM_ACTIVITY) {
            try {
                currentFMBean = SPUtil.getInstance().getObject(Constants.LAST_PLAY, FMBean::class.java)
                current = resultFMBean.indexOf(currentFMBean)
                fmActivityBean.currentFM = (360.0 * current / resultFMBean.size).toFloat()
                fmActivityBean.name = resultFMBean[current].name
                fmActivityBean.radioFm = resultFMBean[current].radioFm!!
                mBinding.fmActivityBean = fmActivityBean
                mBinding.myView.invalidate()
            } catch (e: Exception) {
                current = 0
            }
        }
    }

    override fun initView() {
        val pageViewModelFactory = Injection.providePageViewModelFactory(this)

        pageViewModel = ViewModelProviders.of(this, pageViewModelFactory).get(PageViewModel::class.java)
        try {
            province = SPUtil.getInstance().getString(Constants.PROVINCE)
        } catch (e: Exception) {
            locationUtil = LocationUtil.getInstance(this)
        }
        try {
            currentFMBean = SPUtil.getInstance().getObject(Constants.LAST_PLAY, FMBean::class.java)
        } catch (e: Exception) {
            cache = false
        }
        mBinding.run {
            presenter = this@FMActivity
            initBackToolbar(toolbar)
            toolbar.title = "调频"
        }
        registerEventBus()
    }

    override fun loadData() {
        loadingDialog.show()
        fmActivityBean.loading = false
        mBinding.fmActivityBean = fmActivityBean
        Thread {
            kotlin.run {
                locationUtil?.checkPermission()
                i = 0
                n = Constants.placeCodeList.size
                if (isInit) {
                    if (locationUtil?.province != "" && locationUtil != null) {
                        province = locationUtil?.province.toString()
                        SPUtil.getInstance().putString(Constants.PROVINCE, province)
                    }
                } else if (resultFMBean.size > 0)
                    currentFMBean = resultFMBean[current]
                resultFMBean.clear()
                items.clear()
                for (i in Constants.placeCodeList.iterator()) {
                    loadFMBeanByChannel(i, "3")
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterEventBus()
    }

    //这是定位获取的结果的通知，定位功能写的不够完善
    override fun myNoification(description: String?, result: String?) {
        if (result == "ok") {

        } else {
            Looper.prepare()
            Toast.makeText(this, "无定位权限", Toast.LENGTH_SHORT).show()
//            Looper.loop()
        }
    }

    //获取电台
    var n = Constants.placeCodeList.size//需要执行n次才可以将所有电台获取
    var i = 0//用于统计执行了多少次
    private fun loadFMBeanByChannel(channelCode: String, level: String = "3") {
        val service = RetrofitFactory.instance.create(FMService::class.java)
        service.getChannel(level, "1", channelCode, "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val result = it.result
                    if (result != null) {
                        if (result[0].isExisUrl != 1) {
                            n += result.size
                            for (f in result) {
                                items.add(f.name)
                                if (f.name.equals(province)) {
                                    loadFMBeanByChannel(f.radioId.toString(), "4")
                                } else {
                                    i++
                                }
//                                loadFMBeanByChannel(f.radioId.toString(), "4")
                            }
                        } else {
                            resultFMBean.addAll(result)
                        }

                    }
                    i++
                    if (i == n) {
                        orderResult()
                        //此时所有电台均已获取
                        if (!isInit || cache) {
                            for (item in 0..(resultFMBean.size - 1)) {
                                if (currentFMBean.radioId == resultFMBean[item].radioId) {
                                    current = item
                                    if (PlayServiceManager.isListenerFMBean())
                                        mBinding.pause.isSelected = true
                                    break
                                }
                                if (item == resultFMBean.size - 1) {
                                    current = 0
                                }
                            }
                        }
                        currentFMBean = resultFMBean[current]
                        fmActivityBean.province = "当前位置：$province  [切换]"
                        fmActivityBean.currentFM = (360.0 * current / resultFMBean.size).toFloat()
                        fmActivityBean.name = resultFMBean[current].name
                        fmActivityBean.radioFm = resultFMBean[current].radioFm!!
                        fmActivityBean.loading = true
                        mBinding.fmActivityBean = fmActivityBean
                        mBinding.myView.invalidate()
                        loadingDialog.dismiss()
                        isInit = false
                        upDataFavoriteImageView()
                        saveData()
                    }
                }, {
                    i++
                    if (i == n) {
                        orderResult()
                        //此时所有电台均已获取
                        if (resultFMBean.size == 0) {
                            current=0
                            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show()
                            fmActivityBean.loadFailed = true
                        } else {
                            if (!cache) {
                                fmActivityBean.name = resultFMBean[current].name
                                fmActivityBean.currentFM = (current / resultFMBean.size * 360).toFloat()
                                fmActivityBean.radioFm = resultFMBean[current].radioFm!!
                            } else {
                                for (item in 0..(resultFMBean.size - 1)) {
                                    if (currentFMBean.radioId == resultFMBean[item].radioId) {
                                        mBinding.fmActivityBean?.currentFM = (360.0 * i / resultFMBean.size).toFloat()
                                        current = item
                                        if (PlayServiceManager.isListenerFMBean())
                                            mBinding.pause.isSelected = true
                                    }
                                }
                            }
                            fmActivityBean.loading = true
                            isInit = false
                            saveData()
                            currentFMBean = resultFMBean[current]
                            fmActivityBean.province = "当前位置：" + province
                            mBinding.myView.invalidate()
                            upDataFavoriteImageView()
                        }
                        mBinding.fmActivityBean = fmActivityBean
                        loadingDialog.dismiss()

                    }
                })
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        if (resultFMBean.size > 0) {
            when (view) {
                mBinding.top -> {
                    if (current > 0)
                        listenerRadio(resultFMBean[--current], this)
                    else {
                        current = resultFMBean.size - 1
                        listenerRadio(resultFMBean[current], this)
                    }
                }
                mBinding.down -> {
                    if (current < resultFMBean.size - 1)
                        listenerRadio(resultFMBean[++current], this)
                    else {
                        current = 0
                        listenerRadio(resultFMBean[current], this)
                    }
                }
                mBinding.pause -> {
                    if (!PlayServiceManager.isListenerFMBean()) {//针对定时停止后点击继续播放
                        mBinding.pause.isSelected = true
                    } else {
                        PlayServiceManager.pauseContinue()
                        mBinding.pause.isSelected = false
                    }
                }
                mBinding.favoriteImageView -> {
                    mBinding.favoriteImageView.isSelected = !mBinding.favoriteImageView.isSelected
                    if (mBinding.favoriteImageView.isSelected) {
                        disposable.add(viewModel.addFMBeanToCollection(resultFMBean[current])
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    toast("添加成功")
                                })
                    } else {
                        disposable.add(viewModel.removeFMBeanFromCollection(resultFMBean[current])
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    toast("移除成功")
                                })
                    }
                }
            }
            currentFMBean = resultFMBean[current]
            fmActivityBean.name = resultFMBean[current].name
            fmActivityBean.currentFM = (current * 360.0 / resultFMBean.size).toFloat()
            fmActivityBean.radioFm = resultFMBean[current].radioFm!!
            if (view != mBinding.favoriteImageView)
                upDataFavoriteImageView()
            if (mBinding.pause.isSelected)
                PlayServiceManager.play(resultFMBean[current], this)
        } else {
            when (view?.id) {
                R.id.tv_refresh -> {
                    loadData()
                    loadingDialog.show()
                    fmActivityBean.loadFailed = false
                }
                R.id.tv_set_net -> {
                    startActivityForResult(Intent(Settings.ACTION_SETTINGS), Constants.SET_NET_CODE)
                }
            }
        }
        var dialog: TimingDialog? = null
        when (view) {
            mBinding.location -> {
                dialog = TimingDialog(this, object : ItemClickPresenter<Any> {
                    override fun onItemClick(view: View?, item: Any) {
                        log(item.toString())
                        province = item.toString()
                        SPUtil.getInstance().putString(Constants.PROVINCE, province)
                        loadData()
                        dialog!!.dismiss()
                    }
                })
                val temp: Array<String> = Array(items.size, { "" })
                for (i in 0..(temp.size - 1)) {
                    temp[i] = items[i]
                }
                dialog.setTitle("选择地区")
                dialog.setItems(temp)
                dialog.show()
            }
        }
        mBinding.fmActivityBean = fmActivityBean
        mBinding.myView.invalidate()
    }

    //这是自定义View的触摸事件，用于及时更新页面
    override fun onTouch(currentFM: Double, status: Int) {
        current = (currentFM / 360 * resultFMBean.size + 0.5).toInt()
        if (current == resultFMBean.size) {
            current = 0
        }
        fmActivityBean.currentFM = currentFM.toFloat()
        fmActivityBean.name = resultFMBean[current].name
        fmActivityBean.radioFm = resultFMBean[current].radioFm!!
        mBinding.fmActivityBean = fmActivityBean
        if (status == MotionEvent.ACTION_UP) {
            PlayServiceManager.play(resultFMBean[current], this)
            fmActivityBean.currentFM = (360.0 * current / resultFMBean.size).toFloat()
            mBinding.myView.invalidate()
            upDataFavoriteImageView()
        }
    }

    //更新收藏图标
    private fun upDataFavoriteImageView() {
        if (resultFMBean.size > 0) {
            disposable.add(viewModel.isCollectionFMBean(resultFMBean[current])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        mBinding.favoriteImageView.isSelected = it
                        log("isCollectionFMBean isSelected==$it")
                    }, {
                        mBinding.favoriteImageView.isSelected = false
                        log("isCollectionFMBean Error==${it.message}")
                    }))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isListening(event: ListenEvent) {
        log("收听界面接收广播，更新页面状态==$event")
        mBinding.run {
            if (event.fmBean != null) {
//                currentFMBean = event.fmBean!!
//                executePendingBindings()
            }
            when (event.status) {
                Constants.STATE_PLAYING -> {
                    pause.isSelected = true
                }
                Constants.STATE_PAUSE -> {
                    pause.isSelected = false
                }
                Constants.STATE_IDLE -> {
                    pause.isSelected = false
                }
            }
        }
    }

    //将数据存入数据库，便于锁屏页面使用
    private fun saveData() {
        SPUtil.getInstance().putString(Constants.CURRENT_PAGE, Constants.FM_ACTIVITY)
        disposable.add(pageViewModel.getListByPage(Constants.FM_ACTIVITY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isEmpty()) {
                        //如果没有当前页面数据才保存
                        disposable.add(pageViewModel.saveList(Constants.FM_ACTIVITY, resultFMBean)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    Lg.e("CityChannelActivity  addPageFMBean success")
                                }
                        )
                    } else {
                        Lg.e("已经保存过了，不继续")
                    }
                })
    }

    //对得到的结果进行递增排序,排序方式为"AM"优先于"FM"；当字符串中没有"FM"或"AM"时，而只有数字时，这数字将被当做是"FM"排序；其他情况将全部排在最后
    private fun orderResult() {
        var userContrastI: Double
        var userContrastJ: Double
        if (resultFMBean.size > 0) {
            var temp: FMBean
            for (i in 0..(resultFMBean.size - 2)) {
                for (j in 1 + i..(resultFMBean.size - 1)) {
                    userContrastI = try {
                        if (resultFMBean[i].radioFm!!.contains("FM") || !resultFMBean[i].radioFm!!.contains("AM")) {
                            resultFMBean[i].radioFm?.split("FM")?.last()!!.toDouble() * 1000
                        } else {
                            resultFMBean[i].radioFm?.split("AM")?.last()!!.toDouble()
                        }
                    } catch (e: Exception) {
                        999 * 1000.0
                    }
                    userContrastJ = try {
                        if (resultFMBean[j].radioFm!!.contains("FM") || !resultFMBean[j].radioFm!!.contains("AM")) {
                            resultFMBean[j].radioFm?.split("FM")?.last()!!.toDouble() * 1000
                        } else {
                            resultFMBean[j].radioFm?.split("AM")?.last()!!.toDouble()
                        }
                    } catch (e: Exception) {
                        999 * 1000.0
                    }

                    if (userContrastI > userContrastJ) {
                        temp = resultFMBean[i]
                        resultFMBean[i] = resultFMBean[j]
                        resultFMBean[j] = temp
                    }
                }
            }
        }
    }
}