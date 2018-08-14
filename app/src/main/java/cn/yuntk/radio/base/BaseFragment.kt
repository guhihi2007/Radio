package cn.yuntk.radio.base

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.manager.PlayServiceManager
import cn.yuntk.radio.view.loading.LoadingDialog
import io.reactivex.disposables.CompositeDisposable

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), Presenter {


    protected lateinit var mBinding: VB
    protected lateinit var mContext: Context
    protected val disposable = CompositeDisposable()
    protected lateinit var loadingDialog: LoadingDialog

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mContext = activity ?: throw Exception("activity 为null")
        loadingDialog = LoadingDialog.instance(activity)
        loadingDialog.setCancelable(true)
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments(savedInstanceState)
    }

    abstract fun initArguments(savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), null, false)
        return mBinding.root
    }

    protected fun listenerRadio(item: FMBean) {
        PlayServiceManager.play(item, activity as Activity)
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    override fun onClick(view: View?) {
    }

    protected fun dismissDialog() {
        loadingDialog.dismiss()
    }

    protected fun showLoading(){
        loadingDialog.show()
    }
    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}