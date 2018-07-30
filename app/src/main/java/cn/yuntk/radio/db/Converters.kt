package cn.yuntk.radio.db

import android.arch.persistence.room.TypeConverter
import android.text.TextUtils

/**
 * 因为sql中不能插入数组这样类型数据，就需要使用到了类型转换器了,这里使用了TypeConverters
 * Author : Gupingping
 * Date : 2018/7/23
 * Mail : gu12pp@163.com
 */
class Converters {
    @TypeConverter
    open fun arrayToString(array: Array<String>): String {
        if (array.isEmpty()) {
            return ""
        }

        val builder = StringBuilder(array[0])
        for (i in 1 until array.size) {
            builder.append(",").append(array[i])
        }
        return builder.toString()
    }

    @TypeConverter
    open fun StringToArray(value: String): Array<String>? {
        return if (TextUtils.isEmpty(value)) null else value.split(",").toTypedArray()

    }
}