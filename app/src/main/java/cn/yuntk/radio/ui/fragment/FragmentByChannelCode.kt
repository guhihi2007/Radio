package cn.yuntk.radio.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.Observable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import cn.yuntk.radio.BR.presenter
import cn.yuntk.radio.Constants
import cn.yuntk.radio.Constants.CHANNEL_CODE
import cn.yuntk.radio.Constants.CHANNEL_NAME
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseFragment
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.base.Presenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.databinding.FragmentByChannelCodeBinding
import cn.yuntk.radio.ui.activity.CityChannelActivity
import cn.yuntk.radio.ui.activity.ListenerFMBeanActivity
import cn.yuntk.radio.utils.*
import cn.yuntk.radio.viewmodel.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
class FragmentByChannelCode : BaseFragment<FragmentByChannelCodeBinding>(), ItemClickPresenter<FMBean> {

    private var mainViewModel = MainViewModel()
    private lateinit var channelCode: String
    private lateinit var channelName: String

    private lateinit var pageViewModel: PageViewModel
    private lateinit var pageViewModelFactory: PageViewModelFactory

    override fun getLayoutId(): Int = R.layout.fragment_by_channel_code

    override fun initArguments(savedInstanceState: Bundle?) {
        arguments?.apply {
            channelCode = getString(CHANNEL_CODE)
            channelName = getString(CHANNEL_NAME)
        }
    }

    override fun initView() {
        loadingDialog.show()
        //构建存库操作--------------start
        pageViewModelFactory = Injection.providePageViewModelFactory(activity!!)

        pageViewModel = ViewModelProviders.of(activity!!, pageViewModelFactory).get(PageViewModel::class.java)


        mainViewModel.loadFMBeanByChannel(pageViewModel, channelCode)
        val mAdapter = BaseDataBindingAdapter(mContext, R.layout.item_fm_bean, this, mainViewModel.fmBeanList)
        mBinding.run {
            vm = mainViewModel
            presenter = this@FragmentByChannelCode
            homeFragmentRecycler.apply {
                layoutManager = LinearLayoutManager(mContext)
//            layoutManager = GridLayoutManager(mContext, 2)
                adapter = mAdapter
            }
        }



        mainViewModel.fmBeanList.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<FMBean>>() {
            override fun onChanged(sender: ObservableArrayList<FMBean>?) {
            }

            override fun onItemRangeRemoved(sender: ObservableArrayList<FMBean>?, positionStart: Int, itemCount: Int) {
            }

            override fun onItemRangeMoved(sender: ObservableArrayList<FMBean>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
            }

            override fun onItemRangeInserted(sender: ObservableArrayList<FMBean>?, positionStart: Int, itemCount: Int) {
                Lg.e("fmBeanList onItemRangeInserted sender==${sender?.size}")
                loadingDialog.dismiss()
                //保存
                disposable.add(pageViewModel.saveList(channelCode, mainViewModel.fmBeanList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Lg.e("保存$channelCode success")
                        })


                if (sender != null && sender.size > 0) {
                    if (sender[0].isExisUrl == 1) {

                        SPUtil.getInstance().putString(Constants.CURRENT_PAGE, channelName)
                        disposable.add(pageViewModel.getListByPage(channelName)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    mainViewModel.loadFailed.set(false)
                                    if (it.isEmpty()) {   //保存当前页面FMBean,用于锁屏时查询当前页播放列表
                                        //如果没有当前页面数据才保存
                                        disposable.add(pageViewModel.saveList(channelName, mainViewModel.fmBeanList)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe {
                                                    Lg.e("FragmentByChannelCode  addPageFMBean success")
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
        //构建存库操作--------------end

        mainViewModel.loadFailed.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                activity?.log("initView loadFailed==${mainViewModel.loadFailed.get()}")
                dismissDialog()
            }
        })

    }

    override fun onClick(view: View?) {
        mBinding.run {
            when (view?.id) {
                R.id.tv_refresh -> {
                    retryLoadData()
                }
                R.id.tv_set_net -> {
                    startActivityForResult(Intent(Settings.ACTION_SETTINGS), Constants.SET_NET_CODE)
                }
                else -> {
                }
            }
        }

    }

    private fun retryLoadData() {
        showLoading()
        mBinding.run {
            mainViewModel.fmBeanList.clear()
            mainViewModel.loadFMBeanByChannel(pageViewModel, channelCode)
            executePendingBindings()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!NetworkUtils.isAvailable(mContext)) {
            activity?.toast("网络异常")
            return
        }
        if (requestCode == Constants.SET_NET_CODE) {
            retryLoadData()
        }
    }

    //homeFragmentRecycler item点击回调
    override fun onItemClick(view: View?, item: FMBean) {
        activity?.log("onItemClick==$item")
        if (item.radioUrl != null) {
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