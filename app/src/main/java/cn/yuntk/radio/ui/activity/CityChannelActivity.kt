package cn.yuntk.radio.ui.activity

import android.support.v7.widget.GridLayoutManager
import android.view.View
import cn.yuntk.radio.Constants
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.databinding.ActivityCityChannelBinding
import cn.yuntk.radio.utils.jumpActivity
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.viewmodel.MainViewModel

/**
 * Author : Gupingping
 * Date : 2018/7/19
 * Mail : gu12pp@163.com
 */
class CityChannelActivity : BaseActivity<ActivityCityChannelBinding>(), ItemClickPresenter<FMBean> {
    override fun isFullScreen(): Boolean =false

    private val mainViewModel = MainViewModel()
    private var cityFMBean: FMBean? = null
    override fun getLayoutId(): Int = R.layout.activity_city_channel

    override fun initView() {
        cityFMBean = intent?.getSerializableExtra(Constants.KEY_SERIALIZABLE) as FMBean
        mBinding.run {
            vm = mainViewModel
            initBackToolbar(toolbar)
            toolbar.title = "城市之音"
//            cityChannelRecycler.layoutManager = LinearLayoutManager(mContext)
            cityChannelRecycler.layoutManager = GridLayoutManager(mContext,2)
            cityChannelRecycler.adapter = BaseDataBindingAdapter(mContext, R.layout.item_fm_bean,
                    this@CityChannelActivity, mainViewModel.fmBeanList)
        }
    }

    override fun loadData() {
        mainViewModel.loadFMBeanByChannel(cityFMBean?.radioId.toString(), "4")
    }

    override fun onItemClick(view: View?, item: FMBean) {
        log("onItemClick==$item")
        jumpActivity(ListenerFMBeanActivity::class.java, item)
    }

}