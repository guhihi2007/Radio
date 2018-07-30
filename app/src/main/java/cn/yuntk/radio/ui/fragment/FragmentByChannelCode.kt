package cn.yuntk.radio.ui.fragment

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import cn.yuntk.radio.Constants.CHANNEL_CODE
import cn.yuntk.radio.Constants.CHANNEL_NAME
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseFragment
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.databinding.FragmentByChannelCodeBinding
import cn.yuntk.radio.ui.activity.CityChannelActivity
import cn.yuntk.radio.ui.activity.ListenerFMBeanActivity
import cn.yuntk.radio.utils.jumpActivity
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.utils.toast
import cn.yuntk.radio.viewmodel.MainViewModel

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
class FragmentByChannelCode : BaseFragment<FragmentByChannelCodeBinding>(), ItemClickPresenter<FMBean> {

    private var mainViewModel = MainViewModel()
    private lateinit var channelCode: String
    private lateinit var channelName: String

    override fun getLayoutId(): Int = R.layout.fragment_by_channel_code

    override fun initArguments(savedInstanceState: Bundle?) {
        arguments?.apply {
            channelCode = getString(CHANNEL_CODE)
            channelName = getString(CHANNEL_NAME)
        }
    }

    override fun initView() {
        mainViewModel.loadFMBeanByChannel(channelCode)
        mBinding.vm = mainViewModel
        val mAdapter = BaseDataBindingAdapter(mContext, R.layout.item_fm_bean, this, mainViewModel.fmBeanList)
        mBinding.homeFragmentRecycler.apply {
//            layoutManager = LinearLayoutManager(mContext)
            layoutManager=GridLayoutManager(mContext,2)
            adapter = mAdapter
        }
    }


    override fun onItemClick(view: View?, item: FMBean) {
        activity?.log("onItemClick==$item")
        if (item.isExisUrl == 1) {
            //跳转播放页面
            activity?.jumpActivity(ListenerFMBeanActivity::class.java, item)
        } else {
            //跳转子页面
            startCityChannelActivity(item)
        }
    }

    private fun startCityChannelActivity(item: FMBean) {
        activity?.jumpActivity(CityChannelActivity::class.java, item)
    }

    companion object {

        fun newInstance(channelName: String?, channelCode: String?): FragmentByChannelCode {
            val fragment = FragmentByChannelCode()
            if (!TextUtils.isEmpty(channelCode) || !TextUtils.isEmpty(channelName)) {
                val bundle = Bundle()
                bundle.putString(CHANNEL_CODE, channelCode)
                bundle.putString(CHANNEL_NAME, channelName)
                fragment.arguments = bundle
            }
            return fragment
        }

    }

}