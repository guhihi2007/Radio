package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import cn.yuntk.radio.api.FMService
import cn.yuntk.radio.api.RetrofitFactory
import cn.yuntk.radio.bean.ChannelBean
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.utils.logE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Author : Gupingping
 * Date : 2018/7/11
 * Mail : gu12pp@163.com
 */
class MainViewModel : ViewModel() {

    val fmBeanList = ObservableArrayList<FMBean>()

    val fmBean = ObservableField<String>()

    val loadFailed = ObservableField<Boolean>()

    val channelBean = ObservableArrayList<ChannelBean>()

    fun loadFMBeanByChannel(channelCode: String, level: String = "3") {
        val service = RetrofitFactory.instance.create(FMService::class.java)
        service.getChannel(level, "1", channelCode, "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    "ChannelSubBean==${it.message}".logE("MainViewModel")
                    val result = it.result
                    if (result != null) {
                        fmBeanList.addAll(result)
                    }
                }, {
                    "loadFMBeanByChannel failed==${it.message}".logE("MainViewModel")
                    loadFailed.set(true)
                })

    }

}
