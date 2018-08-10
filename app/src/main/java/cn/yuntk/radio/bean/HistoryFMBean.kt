package cn.yuntk.radio.bean

import android.arch.persistence.room.*

import cn.yuntk.radio.db.Converters
import org.jetbrains.annotations.NotNull

/**
 * Author : Gupingping
 * Date : 2018/7/30
 * Mail : gu12pp@163.com
 */
@Entity(tableName = "HISTORY_FMBEAN")
@TypeConverters(Converters::class)
class HistoryFMBean(@PrimaryKey(autoGenerate = false)
                    @NotNull
                    @ColumnInfo(name = "HistoryName")
                    var name: String = "null") {

    var time: Long = System.currentTimeMillis()
    @Embedded
    var fmBean: FMBean? = null

}
