package cn.yuntk.radio.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.*

/**
 * Author : Gupingping
 * Date : 2018/7/23
 * Mail : gu12pp@163.com
 */
class GsonUtils {
    companion object {
        private val gson = Gson()

        /**
         * 转成json
         *
         * @param object
         * @return
         */
        @JvmStatic
        fun GsonString(`object`: Any): String? {
            return gson.toJson(`object`)
        }

        /**
         * 解析jsonObject
         * @param jsonString
         * @param classes
         * @param <T>
         * @return
        </T> */
        @JvmStatic
        fun <T> parseObject(jsonString: String?, classes: Class<T>): T? {
            if (jsonString == null)
                return null
            var t: T? = null
            try {
                t = gson.fromJson(jsonString, classes)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }

            return t
        }

        /**
         * 解析jsonArray
         * @param jsonString
         * @param classes
         * @param <T>
         * @return
        </T> */
        @JvmStatic
        fun <T> parseArray(jsonString: String, classes: Class<Array<T>>): List<T> {
            val list = gson.fromJson(jsonString, classes)
            return Arrays.asList(*list)
        }
    }
}