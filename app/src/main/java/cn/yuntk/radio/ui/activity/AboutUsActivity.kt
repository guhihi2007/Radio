package cn.yuntk.radio.ui.activity

import android.view.View
import cn.yuntk.radio.BuildConfig
import cn.yuntk.radio.R
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.databinding.ActivityAboutUsBinding
import cn.yuntk.radio.utils.jumpActivity

/**
 * Author : Gupingping
 * Date : 2018/7/30
 * Mail : gu12pp@163.com
 */
class AboutUsActivity : BaseActivity<ActivityAboutUsBinding>() {

    private var count: Int = 0
    override fun isFullScreen(): Boolean = false

    override fun getLayoutId(): Int = R.layout.activity_about_us

    override fun initView() {
        mBinding.run {
            initBackToolbar(toolbar)
            toolbar.title = "关于我们"
            presenter = this@AboutUsActivity
            aboutUsTitle.text = resources.getString(R.string.app_name)
            aboutUsVersion.text = BuildConfig.VERSION_NAME
        }
    }

    override fun loadData() {
    }

    override fun onClick(view: View?) {
        mBinding.run {
            if (view?.id == mBinding.aboutUsIcon.id) {
                count++
                if (count == 5) {
                    jumpActivity(AppInfoActivity::class.java, null)
                }
            }
        }

    }
}