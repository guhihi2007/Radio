package cn.yuntk.radio.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.yuntk.radio.Constants
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.databinding.ActivityCityChannelBinding
import cn.yuntk.radio.utils.Lg
import cn.yuntk.radio.utils.SPUtil
import cn.yuntk.radio.utils.jumpActivity
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.viewmodel.Injection
import cn.yuntk.radio.viewmodel.MainViewModel
import cn.yuntk.radio.viewmodel.PageViewModel
import cn.yuntk.radio.viewmodel.PageViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Author : Gupingping
 * Date : 2018/7/19
 * Mail : gu12pp@163.com
 */
class CityChannelActivity : BaseActivity<ActivityCityChannelBinding>(), ItemClickPresenter<FMBean> {
    override fun isFullScreen(): Boolean = false

    private val mainViewModel = MainViewModel()
    private var cityFMBean: FMBean? = null
    private lateinit var pageViewModel: PageViewModel
    private lateinit var pageViewModelFactory: PageViewModelFactory

    override fun getLayoutId(): Int = R.layout.activity_city_channel

    override fun initView() {
        cityFMBean = intent?.getSerializableExtra(Constants.KEY_SERIALIZABLE) as FMBean
        mBinding.run {
            vm = mainViewModel
            initBackToolbar(toolbar)
            toolbar.title = "城市之音"
            cityChannelRecycler.layoutManager = LinearLayoutManager(mContext)
//            cityChannelRecycler.layoutManager = GridLayoutManager(mContext, 2)
            cityChannelRecycler.adapter = BaseDataBindingAdapter(mContext, R.layout.item_fm_bean,
                    this@CityChannelActivity, mainViewModel.fmBeanList)
        }

        pageViewModelFactory = Injection.providePageViewModelFactory(this)

        pageViewModel = ViewModelProviders.of(this, pageViewModelFactory).get(PageViewModel::class.java)


        mainViewModel.fmBeanList.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<FMBean>>() {
            override fun onChanged(sender: ObservableArrayList<FMBean>?) {
            }

            override fun onItemRangeRemoved(sender: ObservableArrayList<FMBean>?, positionStart: Int, itemCount: Int) {

            }

            override fun onItemRangeMoved(sender: ObservableArrayList<FMBean>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
            }

            override fun onItemRangeInserted(sender: ObservableArrayList<FMBean>?, positionStart: Int, itemCount: Int) {
                Lg.e("fmBeanList onItemRangeInserted sender==${sender?.size}")
                //保存当前页面FMBean,用于锁屏时查询当前页播放列表
                if (sender != null && sender.size > 0) {
                    if (sender[0].isExisUrl == 1) {
                        SPUtil.getInstance().putString(Constants.CURRENT_PAGE, cityFMBean!!.name)
                        disposable.add(pageViewModel.getListByPage(cityFMBean!!.name)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    if (it.isEmpty()) {
                                        //如果没有当前页面数据才保存
                                        disposable.add(pageViewModel.saveList(cityFMBean!!.name, mainViewModel.fmBeanList)
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
                }
            }

            override fun onItemRangeChanged(sender: ObservableArrayList<FMBean>?, positionStart: Int, itemCount: Int) {
            }

        })
    }

    override fun loadData() {
        mainViewModel.loadFMBeanByChannel(cityFMBean?.radioId.toString(), "4")
    }

    override fun onItemClick(view: View?, item: FMBean) {
        log("onItemClick==$item")
        jumpActivity(ListenerFMBeanActivity::class.java, item)
    }

}