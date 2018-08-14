package cn.yuntk.radio.ui.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import cn.yuntk.radio.BuildConfig
import cn.yuntk.radio.Constants
import cn.yuntk.radio.Constants.ABOUTUS
import cn.yuntk.radio.Constants.COLLECTION
import cn.yuntk.radio.Constants.FEEDBACK
import cn.yuntk.radio.Constants.FOREIGN_CODE
import cn.yuntk.radio.Constants.HISTORY
import cn.yuntk.radio.Constants.NATION_CODE
import cn.yuntk.radio.Constants.NET_CODE
import cn.yuntk.radio.Constants.PROVINCE_CODE
import cn.yuntk.radio.Constants.TIMIMG
import cn.yuntk.radio.Constants.UPDATE
import cn.yuntk.radio.Constants.channelList

import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.ChannelBean
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.messageEvent.ListenEvent
import cn.yuntk.radio.databinding.ActivityMainBinding
import cn.yuntk.radio.view.FloatViewManager
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.manager.UpdateManager
import cn.yuntk.radio.play.QuitTimer
import cn.yuntk.radio.service.LockService
import cn.yuntk.radio.ui.fragment.FragmentByChannelCode
import cn.yuntk.radio.utils.*
import cn.yuntk.radio.view.TimingDialog
import cn.yuntk.radio.view.widget.ExitDialog
import cn.yuntk.radio.viewmodel.Injection
import cn.yuntk.radio.viewmodel.MainViewModel
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI
import com.alibaba.sdk.android.feedback.util.IUnreadCountCallback
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.vov.vitamio.Vitamio
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity<ActivityMainBinding>(), ItemClickPresenter<ChannelBean> {

    private var temp: Fragment? = null
    override fun getLayoutId(): Int = R.layout.activity_main
    private var dialog: TimingDialog? = null
    private var field = ObservableField<FMBean>()
    private var mainViewModel: MainViewModel = MainViewModel()
    override fun initView() {
        /**--------应用必要初始化--------*/
        SPUtil.init(this)
        if (Vitamio.isInitialized(applicationContext)) {
            log("Vitamio isInitialized")
            PlayServiceManager.init(this)
        }
        startService(Intent(this, LockService::class.java))
        FeedbackAPI.init(application, Constants.FEED_BACK_KEY, Constants.FEED_BACK_SECRET)
        if (!BuildConfig.DEBUG) {
            log("CrashReport/UMConfigure init ")
            CrashReport.initCrashReport(application, Constants.BUGLY_KEY, false)
            UMConfigure.init(application, Constants.UMENG_KEY, BuildConfig.FLAVOR, UMConfigure.DEVICE_TYPE_PHONE, null)
        }
        /**--------应用初必要始化--------*/

        /**--------布局初始化--------*/
        val toolbar = mBinding.toolbar
        setSupportActionBar(toolbar)

        val drawer = mBinding.drawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        mainViewModel.channelBean.addAll(Constants.channelList)

        mBinding.apply {
            vm = mainViewModel
            navView.itemIconTintList = null
            nvMenuRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            nvMenuRecyclerView.adapter = BaseDataBindingAdapter<ChannelBean>(this@MainActivity,
                    R.layout.item_navigation, this@MainActivity, mainViewModel.channelBean)

//            navView.apply {
//                setNavigationItemSelectedListener(this@MainActivity)
//                itemIconTintList = null//设置可以使抽屉显示icon自己的颜色
//            }
        }
        /**--------布局初始化--------*/

        registerEventBus()

        /**--------获取反馈回复--------*/
        getFeedbackUnreadCounts()
    }


    override fun loadData() {
        changeFragment(FragmentByChannelCode.newInstance(channelList[0].name, channelList[0].chanelCode), channelList[0].name)
        UpdateManager.check(this, true)
    }


    override fun onStart() {
        super.onStart()

        //悬浮框内数据
        field.set(SPUtil.getInstance().getObject(Constants.LAST_PLAY, FMBean::class.java))
        if (field.get() != null)
            FloatViewManager.getInstance().attach(this)
        FloatViewManager.getInstance().add(this, this, field.get())
    }

    override fun onResume() {
        super.onResume()
        //友盟统计
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        //友盟统计
        MobclickAgent.onPause(this)
    }

    override fun onStop() {
        super.onStop()
        FloatViewManager.getInstance().detach(this)

    }

    override fun onItemClick(view: View?, item: ChannelBean) {
        log("MainActivity onItemClick item==$item")
        if (item.resId != -1)
            onCloseDrawerLayout()
        when (item.chanelCode) {
            NATION_CODE,
            PROVINCE_CODE,
            FOREIGN_CODE,
            NET_CODE -> {
                changeFragment(FragmentByChannelCode.newInstance(item.name, item.chanelCode), item.name)
            }
            TIMIMG -> {
                dialog = TimingDialog(this, object : ItemClickPresenter<Any> {
                    override fun onItemClick(view: View?, item: Any) {
                        log(item.toString())
                        startTimer(item)
                        dialog!!.dismiss()
                    }
                })
                dialog!!.setTitle("定时关闭")
                dialog!!.setItems(resources.getStringArray(R.array.timer_text))
                dialog!!.show()
            }
            COLLECTION -> {
                jumpActivity(CollectionActivity::class.java, null)
            }
            HISTORY -> {
                jumpActivity(HistoryActivity::class.java, null)
            }
            ABOUTUS -> {
                jumpActivity(AboutUsActivity::class.java, null)
            }
            FEEDBACK -> {
                FeedbackAPI.openFeedbackActivity()
            }
            UPDATE -> {
                UpdateManager.check(this, false)
            }
        }
    }

    //悬浮窗点击回调
    override fun onClick(view: View?) {
        if (field.get() != null) {
            when (view?.id) {
                R.id.ll_out -> {
                    log("MainActivity onClick ll_out==${view.id}")
                    //获取最新的频道
                    field.set(SPUtil.getInstance().getObject(Constants.LAST_PLAY, FMBean::class.java))
                    jumpActivity(ListenerFMBeanActivity::class.java, field.get())
                }
                R.id.float_play -> {
                    log("MainActivity onClick float_play==${view.id}")
                    view.isSelected = !view.isSelected
                    val state = PlayServiceManager.getListenerState()
                    if (state == Constants.STATE_IDLE || state == Constants.STATE_PAUSE) {
                        PlayServiceManager.play(field.get()!!, this)
                    } else {
                        PlayServiceManager.pauseContinue()
                    }
                }
            }
        }
    }

    //接受定时停止时，更新悬浮窗按钮状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isListening(event: ListenEvent) {
        log("MainActivity 接受广播，更新悬浮窗按钮状态==$event")
        FloatViewManager.getInstance().apply {
            if (event.fmBean != null)
                floatingView.setFMBean(event.fmBean)
            floatingView.float_play.isSelected = event.status == Constants.STATE_PLAYING
        }
    }

//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_nation_code -> replaceFragment(item.title.toString(), NATION_CODE)
//            R.id.nav_province_code -> replaceFragment(item.title.toString(), PROVINCE_CODE)
//            R.id.nav_foreign_code -> replaceFragment(item.title.toString(), FOREIGN_CODE)
//            R.id.nav_net_code -> replaceFragment(item.title.toString(), NET_CODE)
//
//            R.id.nav_timingClose -> {
//                dialog = TimingDialog(this, object : ItemClickPresenter<Any> {
//                    override fun onItemClick(view: View?, item: Any) {
//                        log(item.toString())
//                        startTimer(item)
//                        dialog!!.dismiss()
//                    }
//                })
//                dialog!!.setTitle("定时关闭")
//                dialog!!.setItems(resources.getStringArray(R.array.timer_text))
//                dialog!!.show()
//            }
//
//            R.id.nav_favorite -> {
//                jumpActivity(CollectionActivity::class.java, null)
//            }
//            R.id.nav_about_us -> {
//                jumpActivity(AboutUsActivity::class.java, null)
//            }
//            R.id.nav_feed_back -> {
//                FeedbackAPI.openFeedbackActivity()
//            }
//
//
//        }
//        onCloseDrawerLayout()
//        return true
//    }

    private fun startTimer(item: Any) {
        val minute: Long = when (item) {
            "10分钟后" -> 10
            "20分钟后" -> 20
            "30分钟后" -> 30
            "45分钟后" -> 45
            "60分钟后" -> 60
            "90分钟后" -> 60
            else -> 0
        }
        QuitTimer.start(minute * 60 * 1000)
        if (minute > 0)
            toast(getString(R.string.timer_set, minute.toString()))
        else
            toast(getString(R.string.timer_cancel))
    }

    private fun onCloseDrawerLayout() {
        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.collection_delete, menu)
        return true
    }

    private fun replaceFragment(name: String, chanelCode: String) {
        changeFragment(FragmentByChannelCode.newInstance(name, chanelCode), name)
    }

    private fun changeFragment(fragment: Fragment, title: String?) {
        supportActionBar?.title = title
        switchFragment(temp, fragment)
        temp = fragment
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayServiceManager.unbind(this)
        unRegisterEventBus()
    }

    override fun isFullScreen(): Boolean = false


    private fun getFeedbackUnreadCounts() {
        FeedbackAPI.getFeedbackUnreadCount(object : IUnreadCountCallback {
            override fun onSuccess(p0: Int) {
                log("getFeedbackUnreadCounts p0==$p0")
                setNewVisible(p0 > 0)
            }

            override fun onError(p0: Int, p1: String?) {

            }
        })
    }

    fun setNewVisible(visible: Boolean) {
        //设置反馈new图标是否可见,有点low啊，没想到什么好方法
        mainViewModel.channelBean[mainViewModel.channelBean.size - 2].newVisible = visible
        mBinding.apply {
            vm = mainViewModel
            executePendingBindings()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                onCloseDrawerLayout()
            } else
                ExitDialog(this).show()
            true
        } else
            super.onKeyDown(keyCode, event)
    }
}
