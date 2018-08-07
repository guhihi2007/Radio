package cn.yuntk.radio.bean

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
class ChannelBean(var name: String?, var chanelCode: String?,var resId:Int) {

    var newVisible: Boolean = false

    override fun toString(): String {
        return "ChannelBean{" +
                "name='" + name + '\''.toString() +
                ", chanelCode='" + chanelCode + '\''.toString() +
                '}'.toString()
    }
}
