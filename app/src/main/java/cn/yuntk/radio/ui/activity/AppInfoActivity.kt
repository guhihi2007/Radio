package cn.yuntk.radio.ui.activity

import android.databinding.ObservableArrayList
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.yuntk.radio.BuildConfig
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.AppInfoBean
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.databinding.ActivityAppInfoBinding
import cn.yuntk.radio.utils.Lg
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.utils.toast

/**
 * Author : Gupingping
 * Date : 2018/7/30
 * Mail : gu12pp@163.com
 */
class AppInfoActivity : BaseActivity<ActivityAppInfoBinding>(), ItemClickPresenter<AppInfoBean> {


    private val list = ObservableArrayList<AppInfoBean>()
    override fun isFullScreen(): Boolean = false

    override fun getLayoutId(): Int = R.layout.activity_app_info

    override fun initView() {
        mBinding.run {
            toolbar.title = "应用配置信息"
            addData()
            appInfoRecycler.layoutManager = LinearLayoutManager(mContext)
            appInfoRecycler.adapter = BaseDataBindingAdapter(mContext, R.layout.item_app_info_bean,
                    this@AppInfoActivity, list)

        }
    }

    override fun loadData() {
    }

    override fun onItemClick(view: View?, item: AppInfoBean) {
        log("$item")
        if (item.name=="OpenLog"){
            Lg.mDebuggable=Lg.LEVEL_ALL
            toast("开启日志")
            return
        }
        if (item.name=="CloseLog"){
            Lg.mDebuggable=Lg.LEVEL_OFF
            toast("关闭日志")
            return
        }
    }

    private fun addData(): ObservableArrayList<AppInfoBean> {
        list.add(AppInfoBean("版本号", BuildConfig.VERSION_NAME))
        list.add(AppInfoBean("版本代号", BuildConfig.VERSION_CODE.toString()))
        list.add(AppInfoBean("版本渠道", BuildConfig.FLAVOR.substring(BuildConfig.FLAVOR.indexOf("_"), BuildConfig.FLAVOR.length)))
        list.add(AppInfoBean("Build_time", getString(R.string.build_time)))
        list.add(AppInfoBean("OpenLog", ""))
        list.add(AppInfoBean("CloseLog", ""))
        return list
    }
}