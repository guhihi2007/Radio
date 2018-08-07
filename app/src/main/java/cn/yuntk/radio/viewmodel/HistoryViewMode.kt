package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.HistoryFMBean
import cn.yuntk.radio.utils.Lg
import io.reactivex.Completable
import io.reactivex.Maybe

/**
 * Author : Gupingping
 * Date : 2018/8/7
 * Mail : gu12pp@163.com
 */
class HistoryViewMode : ViewModel() {
    val historyList = ObservableArrayList<FMBean>()

    //获取收听记录
    fun getHistoryFMBean(): Maybe<List<FMBean>> {
        Lg.e("HistoryViewMode getHistoryFMBean()")
        return Maybe.fromAction {
            Injection.getHistoryDao().getHistoryList().map {
                Lg.e("HistoryViewMode map==${it.fmBean!!.name}")
                historyList.add(it.fmBean!!)
                it.fmBean!!
            }
        }
    }

    fun saveHistory(fmBean: FMBean): Completable {
        return Completable.fromAction {
            val historyFMBean = HistoryFMBean()
            historyFMBean.fmBean = fmBean
            Injection.getHistoryDao().saveHistory(historyFMBean)
        }
    }


    fun removeAll(): Completable {
        return Completable.fromAction {
            Injection.getHistoryDao()
                    .clearHistory(Injection.getHistoryDao().getHistoryList())
        }
    }
}