package cn.yuntk.radio.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.databinding.ActivityHistoryBinding
import cn.yuntk.radio.utils.jumpActivity
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.utils.toast
import cn.yuntk.radio.viewmodel.HistoryViewMode
import cn.yuntk.radio.viewmodel.Injection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Author : Gupingping
 * Date : 2018/8/6
 * Mail : gu12pp@163.com
 */
class HistoryActivity : BaseActivity<ActivityHistoryBinding>(), ItemClickPresenter<FMBean> {

    private lateinit var adapter: BaseDataBindingAdapter<FMBean>

    override fun getLayoutId(): Int = R.layout.activity_history

    private lateinit var historyViewModel: HistoryViewMode
    override fun initView() {
        //构建viewModel
        val viewModelFactory = Injection.provideHistoryViewModelFactory(this)
        historyViewModel = ViewModelProviders.of(this, viewModelFactory).get(HistoryViewMode::class.java)
        mBinding.run {
            initBackToolbar(toolbar)
            presenter = this@HistoryActivity
            toolbar.title = "收听记录"
            historyRecycler.layoutManager = LinearLayoutManager(mContext)
            adapter = BaseDataBindingAdapter(this@HistoryActivity, R.layout.item_history_fm_bean, this@HistoryActivity, historyViewModel.historyList)
            historyRecycler.adapter = adapter
            //异步查库
            disposable.add(historyViewModel.getHistoryFMBean()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        log("查询数据库，收听记录==${it.size}")
                    })
        }

    }


    override fun loadData() {

    }

    override fun onItemClick(view: View?, item: FMBean) {
        log("onItemClick==$item")
        jumpActivity(ListenerFMBeanActivity::class.java, item)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_clear, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear -> {
                log("History_clear")
                historyViewModel.historyList.clear()
                disposable.add(historyViewModel.removeAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            log("removeAll callback")
                            toast("已清空")
                        })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun isFullScreen(): Boolean = false

}