package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import cn.yuntk.radio.BuildConfig
import cn.yuntk.radio.Constants
import cn.yuntk.radio.Constants.FOREIGN_CODE
import cn.yuntk.radio.Constants.NATION_CODE
import cn.yuntk.radio.Constants.NET_CODE
import cn.yuntk.radio.Constants.PROVINCE_CODE
import cn.yuntk.radio.ad.AdsConfig
import cn.yuntk.radio.api.FMService
import cn.yuntk.radio.api.RetrofitFactory
import cn.yuntk.radio.api.YtkService
import cn.yuntk.radio.bean.ChannelBean
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.utils.SPUtil
import cn.yuntk.radio.utils.logE
import com.google.gson.Gson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors


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

    val disposable = CompositeDisposable()

    //请求频道信息
    fun loadFMBeanByChannel(pageViewMode: PageViewModel, channelCode: String, level: String = "3") {

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
                }) {
                    "请求失败 查询已存数据库电台".logE("MainViewModel")
                    when (channelCode) {
                        NATION_CODE,
                        PROVINCE_CODE,
                        FOREIGN_CODE,
                        NET_CODE -> {
                            disposable.add(pageViewMode.getListByPage(channelCode)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        "查询已存数据库==$it".logE("MainViewModel")
                                        if (it.isNotEmpty()) {
                                            fmBeanList.clear()
                                            fmBeanList.addAll(it.flatMap {
                                                listOf(it.fmBean!!)
                                            })
                                        } else {
                                            loadFailed.set(true)
                                        }

                                    })
                        }
                        else ->{
                            loadFailed.set(true)
                        }
                    }
                    //                    "loadFMBeanByChannel failed==${it.message}".logE("MainViewModel")
                    //                    loadFailed.set(true)
                }

    }
    /**
     * 请求频道信息(不包含省市列表)，请求最终结果；
     * 未曾使用，未曾写完；由于我想到的判断数据是否全部加载完成的方法不够合理，所以还没有使用过
     */
    private fun loadFMBeanByChannel(channelCode: List<String>,level: String ="3"){
        val service = RetrofitFactory.instance.create(FMService::class.java)
        for (temp in channelCode){
            service.getChannel(level, "1", temp, "1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val result = it.result
                        if (result != null) {
                            if (result[0].isExisUrl!=1){
                                val resultList=ArrayList<String>()
                                for (f in result) {
                                    resultList.add(f.radioId.toString())
                                }
                                loadFMBeanByChannel(resultList,"4")
                            }else {
                                fmBeanList.addAll(result)
                            }
                        }
                    }, {

                    })
        }
    }

    //请求广告配置信息
    fun loadAdConfig() {
        val service = RetrofitFactory.service.create(YtkService::class.java)
        service.getAppConfig(BuildConfig.APPLICATION_ID, BuildConfig.FLAVOR.substring(BuildConfig.FLAVOR.indexOf("_"), BuildConfig.FLAVOR.length), BuildConfig.VERSION_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<AdsConfig> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable.add(d)
                    }

                    override fun onNext(t: AdsConfig) {
                        val gson = Gson()
                        SPUtil.getInstance().putString(Constants.MUSIC_DETAIL, gson.toJson(t.data?.music_detail))
                        SPUtil.getInstance().putString(Constants.HOME_PAGE, gson.toJson(t.data?.home_page))
                        SPUtil.getInstance().putString(Constants.HOME_PAGE_NEW, gson.toJson(t.data?.home_page_new))
                        SPUtil.getInstance().putString(Constants.LISTENING_PAGE, gson.toJson(t.data?.listening_page))
                        SPUtil.getInstance().putString(Constants.START_PAGE, gson.toJson(t.data?.start_page))
                        SPUtil.getInstance().putString(Constants.CATEGORY_PAGE, gson.toJson(t.data?.category_page))

                    }

                    override fun onError(e: Throwable) {
                    }
                })
    }
}
