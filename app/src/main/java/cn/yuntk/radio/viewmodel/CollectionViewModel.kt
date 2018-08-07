package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.HistoryFMBean
import cn.yuntk.radio.db.CollectionFMBeanDao
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 */
class CollectionViewModel(private val dao: CollectionFMBeanDao) : ViewModel() {

    //查询数据库是否有保存这个频道
    fun isCollectionFMBean(fmBean: FMBean): Single<Boolean> {
        return dao.getFMBean(fmBean.name).map { it ->
            it == fmBean
        }
    }

    //添加至数据库
    fun addFMBeanToCollection(fmBean: FMBean): Completable {
        return Completable.fromAction {
            dao.insertDB(fmBean)
        }
    }

    //数据库中移除
    fun removeFMBeanFromCollection(fmBean: FMBean): Completable {
        return Completable.fromAction {
            dao.removeFromDB(fmBean)
        }
    }

    fun removeList(list: List<FMBean>): Completable {
        return Completable.fromAction {
            dao.removeList(list)
        }
    }

    //获取所有收藏
    fun getList(): Maybe<List<FMBean>> {
        return dao.getCollections()
    }

}