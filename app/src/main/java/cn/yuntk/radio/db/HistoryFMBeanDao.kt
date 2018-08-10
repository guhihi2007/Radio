package cn.yuntk.radio.db

import android.arch.persistence.room.*
import cn.yuntk.radio.bean.HistoryFMBean

import cn.yuntk.radio.bean.PageFMBean
import io.reactivex.Maybe
import org.jetbrains.annotations.NotNull

/**
 * Author : Gupingping
 * Date : 2018/7/30
 * Mail : gu12pp@163.com
 */
@Dao
interface HistoryFMBeanDao {

    //保存听过的FMBean
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveHistory(historyFMBean: HistoryFMBean)

    //查询当前页面的FMBan
    @Query("SELECT * FROM HISTORY_FMBEAN  ORDER BY time DESC ")
    fun getHistoryList(): List<HistoryFMBean>

    @Delete
    fun clearHistory(list: List<HistoryFMBean>)
}
