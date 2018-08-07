package cn.yuntk.radio.db

import android.arch.persistence.room.*
import cn.yuntk.radio.bean.FMBean
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Author : Gupingping
 * Date : 2018/7/23
 * Mail : gu12pp@163.com
 * 收藏操作dao
 */

@Dao
interface CollectionFMBeanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDB(fmBean: FMBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<FMBean>)

    @Query("SELECT * FROM TB_FMBean ")
    fun getCollections(): Maybe<List<FMBean>>

    @Query("SELECT * FROM TB_FMBean WHERE name=:name")
    fun getFMBean(name: String): Single<FMBean>

    @Delete
    fun removeFromDB(fmBean: FMBean)

    @Delete
    fun removeList(list: List<FMBean>)
}

/*当数据库中没有user，查询没有返回行时，Single触发onError（EmptyResultSetException.class）。
当数据库中有一个user时， Single触发onSuccess。
如果在Single.onComplete调用之后user被更新，什么也不会发生*/

/*关于Maybe
当数据库中没有user，查询没有返回行时，Maybe调用complete。
当数据库中有一个user时，Maybe将触发onSuccess并调用complete。
如果在Maybe的complete调用之后user被更新，什么也不会发生*/

/*当数据库中没有user，查询没有返回行时，Flowable不会发射，也不会触发onNext, 或者 onError。
当数据库中有一个user时，Flowable会触发onNext。
每当user更新之后，Flowable将自动发射，这样你就可以根据最新的数据来更新UI*/
