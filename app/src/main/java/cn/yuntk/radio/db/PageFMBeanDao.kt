package cn.yuntk.radio.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import cn.yuntk.radio.bean.PageFMBean
import io.reactivex.Maybe
import org.jetbrains.annotations.NotNull

/**
 * Author : Gupingping
 * Date : 2018/7/30
 * Mail : gu12pp@163.com
 */
@Dao
interface PageFMBeanDao {

    //保存页面内的FMBean
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(pageFMBean: List<PageFMBean>)

    //查询当前页面的FMBan
    @Query("SELECT * FROM PAGE_FMBEAN WHERE page=:page")
    fun getListByPage(@NotNull page: String): Maybe<List<PageFMBean>>

}
