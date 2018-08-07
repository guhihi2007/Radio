package cn.yuntk.radio.bean

/**
 * Author : Gupingping
 * Date : 2018/7/30
 * Mail : gu12pp@163.com
 */
data class AppInfoBean(var name: String, var value: String) {
    override fun toString(): String {
        return "AppInfoBean(name='$name', value='$value')"
    }
}