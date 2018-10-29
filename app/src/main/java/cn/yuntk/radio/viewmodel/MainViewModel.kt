package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import cn.yuntk.radio.BuildConfig
import cn.yuntk.radio.Constants.FOREIGN_CODE
import cn.yuntk.radio.Constants.NATION_CODE
import cn.yuntk.radio.Constants.NET_CODE
import cn.yuntk.radio.Constants.PROVINCE_CODE
import cn.yuntk.radio.api.FMService
import cn.yuntk.radio.api.RetrofitFactory
import cn.yuntk.radio.api.YtkService
import cn.yuntk.radio.bean.ChannelBean
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.ibook.ads.ADConstants
import cn.yuntk.radio.ibook.util.LogUtils
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil
import cn.yuntk.radio.utils.logE
import com.google.gson.Gson
import com.yuntk.ibook.ads.AdsConfig
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    //请求广告配置信息
    fun loadAdConfig() {
        val service = RetrofitFactory.service.create(YtkService::class.java)
        service.getAppConfig(BuildConfig.APPLICATION_ID, BuildConfig.FLAVOR.substring(BuildConfig.FLAVOR.indexOf("_"), BuildConfig.FLAVOR.length), BuildConfig.VERSION_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    "loadAdConfig Result==${it.message}".logE("MainViewModel")
                    Executors.newCachedThreadPool().submit {
                        saveConfig(it)
                    }
                }, {
                    "loadAdConfig Throwable==${it.message}".logE("MainViewModel")
                })

    }

    //保存广告配置
    private fun saveConfig(result: AdsConfig) {
        val gson = Gson()
        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.MUSIC_DETAIL, gson.toJson(result.data!!.music_detail))
            LogUtils.e("AdsManager----", "getMusic_detail===" + gson.toJson(result.data!!.music_detail))
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST1, gson.toJson(result.data!!.home_page))
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST2, gson.toJson(result.data!!.home_page))
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST3, gson.toJson(result.data!!.home_page))
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST4, gson.toJson(result.data!!.home_page))
            LogUtils.e("AdsManager----", "getHome_page===" + gson.toJson(result.data!!.home_page))
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_NEW, gson.toJson(result.data!!.home_page_new))
            LogUtils.e("AdsManager----", "getHome_page_new===" + gson.toJson(result.data!!.home_page_new))
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.CATEGORY_PAGE, gson.toJson(result.data!!.category_page))
            LogUtils.e("AdsManager----", "getCategory_page===" + gson.toJson(result.data!!.category_page))

        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.LISTENING_PAGE, gson.toJson(result.data!!.listening_page))
            LogUtils.e("AdsManager----", "getListening_page===" + gson.toJson(result.data!!.listening_page))
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.START_PAGE, gson.toJson(result.data!!.start_page))
            LogUtils.e("AdsManager----", "getStart_page===" + gson.toJson(result.data!!.listening_page))
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }
}
