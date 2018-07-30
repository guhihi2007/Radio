package cn.yuntk.radio.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableArrayList
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import cn.yuntk.radio.R
import cn.yuntk.radio.adapter.BaseDataBindingAdapter
import cn.yuntk.radio.adapter.CheckBoxDataBindingAdapter
import cn.yuntk.radio.base.BaseActivity
import cn.yuntk.radio.base.CheckBoxPresenter
import cn.yuntk.radio.base.ItemClickPresenter
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.databinding.ActivityCollectionBinding
import cn.yuntk.radio.utils.log
import cn.yuntk.radio.viewmodel.FMBeanViewModel
import cn.yuntk.radio.viewmodel.Injection
import cn.yuntk.radio.viewmodel.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Author : Gupingping
 * Date : 2018/7/26
 * Mail : gu12pp@163.com
 * 收藏管理 页面
 */
class CollectionActivity : BaseActivity<ActivityCollectionBinding>(), CompoundButton.OnCheckedChangeListener {


//    private lateinit var viewModelFactory: ViewModelFactory
//    private lateinit var viewModel: FMBeanViewModel
    private lateinit var adapter: CheckBoxDataBindingAdapter
    private val list = ArrayList<FMBean>()
    override fun onClick(view: View?) {
        log("onClick==${view?.id.toString()}")
    }



    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        log("onCheckedChanged isChecked==$isChecked")

    }

    override fun getLayoutId(): Int = R.layout.activity_collection


    override fun initView() {
        //构建viewModel
//        viewModelFactory = Injection.provideFMBeanViewModelFactory(this)
//        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FMBeanViewModel::class.java)

        mBinding.run {
            initBackToolbar(toolbar)
            presenter = this@CollectionActivity
            toolbar.title = "收藏管理"
            cityChannelRecycler.layoutManager = LinearLayoutManager(mContext)
            adapter = CheckBoxDataBindingAdapter(mContext, R.layout.item_collection_manager, list)
            cityChannelRecycler.adapter = adapter
            //异步查库
            disposable.add(viewModel.getList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        adapter.setData(it)
                    })
        }

    }


    override fun loadData() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {

                adapter.removeList(adapter.getDeleteList())
                disposable.add(viewModel.removeList(adapter.getDeleteList())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {

                        })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun isFullScreen(): Boolean =false

}