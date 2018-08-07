package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.PageFMBean
import cn.yuntk.radio.db.PageFMBeanDao
import io.reactivex.Completable
import io.reactivex.Maybe

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 */
class PageViewModel(private val dao: PageFMBeanDao) : ViewModel() {

    fun saveList(pageName: String, list: List<FMBean>): Completable {

        val pageList = mutableListOf<PageFMBean>()
        list.forEach {
            val pageFMBean = PageFMBean()
            pageFMBean.page = pageName
            pageFMBean.fmBean = it
            pageList.add(pageFMBean)
        }
        return Completable.fromAction {
            dao.insertList(pageList)
        }
    }

    fun getListByPage(pageName: String): Maybe<List<PageFMBean>> {
        return dao.getListByPage(pageName)
    }
}