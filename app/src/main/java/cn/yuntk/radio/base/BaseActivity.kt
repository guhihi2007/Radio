package cn.yuntk.radio.base

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.view.loading.LoadingDialog
import cn.yuntk.radio.viewmodel.CollectionViewModel
import cn.yuntk.radio.viewmodel.Injection
import cn.yuntk.radio.viewmodel.CollectionViewModelFactory
import io.reactivex.disposables.CompositeDisposable


/**
 * Author : Gupingping
 * Date : 2018/7/19
 * Mail : gu12pp@163.com
 */
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), Presenter {
    protected lateinit var mBinding: VB

    protected lateinit var mContext: Context

    protected val disposable = CompositeDisposable()
    private lateinit var viewModelFactory: CollectionViewModelFactory
    protected lateinit var viewModel: CollectionViewModel
    protected lateinit var handler: Handler
    protected lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFullScreen()) {
            configScreen()
        }
        loadingDialog = LoadingDialog.instance(this)
        loadingDialog.setCancelable(true)
        handler = Handler(mainLooper)
        mBinding = DataBindingUtil.setContentView<VB>(this, getLayoutId())
        mContext = this
        Injection.init(this)

        //构建viewModel
        viewModelFactory = Injection.provideCollectionViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionViewModel::class.java)

        initView()
        loadData()
    }


    abstract fun isFullScreen(): Boolean

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun loadData()

    fun initBackToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        val bar = supportActionBar
        if (bar != null) {
            bar.title = null
            bar.setDisplayHomeAsUpEnabled(true)
            bar.setDisplayShowHomeEnabled(true)
            bar.setDisplayShowTitleEnabled(true)
            bar.setHomeButtonEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {

    }

    protected fun listenerRadio(item: FMBean, context: Activity) {
        PlayServiceManager.play(item, context)
    }

    protected fun pauseContinue() {
        PlayServiceManager.pauseContinue()
    }

    private fun configScreen() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.addFlags(// 有密码的情况下,覆盖在锁屏界面上
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    }

    protected fun dismissDialog(){
        loadingDialog.dismiss()
    }

    protected fun showLoading(){
        loadingDialog.show()
    }

}