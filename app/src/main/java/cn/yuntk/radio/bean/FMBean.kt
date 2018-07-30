package cn.yuntk.radio.bean

import android.arch.persistence.room.*
import cn.yuntk.radio.db.Converters
import java.io.Serializable

/**
 * Author : Gupingping
 * Date : 2018/7/11
 * Mail : gu12pp@163.com
 */
@Entity(tableName = "TB_FMBean")
@TypeConverters(Converters::class)
data class FMBean(@PrimaryKey(autoGenerate = false)
                  var name: String) : Serializable {

    /**
     * radioId : 74
     * name : CNR中国之声
     * parentId : 73
     * radioFm : FM106.1
     * radioUrl : http://audio1.china-plus.net:31080/10.102.62.44/radios/100001/index_100001.m3u8
     * backUrl : null
     * state : 1
     * sort : 1
     * level : 3
     * isExisUrl : 1
     * addTime : null
     * updateTime : 1503572700000
     */

    var radioId: Int = 0
    var parentId: Int = 0
    var radioFm: String? = null
    var radioUrl: String? = null
    var backUrl: String? = null
    var state: Int = 0
    var sort: Int = 0
    var level: Int = 0
    @Ignore
    var isExisUrl: Int = 0
    @Ignore
    var addTime: Any? = null
    var updateTime: Long = 0
    var isSelected: Boolean = false
    var isPlaying: Boolean = false
    override fun toString(): String {
        return "FMBean(name='$name', radioId=$radioId, parentId=$parentId, radioFm=$radioFm, radioUrl=$radioUrl, backUrl=$backUrl, state=$state, sort=$sort, level=$level, isExisUrl=$isExisUrl, addTime=$addTime, updateTime=$updateTime, isSelected=$isSelected, isPlaying=$isPlaying)"
    }

}
