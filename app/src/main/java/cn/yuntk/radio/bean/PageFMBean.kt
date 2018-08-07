package cn.yuntk.radio.bean

import android.arch.persistence.room.*

import cn.yuntk.radio.db.Converters
import org.jetbrains.annotations.NotNull

/**
 * Author : Gupingping
 * Date : 2018/7/30
 * Mail : gu12pp@163.com
 */
@Entity(tableName = "PAGE_FMBEAN")
@TypeConverters(Converters::class)
class PageFMBean(@PrimaryKey(autoGenerate = true)
                 var id: Int = 0) {

    @ColumnInfo
    var page: String? = null
    @Embedded
    var fmBean: FMBean? = null
}
